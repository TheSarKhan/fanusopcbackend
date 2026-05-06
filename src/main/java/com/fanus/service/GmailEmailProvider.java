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

    @Override
    public void sendPsychologistApplicationReceived(String to, String firstName) {
        send(to, "Fanus – Müraciətiniz Alındı", templateService.buildPsychologistApplicationReceived(firstName));
    }

    @Override
    public void sendPsychologistApplicationAdminNotification(String adminEmail, String firstName, String lastName, String email) {
        send(adminEmail, "Fanus – Yeni Psixoloq Müraciəti", templateService.buildPsychologistApplicationAdminNotification(firstName, lastName, email));
    }

    @Override
    public void sendPsychologistApproved(String to, String firstName) {
        send(to, "Fanus – Müraciətiniz Təsdiqləndi", templateService.buildPsychologistApproved(firstName));
    }

    @Override
    public void sendPsychologistRejected(String to, String firstName, String adminNote) {
        send(to, "Fanus – Müraciətiniz Haqqında Bildiriş", templateService.buildPsychologistRejected(firstName, adminNote));
    }

    @Override
    public void sendAppointmentReceived(String to, String firstName, String requestedPsychologist, java.time.LocalDateTime requestedStartAt) {
        send(to, "Fanus – Müraciətiniz qəbul edildi",
            templateService.buildAppointmentReceived(firstName, requestedPsychologist, requestedStartAt));
    }

    @Override
    public void sendAppointmentAssigned(String to, String firstName, String psychologistName, java.time.LocalDateTime startAt, String sessionFormat) {
        send(to, "Fanus – Randevunuz təyin edildi",
            templateService.buildAppointmentAssigned(firstName, psychologistName, startAt, sessionFormat));
    }

    @Override
    public void sendAppointmentAssignedPsychologist(String to, String firstName, String patientName, java.time.LocalDateTime startAt, String note) {
        send(to, "Fanus – Yeni randevu təyin edildi",
            templateService.buildAppointmentAssignedPsychologist(firstName, patientName, startAt, note));
    }

    @Override
    public void sendAppointmentConfirmed(String to, String firstName, String psychologistName, java.time.LocalDateTime startAt) {
        send(to, "Fanus – Randevunuz təsdiqləndi",
            templateService.buildAppointmentConfirmed(firstName, psychologistName, startAt));
    }

    @Override
    public void sendAppointmentRejected(String to, String firstName, String adminNote) {
        send(to, "Fanus – Randevunuza yenidən baxılır",
            templateService.buildAppointmentRejected(firstName, adminNote));
    }

    @Override
    public void sendAppointmentCancelled(String to, String firstName, String otherPartyName, java.time.LocalDateTime startAt, String cancelledBy) {
        send(to, "Fanus – Randevu ləğv edildi",
            templateService.buildAppointmentCancelled(firstName, otherPartyName, startAt, cancelledBy));
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
