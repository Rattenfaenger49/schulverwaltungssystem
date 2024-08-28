package com.school_system.service.impl;


import com.school_system.common.ResponseObject;
import com.school_system.config.Databases.TenantContext;
import com.school_system.config.MyAppProperties;
import com.school_system.dto.request.*;
import com.school_system.dto.response.StudentResponse;
import com.school_system.dto.response.TeacherResponse;
import com.school_system.dto.response.UserFullNameResponse;
import com.school_system.dto.response.UserResponse;
import com.school_system.entity.school.*;
import com.school_system.entity.security.*;
import com.school_system.enums.authentication.UserType;
import com.school_system.enums.school.ContractType;
import com.school_system.enums.school.FileCategory;
import com.school_system.entity.school.ClientInfo;
import com.school_system.exception.RateLimitingException;
import com.school_system.init.ProfilesLoader;
import com.school_system.mapper.Mapper;
import com.school_system.repository.*;
import com.school_system.service.*;
import com.school_system.util.UserUtils;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.school_system.util.EmailTemplateDataBuilder.buildRegistragionEmailVariables;
import static com.school_system.util.EmailTemplateDataBuilder.buildResetPasswordEmailVariables;


@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    // TODO use the services
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final AddressService addressService;
    private final TeacherRepository teacherRepository;
    private final TeacherService teacherService;
    private final StudentRepository studentRepository;
    private final LessonRepository lessonRepository;
    private final StudentService studentService;
    private final EmailService emailService;
    private final HttpServletResponse httpServletResponse;
    private final PDFCreaterService pdfCreaterService;
    private final BankDataRepository bankDataRepository;
    private final FileMetaDataRepository fileMetaDataRepository;
    private final MyAppProperties myAppProperties;
    private final SecurityService securityService;
    private final ProfilesLoader profilesLoader;

    private final LoginAttempServiceImpl loginAttempServiceImpl;
    private Environment env;


    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Override
    @Transactional
    public ResponseObject<UserResponse> registerUser(UserRegistrationRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseObject.<UserResponse>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message(String.format("E-Mail %s existiert bereits.", request.getUsername()))
                    .build();
        }

        Set<Role> role = new HashSet<>();
        role.add(roleService.getRoleByUserType(UserType.ADMIN));

        Address address = addressService.getExistingOrSaveNewAddress(request.getAddress());
        User user = User.builder()
                .gender(request.getGender())
                .firstName(request.getFirstName())
                .username(request.getUsername())
                .lastName(request.getLastName())
                .birthdate(request.getBirthdate())
                .roles(role)
                .address(address)
                .password("CREATED_BUT_NOT_VERIFIED")
                .enabled(false)
                .verified(false)
                .phoneNumber(request.getPhoneNumber())
                .build();

        userRepository.save(user);

        generateAndSendConfirmationToken(user);

        return ResponseObject.<UserResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(Mapper.toUserResponse(user))
                .message("Ihre Anfrage zur Registrierung wurde abgeschlossen.")
                .build();
    }

    @Override
    public ResponseObject<String> resetPassword(ResetPasswordRequest resetPasswordRequest){
        boolean b = loginAttempServiceImpl.isBlocked("RP-" + resetPasswordRequest.getUsername());
        log.info("blocked: {}", b);
        if(b){
            throw new RateLimitingException("Sie haben bereits eine E-Mail zur Zurücksetzung Ihres Passworts angefordert." +
                    " Bitte warten Sie einige Minuten, bevor Sie es erneut versuchen.");
        };
        // Email ist nicht gültig not important in this case
        String username = resetPasswordRequest.getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Benutzer nicht gefunden."));

        if (!user.isEnabled() || !user.isAccountNonLocked()) {
            return ResponseObject.<String>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Ihr Konto ist entweder nicht aktiviert oder gesperrt. Bitte kontaktieren Sie uns!")
                    .build();
        }
        ConfirmationToken confirmationToken = generateConfirmationToken(user);
        ClientInfo clientInfo = profilesLoader.getClientInfo();
        var logo = clientInfo.getLogoAsByteArray();

        String link = String.format("%s/confirm?token=%s&tenantId=%s",env.getProperty("domain.name") , confirmationToken.getToken() , TenantContext.getCurrentTenant());

        var map = buildResetPasswordEmailVariables(user, link, clientInfo);
        emailService.sendEmail(user.getUsername(), "Passwort Zurücksetzen","resetPasswordTemplate.html", map, logo, null);

        return ResponseObject.<String>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .message("Ihre Anfrage zum Zurücksetzen des Passworts wurde erfolgreich bearbeitet." +
                        " Falls Ihre E-Mail-Adresse in unserem System hinterlegt ist," +
                        " erhalten Sie in Kürze eine E-Mail mit einem Link zum Zurücksetzen " +
                        "Ihres Passworts. Bitte überprüfen Sie Ihren Posteingang und ggf." +
                        " Ihren Spam-Ordner. ")
                .build();
    }

    private ConfirmationToken generateConfirmationToken(User user) {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(3))
                .user(user)
                .build();
        return confirmationTokenRepository.save(confirmationToken);
    }

    @Override
    @Transactional
    @CacheEvict(value = "teachers", key = "'names'",  condition = "false")
    public ResponseObject<TeacherResponse> registerTeacher(TeacherRegistrationRequest request) throws URISyntaxException, MessagingException, IOException {

        if (userRepository.existsByUsername(request.getUsername())) {

            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseObject.<TeacherResponse>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message(String.format("Email %s existiert bereit", request.getUsername()))
                    .build();
        }
        Set<Role> role = new HashSet<>();
        role.add(roleService.getRoleByUserType(UserType.TEACHER));

        // check if Address exist and save the address if not exist
        Address address = addressService.getExistingOrSaveNewAddress(request.getAddress());

        Teacher teacher = Teacher.builder()
                .gender(request.getGender())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .birthdate(request.getBirthdate())
                .username(request.getUsername())
                .password("CREATED_BUT_NOT_VERIFIED")
                .phoneNumber(request.getPhoneNumber())
                .roles(role)
                .education(request.getEducation())
                .qualifications(request.getQualifications())
                .taxId(request.getTaxId())
                .address(address)
                .singleLessonCost(request.getSingleLessonCost())
                .groupLessonCost(request.getGroupLessonCost())
                .build();
        Teacher teacher1 = teacherRepository.save(teacher);
        generateAndSendConfirmationToken(teacher);


        return ResponseObject.<TeacherResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(Mapper.toTeacherResponse(teacher1))
                .message("Ihre Anfrage zur Registrierung wurde abgeschlossen.")
                .build();
    }

    @Override
    @Transactional
    @CacheEvict(value = "students", key = "'names'",  condition = "false")
    public ResponseObject<StudentResponse> registerStudent(StudentRegistrationRequest request) throws URISyntaxException, MessagingException, IOException {

        if (userRepository.existsByUsername(request.getUsername())) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseObject.<StudentResponse>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message(String.format("Email (%s) existiert bereit", request.getUsername()))
                    .build();
        }

        Role role = roleService.getRoleByUserType(UserType.STUDENT);

        // check if Address exist and save the address if not exist
        Address address = addressService.getExistingOrSaveNewAddress(request.getAddress());

        Parent parent = null;
        if (request.getParent() != null) {
            parent = new Parent();
            BeanUtils.copyProperties(request.getParent(), parent);
        }

        Student student = Student.builder()
                .gender(request.getGender())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .birthdate(request.getBirthdate())
                .username(request.getUsername())
                .password("CREATED_BUT_NOT_VERIFIED")
                .phoneNumber(request.getPhoneNumber())
                .roles(Collections.singleton(role))
                .address(address)
                .portalAccess(request.isPortalAccess())
                .level(request.getLevel())
                .parent(parent)
                .enabled(false)
                .verified(false)
                .build();
        Student student1 = studentRepository.save(student);

        if (student.isPortalAccess()) {
            generateAndSendConfirmationToken(student);
        }


        return ResponseObject.<StudentResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(Mapper.toStudentResponse(student1))
                .message("Ihre Anfrage zur Registrierung wurde abgeschlossen.")
                .build();

    }

    @Override
    public ResponseObject<byte[]> generateDocumentation(Long userId, ContractType contractType, String startDate, String endDate) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Keine Benutzer gefunden.")
        );
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<Lesson> lessons = new ArrayList<>();
        boolean isStudent = false;
        if (user instanceof Teacher) {

            lessons = getTeacherLessons(userId, start.atStartOfDay(), end.atStartOfDay(), contractType);


        } else if (user instanceof Student) {
            lessons = getStudentLessons(user.getId(), start.atStartOfDay(), end.atStartOfDay(), contractType);
            isStudent = true;

        }else {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseObject.<byte[]>builder()
                    .message("Benutzer ID ist nicht korrekt!")
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .data(null)
                    .build();
        }
        if (lessons.isEmpty()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseObject.<byte[]>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Keine Unterrichte gefunden!")
                    .build();
        }
        ClientInfo clientInfo = profilesLoader.getClientInfo();
        return pdfCreaterService.createDocumentation(lessons, start, end, isStudent, clientInfo);



    }

    private List<Lesson> getStudentLessons(Long studentId, LocalDateTime startDate,
                                           LocalDateTime endDate, ContractType contractType) {
        List<Lesson> lessons = lessonRepository.findByStudentsIdAndStartAtBetweenOrderByStartAtAsc(studentId, startDate, endDate);
        if (!contractType.equals(ContractType.ANY)) {
            lessons = lessons.stream().filter(l -> l.getContractType().equals(contractType))
                    .toList();
        }
        return lessons;
    }

    private List<Lesson> getTeacherLessons(Long teacherId, LocalDateTime startDate,
                                           LocalDateTime endDate, ContractType contractType) {
        List<Lesson> lessons = lessonRepository.findByTeacherIdAndStartAtBetweenOrderByStartAtAsc(teacherId, startDate, endDate);

        if (contractType.equals(ContractType.ANY))
            return lessons;
        return lessons.stream().filter(lesson -> lesson.getContractType().equals(contractType)).toList();
    }

    @Override
    public ResponseObject<List<UserFullNameResponse>> getUsersSelectList(String token) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = securityService.isAdmin();
        boolean isTeacher = securityService.isTeacher();

        if (isAdmin) {
            List<UserFullNameResponse> userListResponses = userRepository.findStudentsSelectList();
            return ResponseObject.<List<UserFullNameResponse>>builder()
                    .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                    .data(userListResponses)
                    .build();
        } else if (isTeacher) {

            List<UserFullNameResponse> userListResponses = teacherRepository.findStudentsWithFullname(UserUtils.getUserId(authentication));
            return ResponseObject.<List<UserFullNameResponse>>builder()
                    .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                    .data(userListResponses)
                    .build();
        } else {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ResponseObject.<List<UserFullNameResponse>>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .data(null)
                    .message("Zugriff verweigert: Sie sind nicht befugt, diese Aktion auszuführen!")
                    .build();
        }

    }

    @Override
    @Transactional
    public ResponseObject<String> sendConfirmationTokenByUserId(Long userId){

        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Keine Benutzer gefunden."));

        confirmationTokenRepository.deleteByUser(user);

        generateAndSendConfirmationToken(user);

        return ResponseObject.<String>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .message("Die Bestätigung-E-Mail wurde erfolgreich versendet.")
                .build();
    }

    @Override
    public ResponseObject<BankData> saveBankData(BankData bankData) {

        User user = userRepository.findById(bankData.getUser().getId()).orElseThrow(
                () -> new EntityNotFoundException("Keine Benutzer gefunden.")
        );
        Optional<BankData> bankData1 = bankDataRepository.findByUser(user);
        bankData1.ifPresent(data -> bankData.setId(data.getId()));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        bankData.setUpdatedAt(LocalDateTime.now());
        bankData.setUpdatedBy(UserUtils.getUsername(authentication));
        bankDataRepository.save(bankData);
        bankData.setUser(null);
        return ResponseObject.<BankData>builder()
                .data(bankData)
                .message("Bankverbindung wurde erfolgreich gepspeichert!")
                .build();
    }

    @Override
    public ResponseObject<String> sendConfirmationToken(String token) throws MessagingException, URISyntaxException, IOException {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token)
                .orElseThrow(
                        () -> new EntityNotFoundException("Sitzung nicht gefunden.")
                );

        User user = confirmationToken.getUser();
        if (user == null) {
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return ResponseObject.<String>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Etwas ist schief gelaufen. Bitte kontaktieren Sie uns!")
                    .build();
        }
        generateAndSendConfirmationToken(user);
        confirmationTokenRepository.delete(confirmationToken);
        return ResponseObject.<String>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .message("Die Bestätigung-E-Mail wurde erfolgreich versendet.")
                .build();
    }

    @Override
    public ResponseObject<? extends UserResponse> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = securityService.isAdmin();
        boolean isTeacher = securityService.isTeacher();
        boolean isStudent = securityService.isStudent();
        Long id = UserUtils.getUserId(authentication);

        if (isAdmin) {
            User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Benutzer wurde nicht gefunden."));
            return ResponseObject.<UserResponse>builder()
                    .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                    .data(Mapper.toUserResponse(user))
                    .build();
        } else if (isTeacher) {
            return teacherService.getTeacherById(id);
        } else if (isStudent) {
            return studentService.getStudentById(id);
        }
        httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return ResponseObject.<StudentResponse>builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .data(null)
                .message("Zugriff verweigert: Sie sind nicht befugt, diese Aktion auszuführen!")
                .build();
    }

    @Override
    @Transactional
    public ResponseObject<? extends UserResponse> updateProfile(UserUpdateRequest userUpdateRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long id = UserUtils.getUserId(authentication);

        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Benutzer nicht gefunden."));
        BeanUtils.copyProperties(userUpdateRequest, user, "id", "address");

        if (!userUpdateRequest.getAddress().equals(user.getAddress())) {
            Address address = addressService.getExistingOrSaveNewAddress(userUpdateRequest.getAddress());
            user.setAddress(address);
        }

        userRepository.save(user);
        return ResponseObject.<UserResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(Mapper.toUserResponse(user))
                .build();
    }

    @Override
    @Transactional
    public ResponseObject<? extends UserResponse> deleteProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long id = UserUtils.getUserId(authentication);


        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Benutzer nicht gefunden."));
        String username = UserUtils.getUsername(authentication);
        user.deleteProfile();
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(username);
        userRepository.save(user);

        return ResponseObject.<UserResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(null)
                .message("Benutzer erfolgreich gelöscht.")
                .build();
    }

    @Override
    public ResponseObject<? extends UserResponse> accountSperren(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Benutzer nicht gefunden."));
        user.setEnabled(false);
        user.setAccountNonLocked(false);
        userRepository.save(user);
        return ResponseObject.<UserResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(Mapper.toUserResponse(user))
                .message("Das Benutzerkonto wurde gesperrt.")
                .build();
    }

    @Override
    public ResponseObject<BankData> getBankData(Long userId) {
        User user = new User();
        user.setId(userId);
        Optional<BankData> bankOpt = bankDataRepository.findByUser(user);
        BankData bankData = bankOpt.orElse(null);
        if(bankData != null) {
            bankData.setUser(user);
        }
        return ResponseObject.<BankData>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(bankOpt.orElse(null))
                .message("Anfrage erfolgreich beabeitet")
                .build();

    }

    @Override
    public ResponseObject<List<FileMetadata>> getPersonalFiles(Long userId) {
        List<FileMetadata> files = fileMetaDataRepository.findByEntityIdAndFileCategory(userId, FileCategory.PERSONAL_FILE);
        return ResponseObject.<List<FileMetadata>>builder()
                .data(files)
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .build();
    }



    private void generateAndSendConfirmationToken(User user) {

        ConfirmationToken confirmationToken = generateConfirmationToken(user);

        String confirmationLink = String.format("%s/confirm?token=%s&tenantId=%s",myAppProperties.getDomainName() , confirmationToken.getToken() , TenantContext.getCurrentTenant());
        ClientInfo clientInfo = profilesLoader.getClientInfo();
        var logo = clientInfo.getLogoAsByteArray();
        var map = buildRegistragionEmailVariables(user, confirmationLink, clientInfo);

        emailService.sendEmail(user.getUsername(),"Verifizierungsmail", "registrationEmailTemplate.html", map, logo,null );

    }


}
