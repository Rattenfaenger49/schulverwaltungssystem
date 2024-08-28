package com.school_system.service.impl;

import com.school_system.classes.Attachment;
import com.school_system.config.MyAppProperties;
import com.school_system.service.LoginAttemptService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@EnableAsync
@AllArgsConstructor
@Component
public class EmailService {


    private final JavaMailSender mailHTMLSender;
    private final MyAppProperties myAppProperties;
    private final LoginAttemptService loginAttemptService;


    @Async
    public void sendEmail(String to, String subject, String templateName, Map<String, String> variables, byte [] logo, List<Attachment> attachment) {
        try {
            MimeMessage message = mailHTMLSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // true for multipart

            message.setFrom(new InternetAddress("info@example.de"));
            message.setRecipients(MimeMessage.RecipientType.TO, to);
            message.setSubject(subject);

            String template = loadTemplate(templateName);
            String body = fillTemplate(template, variables);

            helper.setText(body, true); // true indicates that the body is HTML

            // Add logo if present
            if (logo != null ) {
                ByteArrayInputStream bis = new ByteArrayInputStream(logo);
                helper.addInline("logo", new ByteArrayDataSource(bis, "image/png"));
            } else {
                // TODO
                // Optional: Add a placeholder image or handle missing logo scenario

            }

            // Add attachment if present
            if (attachment != null && !attachment.isEmpty()) {
                attachment.forEach(a -> {
                    try {
                        helper.addAttachment(a.filename(), a.source());
                    } catch (MessagingException e) {
                        log.error("Failed to add attachment: {}", a.filename(), e);
                        sendErrorNotification(e.getMessage(), "Failed to add attachment from " + subject);
                    }
                });

            }
            mailHTMLSender.send(message);
            if(templateName.equals("resetPasswordTemplate.html")) {
                log.info("Mard Send Email to : {}", to);
                this.loginAttemptService.handleSuccessfulResetPassword(to);
            }
        } catch (Exception e) {
            log.error("Error sending email with subject [{}]: {}", subject, e.getMessage());
            sendErrorNotification(e.getMessage(), subject);
        }
    }


    private String loadTemplate(String templateName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(myAppProperties.getTemplatesPath() + templateName)));
    }
    private String fillTemplate(String template, Map<String, String> variables) {
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            template = template.replace("[" + entry.getKey() + "]", entry.getValue());
        }
        return template;
    }

    public void sendErrorNotification(String errorMsg, String subject) {
        try {
            MimeMessage message = mailHTMLSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false);

            message.setFrom(new InternetAddress("info@example.de"));
            message.setRecipients(MimeMessage.RecipientType.TO, "admin@example.de");
            message.setSubject("Error Notification");
            String body = String.format("An error occurred while sending an email with Subject[%s]: %s", subject, errorMsg);
            message.setContent(body, "text/plain; charset=utf-8");

            mailHTMLSender.send(message);
        } catch (Exception ex) {
            log.error("Failed to send error notification email: " + ex.getMessage());
        }
    }

}
