package com.fanus.service;

import java.time.LocalDateTime;

public interface EmailService {
    void sendVerificationEmail(String to, String firstName, String token);
    void sendWelcomeEmail(String to, String firstName);
    void sendPasswordResetEmail(String to, String firstName, String token);
    void sendOperatorCredentialsEmail(String to, String firstName, String tempPassword);
    void sendPsychologistApplicationReceived(String to, String firstName);
    void sendPsychologistApplicationAdminNotification(String adminEmail, String firstName, String lastName, String email);
    void sendPsychologistApproved(String to, String firstName);
    void sendPsychologistRejected(String to, String firstName, String adminNote);

    // ─── Appointment lifecycle ───────────────────────────────────────────────
    void sendAppointmentReceived(String to, String firstName, String requestedPsychologist, LocalDateTime requestedStartAt);
    void sendAppointmentAssigned(String to, String firstName, String psychologistName, LocalDateTime startAt, String sessionFormat);
    void sendAppointmentAssignedPsychologist(String to, String firstName, String patientName, LocalDateTime startAt, String note);
    void sendAppointmentConfirmed(String to, String firstName, String psychologistName, LocalDateTime startAt);
    void sendAppointmentRejected(String to, String firstName, String adminNote);
    void sendAppointmentCancelled(String to, String firstName, String otherPartyName, LocalDateTime startAt, String cancelledBy);
}
