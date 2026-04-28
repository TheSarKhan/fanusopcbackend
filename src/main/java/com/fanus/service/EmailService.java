package com.fanus.service;

public interface EmailService {
    void sendVerificationEmail(String to, String firstName, String token);
    void sendWelcomeEmail(String to, String firstName);
    void sendPasswordResetEmail(String to, String firstName, String token);
    void sendOperatorCredentialsEmail(String to, String firstName, String tempPassword);
}
