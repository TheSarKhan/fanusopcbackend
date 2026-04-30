package com.fanus.service;

public interface EmailService {
    void sendVerificationEmail(String to, String firstName, String token);
    void sendWelcomeEmail(String to, String firstName);
    void sendPasswordResetEmail(String to, String firstName, String token);
    void sendOperatorCredentialsEmail(String to, String firstName, String tempPassword);
    void sendPsychologistApplicationReceived(String to, String firstName);
    void sendPsychologistApplicationAdminNotification(String adminEmail, String firstName, String lastName, String email);
    void sendPsychologistApproved(String to, String firstName);
    void sendPsychologistRejected(String to, String firstName, String adminNote);
}
