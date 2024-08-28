package com.school_system.service.impl;

import com.school_system.classes.AttemptData;
import com.school_system.common.ResponseObject;
import com.school_system.config.properties.AuthenticationProperties;
import com.school_system.dto.CustomUserDetails;
import com.school_system.dto.request.AuthenticationRequest;
import com.school_system.dto.request.UpdatePasswordRequest;
import com.school_system.dto.response.AuthenticationResponse;
import com.school_system.entity.security.ConfirmationToken;
import com.school_system.entity.security.Token;
import com.school_system.entity.security.User;
import com.school_system.enums.authentication.TokenType;
import com.school_system.exception.RateLimitingException;
import com.school_system.exception.TokenException;
import com.school_system.init.CacheObjects;
import com.school_system.mapper.Mapper;
import com.school_system.repository.ConfirmationTokenRepository;
import com.school_system.repository.TokenRepository;
import com.school_system.repository.UserRepository;
import com.school_system.service.AuthenticationService;
import com.school_system.service.JwtService;
import com.school_system.service.LoginAttemptService;
import com.school_system.util.UserUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static com.school_system.util.TimeUtils.getBlockDuration;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HttpServletResponse httpServletResponse;
    private final AuthenticationProperties authenticationProperties;
    private final AuthenticationManager authenticationManager;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final LoginAttemptService loginAttemptService;

    public AuthenticationServiceImpl(JwtService jwtService,
                                     TokenRepository tokenRepository,
                                     UserRepository userRepository,
                                     LoginAttemptService loginAttemptService,
                                     @Lazy PasswordEncoder passwordEncoder,
                                     @Lazy AuthenticationProperties authenticationProperties,
                                     @Lazy AuthenticationManager authenticationManager,
                                     ConfirmationTokenRepository confirmationTokenRepository,
                                     HttpServletResponse httpServletResponse) {
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationProperties = authenticationProperties;
        this.authenticationManager = authenticationManager;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.httpServletResponse = httpServletResponse;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    @Transactional
    public ResponseEntity<AuthenticationResponse> login(AuthenticationRequest request, HttpServletResponse response) {

        try {

            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            ));
            loginAttemptService.handleSuccessfulLogin(request.getUsername());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(buildAuthenticationResponse((CustomUserDetails) authentication.getPrincipal(), response));
        } catch (BadCredentialsException  e) {
            AttemptData a = loginAttemptService.handleFailurLogin(request.getUsername());

            throw new BadCredentialsException(String
                    .format("Ungültiger Benutzername oder ungültiges Passwort.Sie haben noch %d Versuche übrig", (5 - ( a.attempts() % 5) )%5) );
        }

    }

    @Override
    @Transactional
    public AuthenticationResponse buildAuthenticationResponse(CustomUserDetails userDetails, HttpServletResponse response) {


        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        this.saveUserToken(userDetails, accessToken, TokenType.ACCESS_TOKEN);
        this.saveUserToken(userDetails, refreshToken, TokenType.REFRESH_TOKEN);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(authenticationProperties.getAccessToken().getExpirationInSeconds())
                .build();
    }

    @Override
    @Transactional
    public ResponseObject<String> confirmToken(String token, HttpServletResponse response) {

        validateConfirmationToke(token);

        return ResponseObject.<String>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .message("Token ist gültig.")
                .data(token)
                .build();
    }

    @Override
    @Transactional
    public AuthenticationResponse refreshToken(String refreshToken, HttpServletRequest httpRequest) {
        Jws<Claims> claimsJws = jwtService.validateToken(refreshToken, true);
        Claims body = claimsJws.getBody();
        String tenantIdHeader = httpRequest.getHeader("X-TenantID");
        String tenantIdPayload = (String) body.get("tenantId");
        if (tenantIdHeader == null || !tenantIdHeader.equals(tenantIdPayload)) {
            throw new JwtException("Die Sitzung ist ungültig. Bitte melden Sie sich erneut an.");
        }
        User user = userRepository.findByUsername((String) body.get("username")).orElseThrow(
                () -> new UsernameNotFoundException(String.format("Username %s not found", body.get("username")))
        );

        CustomUserDetails userDetails = Mapper.toCustomUserDetails(user);

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshTokenNew = jwtService.generateRefreshToken(userDetails);
        this.saveUserToken(userDetails, accessToken, TokenType.ACCESS_TOKEN);
        this.saveUserToken(userDetails, refreshTokenNew, TokenType.REFRESH_TOKEN);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(authenticationProperties.getAccessToken().getExpirationInSeconds())
                .build();
    }

    @Override
    @Transactional
    public ResponseObject<String> logout(HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = UserUtils.getUsername(authentication);
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(String.format("Username %s not found", username))
        );

        revokeUserToken(user, List.of(TokenType.ACCESS_TOKEN, TokenType.REFRESH_TOKEN));
        // CookiesUtility.removeResponseCookies(response, "access-token");
        // CookiesUtility.removeResponseCookies(response, "refresh-token");
        return ResponseObject.<String>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .message("Benutzer erfolgreich ausgeloggt.")
                .build();
    }

    @Override
    @Transactional
    public ResponseObject<String> setPassowrd(UpdatePasswordRequest updatePasswordRequest) {

        // TODO check for password strength
        ConfirmationToken confirmationToken = validateConfirmationToke(updatePasswordRequest.getConfirmationToken());

        if (!updatePasswordRequest.getConfirmationPassword().equals(updatePasswordRequest.getPassword())) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseObject.<String>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Passwörter stimmen nicht überein!")
                    .build();
        }
        Long id = confirmationToken.getUser().getId();
        User user = userRepository.findById(id).orElseThrow(() ->
                new UsernameNotFoundException("Username nicht gefunden, etwas ist schiefgelaufen. Bitte kontaktieren Sie uns!"));

        String newPassword = updatePasswordRequest.getPassword();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setVerified(true);
        user.setEnabled(true);
        userRepository.save(user);
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        confirmationTokenRepository.save(confirmationToken);
        return ResponseObject.<String>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .message("Passwort erfolgreich aktualisiert!")
                .build();
    }

    private ConfirmationToken validateConfirmationToke(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenException("Token nicht gefunden. Bitte fordern Sie einen neuen Token an."));

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenException("Token ist abgelaufen!");
        }
        if (confirmationToken.getConfirmedAt() != null) {
            // TODO check if password is set or account confirmed successfully
            throw new TokenException(
                    String.format("E-Mail: %s ist bereits bestätigt.",
                            confirmationToken.getUser().getUsername()));

        }
        return confirmationToken;
    }

    @Transactional
    public void revokeUserToken(User user, List<TokenType> tokenTypes) {

        tokenTypes.forEach(tt -> {
            List<Token> token = tokenRepository.findByUserIdAndTokenType(user.getId(), tt);
            token.forEach(t -> {
                t.setRevoked(true);
            });
            tokenRepository.saveAll(token);
        });
    }

    private void saveUserToken(CustomUserDetails userDetails, @NotBlank String token, TokenType tokenType) {
        User user = User.builder()
                .id(userDetails.getId())
                .build();
        Token t = Token.builder()
                .token(token)
                .tokenType(tokenType)
                .user(user)
                .revoked(false)
                .build();

        tokenRepository.save(t);
    }
}
