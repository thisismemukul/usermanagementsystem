package com.user.management.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.HashMap;
import java.util.Map;

import static com.user.management.enums.ResponseCode.INPUT_IS_INVALID;
import static com.user.management.util.UserManagementUtils.createValidationException;

@Service
@Slf4j
public class EmailService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender mailSender;
    private final FreeMarkerConfigurer freemarkerConfigurer;


    public EmailService(JavaMailSender mailSender, FreeMarkerConfigurer freemarkerConfigurer) {
        this.mailSender = mailSender;
        this.freemarkerConfigurer = freemarkerConfigurer;
    }

    public void sendEmail(String to, String subject, String message) throws MessagingException {
        if (!StringUtils.hasText(to) || !isValidEmail(to)) {
            log.error("Invalid input: 'to' email address is null, empty, or invalid.");
            throw createValidationException(INPUT_IS_INVALID);
        }

        if (!StringUtils.hasText(subject)) {
            log.error("Invalid input: Email subject is null or empty.");
            throw createValidationException(INPUT_IS_INVALID);
        }

        if (!StringUtils.hasText(message)) {
            log.error("Invalid input: Email message is null or empty.");
            throw createValidationException(INPUT_IS_INVALID);
        }

        Map<String, Object> model = new HashMap<>();
        model.put("resetUrl", message);
        String emailContent = getEmailContent(model);

        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("Password Reset Request");
        helper.setText(emailContent, true);

        try {
            mailSender.send(msg);
            log.info("Email successfully sent to {}", to);
        } catch (MailException e) {
            log.error("Failed to send email to {}. Error: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email. Please try again later.", e);
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    private String getEmailContent(Map<String, Object> model) throws MessagingException {
        try {
            String templateName = "resetPasswordEmailTemplate.html";
            return FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfigurer.getConfiguration().getTemplate(templateName), model);
        } catch (freemarker.template.TemplateNotFoundException e) {
            log.error("Template not found: {}", e.getMessage());
            throw new MessagingException("Template not found for email content generation", e);
        } catch (Exception e) {
            log.error("Error generating email content: {}", e.getMessage());
            throw new MessagingException("Error generating email content", e);
        }
    }

}
