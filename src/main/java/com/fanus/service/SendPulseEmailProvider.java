package com.fanus.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@ConditionalOnProperty(name = "app.email.provider", havingValue = "sendpulse", matchIfMissing = true)
@RequiredArgsConstructor
public class SendPulseEmailProvider implements EmailService {

    private final EmailTemplateService templateService;
    private final RestTemplate restTemplate;

    @Value("${app.email.sendpulse.client-id:}")
    private String clientId;

    @Value("${app.email.sendpulse.client-secret:}")
    private String clientSecret;

    @Value("${app.email.from:noreply@fanus.az}")
    private String fromAddress;

    @Value("${app.email.from-name:Fanus Psixologiya}")
    private String fromName;

    @Override
    public void sendVerificationEmail(String to, String firstName, String token) {
        sendHtml(to, firstName, "Fanus – Email Təsdiqi", templateService.buildVerificationEmail(firstName, token));
    }

    @Override
    public void sendWelcomeEmail(String to, String firstName) {
        sendHtml(to, firstName, "Fanus-a xoş gəldiniz!", templateService.buildWelcomeEmail(firstName));
    }

    @Override
    public void sendPasswordResetEmail(String to, String firstName, String token) {
        sendHtml(to, firstName, "Fanus – Şifrə Sıfırlama", templateService.buildPasswordResetEmail(firstName, token));
    }

    @Override
    public void sendOperatorCredentialsEmail(String to, String firstName, String tempPassword) {
        sendHtml(to, firstName, "Fanus – Operator Hesabı Yaradıldı",
                templateService.buildOperatorCredentialsEmail(firstName, to, tempPassword));
    }

    private void sendHtml(String to, String toName, String subject, String html) {
        if (clientId.isBlank() || clientSecret.isBlank()) {
            log.warn("SendPulse credentials not configured; email to {} skipped. Subject: {}", to, subject);
            return;
        }
        try {
            String accessToken = getAccessToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            Map<String, Object> body = Map.of(
                "email", Map.of(
                    "subject", subject,
                    "html", html,
                    "from", Map.of("name", fromName, "email", fromAddress),
                    "to", new Object[]{Map.of("name", toName, "email", to)}
                )
            );

            restTemplate.exchange(
                "https://api.sendpulse.com/smtp/emails",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                String.class
            );
            log.info("Email sent via SendPulse to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email via SendPulse to {}: {}", to, e.getMessage());
        }
    }

    private String getAccessToken() {
        Map<String, String> body = Map.of(
            "grant_type", "client_credentials",
            "client_id", clientId,
            "client_secret", clientSecret
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        @SuppressWarnings("unchecked")
        Map<String, Object> resp = restTemplate.postForObject(
            "https://api.sendpulse.com/oauth/access_token",
            new HttpEntity<>(body, headers),
            Map.class
        );
        if (resp == null || !resp.containsKey("access_token")) {
            throw new RuntimeException("SendPulse auth failed");
        }
        return (String) resp.get("access_token");
    }
}
