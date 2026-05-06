package com.fanus.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class EmailTemplateService {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.forLanguageTag("az"));

    private static String fmt(LocalDateTime dt) {
        return dt == null ? "—" : dt.format(DT_FMT);
    }

    private static String safe(String v) {
        return v == null || v.isBlank() ? "—" : v;
    }

    public String buildVerificationEmail(String firstName, String token) {
        String link = frontendUrl + "/verify?token=" + token;
        return wrap(
            "Email Ünvanınızı Təsdiqləyin",
            "<p>Salam, <strong>" + firstName + "</strong>!</p>" +
            "<p>Fanus platformasına qeydiyyatınız üçün təşəkkür edirik. " +
            "Hesabınızı fəallaşdırmaq üçün aşağıdakı düyməyə klikləyin:</p>",
            link, "Emailimi Təsdiqlə",
            "<p style='color:#6B7280;font-size:13px;'>Bu link 24 saat ərzində etibarlıdır. " +
            "Əgər qeydiyyatı siz etməmisinizsə, bu emaili nəzərə almayın.</p>"
        );
    }

    public String buildWelcomeEmail(String firstName) {
        String link = frontendUrl + "/patient/profile";
        return wrap(
            "Fanus-a Xoş Gəldiniz!",
            "<p>Salam, <strong>" + firstName + "</strong>!</p>" +
            "<p>Email ünvanınız təsdiqləndi. İndi psixoloqlarımızla tanış ola və " +
            "randevu ala bilərsiniz.</p>",
            link, "Profilimə keç",
            ""
        );
    }

    public String buildPasswordResetEmail(String firstName, String token) {
        String link = frontendUrl + "/reset-password?token=" + token;
        return wrap(
            "Şifrənizi Sıfırlayın",
            "<p>Salam, <strong>" + firstName + "</strong>!</p>" +
            "<p>Hesabınız üçün şifrə sıfırlama tələbi aldıq. " +
            "Yeni şifrə təyin etmək üçün aşağıdakı düyməyə klikləyin:</p>",
            link, "Şifrəni Sıfırla",
            "<p style='color:#6B7280;font-size:13px;'>Bu link 1 saat ərzində etibarlıdır. " +
            "Əgər bu tələbi siz göndərməmisinizsə, şifrəniz dəyişdirilməyəcək.</p>"
        );
    }

    public String buildOperatorCredentialsEmail(String firstName, String email, String tempPassword) {
        String link = frontendUrl + "/login";
        return wrap(
            "Operator Hesabınız Yaradıldı",
            "<p>Salam, <strong>" + firstName + "</strong>!</p>" +
            "<p>Fanus platformasında operator hesabınız yaradılmışdır. " +
            "Aşağıdakı məlumatlarla daxil ola bilərsiniz:</p>" +
            "<div style='background:#F3F4F6;border-radius:8px;padding:16px;margin:16px 0;'>" +
            "<p style='margin:4px 0;'><strong>Email:</strong> " + email + "</p>" +
            "<p style='margin:4px 0;'><strong>Müvəqqəti şifrə:</strong> " + tempPassword + "</p>" +
            "</div>" +
            "<p>Daxil olduqdan sonra şifrənizi dərhal dəyişdirməyinizi tövsiyə edirik.</p>",
            link, "Operator Panelinə Daxil Ol",
            ""
        );
    }

    public String buildPsychologistApplicationReceived(String firstName) {
        String link = frontendUrl + "/psycholog/status";
        return wrap(
            "Müraciətiniz Alındı",
            "<p>Salam, <strong>" + firstName + "</strong>!</p>" +
            "<p>Fanus platformasına psixoloq kimi qoşulmaq üçün müraciətiniz uğurla qəbul edildi.</p>" +
            "<p>Komandamız sənədlərinizi yoxlayacaq. Nəticə barədə sizə bildiriş göndəriləcək. " +
            "Adətən bu proses 2–5 iş günü ərzində tamamlanır.</p>",
            link, "Müraciətin Statusu",
            "<p style='color:#6B7280;font-size:13px;'>Suallarınız üçün bizimlə əlaqə saxlaya bilərsiniz.</p>"
        );
    }

    public String buildPsychologistApplicationAdminNotification(String firstName, String lastName, String email) {
        String link = frontendUrl + "/admin/applications";
        return wrap(
            "Yeni Psixoloq Müraciəti",
            "<p>Yeni psixoloq müraciəti daxil oldu:</p>" +
            "<div style='background:#F3F4F6;border-radius:8px;padding:16px;margin:16px 0;'>" +
            "<p style='margin:4px 0;'><strong>Ad Soyad:</strong> " + firstName + " " + lastName + "</p>" +
            "<p style='margin:4px 0;'><strong>Email:</strong> " + email + "</p>" +
            "</div>" +
            "<p>Admin panelindən müraciəti nəzərdən keçirin.</p>",
            link, "Admin Panelinə Keç",
            ""
        );
    }

    public String buildPsychologistApproved(String firstName) {
        String link = frontendUrl + "/psycholog/dashboard";
        return wrap(
            "Müraciətiniz Təsdiqləndi!",
            "<p>Salam, <strong>" + firstName + "</strong>!</p>" +
            "<p>Fanus platformasına psixoloq kimi qoşulmaq üçün müraciətiniz <strong>uğurla təsdiqləndi</strong>.</p>" +
            "<p>Artıq mövcud email və şifrənizlə psixoloq panelinə daxil ola bilərsiniz.</p>",
            link, "Panelinə Daxil Ol",
            "<p style='color:#6B7280;font-size:13px;'>Suallarınız üçün bizimlə əlaqə saxlaya bilərsiniz.</p>"
        );
    }

    public String buildPsychologistRejected(String firstName, String adminNote) {
        String link = frontendUrl + "/contact";
        String noteHtml = (adminNote != null && !adminNote.isBlank())
            ? "<div style='background:#FEF2F2;border-left:4px solid #EF4444;border-radius:4px;padding:12px 16px;margin:16px 0;'>" +
              "<p style='margin:0;color:#991B1B;font-size:14px;'><strong>Qeyd:</strong> " + adminNote + "</p>" +
              "</div>"
            : "";
        return wrap(
            "Müraciətiniz Qəbul Edilmədi",
            "<p>Salam, <strong>" + firstName + "</strong>!</p>" +
            "<p>Təəssüflə bildiririk ki, hazırda müraciətinizi qəbul edə bilmirik.</p>" +
            noteHtml +
            "<p>Ətraflı məlumat üçün bizimlə əlaqə saxlaya bilərsiniz.</p>",
            link, "Bizimlə Əlaqə",
            "<p style='color:#6B7280;font-size:13px;'>Gələcəkdə yenidən müraciət edə bilərsiniz.</p>"
        );
    }

    // ─── Appointment lifecycle templates ─────────────────────────────────────

    public String buildAppointmentReceived(String firstName, String psychologistName, LocalDateTime requestedStartAt) {
        String link = frontendUrl + "/patient/appointments";
        return wrap(
            "Müraciətiniz Qəbul Edildi",
            "<p>Salam, <strong>" + firstName + "</strong>!</p>" +
            "<p>Randevu müraciətiniz qəbul edildi. Operator komandamız tezliklə baxıb sizə geri dönəcək.</p>" +
            "<div style='background:#F3F4F6;border-radius:8px;padding:16px;margin:16px 0;'>" +
            "<p style='margin:4px 0;'><strong>Psixoloq:</strong> " + safe(psychologistName) + "</p>" +
            "<p style='margin:4px 0;'><strong>Üstün tutulan vaxt:</strong> " + fmt(requestedStartAt) + "</p>" +
            "</div>",
            link, "Randevularıma bax",
            "<p style='color:#6B7280;font-size:13px;'>Adətən cavab müddəti 2–4 saatdır.</p>"
        );
    }

    public String buildAppointmentAssigned(String firstName, String psychologistName, LocalDateTime startAt, String sessionFormat) {
        String link = frontendUrl + "/patient/appointments";
        String formatLabel = "ONLINE".equals(sessionFormat) ? "Online" : "IN_PERSON".equals(sessionFormat) ? "Üzbəüz" : "—";
        return wrap(
            "Randevunuz Təyin Edildi",
            "<p>Salam, <strong>" + firstName + "</strong>!</p>" +
            "<p>Operator randevunuza baxıb psixoloq təyin etdi:</p>" +
            "<div style='background:#F3F4F6;border-radius:8px;padding:16px;margin:16px 0;'>" +
            "<p style='margin:4px 0;'><strong>Psixoloq:</strong> " + safe(psychologistName) + "</p>" +
            "<p style='margin:4px 0;'><strong>Tarix:</strong> " + fmt(startAt) + "</p>" +
            "<p style='margin:4px 0;'><strong>Format:</strong> " + formatLabel + "</p>" +
            "</div>" +
            "<p>Psixoloq vaxtı təsdiqlədikdən sonra sizə yenidən bildiriş gələcək.</p>",
            link, "Detallara bax", ""
        );
    }

    public String buildAppointmentAssignedPsychologist(String firstName, String patientName, LocalDateTime startAt, String note) {
        String link = frontendUrl + "/psycholog/appointments";
        return wrap(
            "Yeni Randevu Sizə Təyin Edildi",
            "<p>Salam, <strong>" + firstName + "</strong>!</p>" +
            "<p>Operator sizə yeni randevu təyin etdi:</p>" +
            "<div style='background:#F3F4F6;border-radius:8px;padding:16px;margin:16px 0;'>" +
            "<p style='margin:4px 0;'><strong>Müştəri:</strong> " + safe(patientName) + "</p>" +
            "<p style='margin:4px 0;'><strong>Tarix:</strong> " + fmt(startAt) + "</p>" +
            "<p style='margin:4px 0;'><strong>Qeyd:</strong> " + safe(note) + "</p>" +
            "</div>" +
            "<p>Panelə daxil olaraq randevunu təsdiqləyə və ya rədd edə bilərsiniz.</p>",
            link, "Panelə keç", ""
        );
    }

    public String buildAppointmentConfirmed(String firstName, String psychologistName, LocalDateTime startAt) {
        String link = frontendUrl + "/patient/appointments";
        return wrap(
            "Randevunuz Təsdiqləndi",
            "<p>Salam, <strong>" + firstName + "</strong>!</p>" +
            "<p>Psixoloq " + safe(psychologistName) + " randevunuzu təsdiqlədi.</p>" +
            "<div style='background:#ECFDF5;border-left:4px solid #10B981;border-radius:4px;padding:12px 16px;margin:16px 0;'>" +
            "<p style='margin:0;color:#065F46;'><strong>Tarix:</strong> " + fmt(startAt) + "</p>" +
            "</div>",
            link, "Detallara bax",
            "<p style='color:#6B7280;font-size:13px;'>Seansa 5 dəqiqə əvvəl hazır olun.</p>"
        );
    }

    public String buildAppointmentRejected(String firstName, String adminNote) {
        String link = frontendUrl + "/patient/appointments";
        String noteHtml = (adminNote != null && !adminNote.isBlank())
            ? "<div style='background:#FEF2F2;border-left:4px solid #EF4444;border-radius:4px;padding:12px 16px;margin:16px 0;'>" +
              "<p style='margin:0;color:#991B1B;'><strong>Qeyd:</strong> " + adminNote + "</p>" +
              "</div>"
            : "";
        return wrap(
            "Randevunuza Yenidən Baxılır",
            "<p>Salam, <strong>" + firstName + "</strong>!</p>" +
            "<p>Təyin olunan psixoloq randevunuzu qəbul edə bilmədi. Operator komandamız sizə uyğun başqa psixoloq təyin edəcək.</p>" +
            noteHtml,
            link, "Randevularıma bax", ""
        );
    }

    public String buildAppointmentCancelled(String firstName, String otherParty, LocalDateTime startAt, String cancelledBy) {
        String link = frontendUrl + "/patient/appointments";
        String byLabel = "PATIENT".equals(cancelledBy) ? "müştəri tərəfindən" : "operator tərəfindən";
        return wrap(
            "Randevu Ləğv Edildi",
            "<p>Salam, <strong>" + firstName + "</strong>!</p>" +
            "<p>Aşağıdakı randevu " + byLabel + " ləğv edildi:</p>" +
            "<div style='background:#F3F4F6;border-radius:8px;padding:16px;margin:16px 0;'>" +
            "<p style='margin:4px 0;'><strong>Tərəf:</strong> " + safe(otherParty) + "</p>" +
            "<p style='margin:4px 0;'><strong>Tarix:</strong> " + fmt(startAt) + "</p>" +
            "</div>",
            link, "Detallara bax", ""
        );
    }

    private String wrap(String title, String body, String ctaLink, String ctaText, String footer) {
        return """
            <!DOCTYPE html>
            <html lang="az">
            <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"></head>
            <body style="margin:0;padding:0;background:#F3F4F6;font-family:sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0" style="padding:32px 0;">
                <tr><td align="center">
                  <table width="560" cellpadding="0" cellspacing="0"
                         style="background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 2px 12px rgba(0,0,0,.08);">
                    <!-- Header -->
                    <tr>
                      <td style="background:linear-gradient(135deg,#002147,#5A4FC8);padding:28px 40px;text-align:center;">
                        <span style="color:#fff;font-size:28px;font-weight:700;letter-spacing:1px;">Fanus</span>
                        <p style="color:rgba(255,255,255,.75);margin:4px 0 0;font-size:13px;">Psixologiya Platforması</p>
                      </td>
                    </tr>
                    <!-- Title -->
                    <tr>
                      <td style="padding:32px 40px 0;">
                        <h2 style="margin:0;color:#1A2535;font-size:20px;">%s</h2>
                      </td>
                    </tr>
                    <!-- Body -->
                    <tr>
                      <td style="padding:16px 40px;color:#374151;font-size:15px;line-height:1.6;">
                        %s
                      </td>
                    </tr>
                    <!-- CTA -->
                    <tr>
                      <td style="padding:8px 40px 24px;text-align:center;">
                        <a href="%s"
                           style="display:inline-block;background:linear-gradient(135deg,#002147,#5A4FC8);
                                  color:#fff;text-decoration:none;padding:14px 32px;
                                  border-radius:8px;font-size:15px;font-weight:600;">
                          %s
                        </a>
                      </td>
                    </tr>
                    <!-- Footer -->
                    <tr>
                      <td style="padding:0 40px 32px;">
                        %s
                      </td>
                    </tr>
                    <!-- Bottom bar -->
                    <tr>
                      <td style="background:#F9FAFB;padding:20px 40px;text-align:center;
                                 color:#9CA3AF;font-size:12px;border-top:1px solid #E5E7EB;">
                        © 2025 Fanus Psixologiya Platforması · fanus.com
                      </td>
                    </tr>
                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(title, body, ctaLink, ctaText, footer);
    }
}
