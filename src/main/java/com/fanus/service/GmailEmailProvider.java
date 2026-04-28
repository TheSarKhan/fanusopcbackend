package com.fanus.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "app.email.provider", havingValue = "gmail")
@RequiredArgsConstructor
public class GmailEmailProvider implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailTemplateService templateService;

    @Value("${app.email.from:noreply@fanus.az}")
    private String fromAddress;

    @Override
    public void sendVerificationEmail(String to, String firstName, String token) {
        send(to, "Fanus – Email Təsdiqi", templateService.buildVerificationEmail(firstName, token));
    }

    @Override
    public void sendWelcomeEmail(String to, String firstName) {
        send(to, "Fanus-a xoş gəldiniz!", templateService.buildWelcomeEmail(firstName));
    }

    @Override
    public void sendPasswordResetEmail(String to, String firstName, String token) {
        send(to, "Fanus – Şifrə Sıfırlama", templateService.buildPasswordResetEmail(firstName, token));
    }

    @Override
    public void sendOperatorCredentialsEmail(String to, String firstName, String tempPassword) {
        send(to, "Fanus – Operator Hesabı Yaradıldı", templateService.buildOperatorCredentialsEmail(firstName, to, tempPassword));
    }

    private void send(String to, String subject, String html) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(msg);
            log.info("Email sent via Gmail to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email via Gmail to {}: {}", to, e.getMessage());
        }
    }
}
