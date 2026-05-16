package com.fixsetgo.lead;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class LeadNotificationService {

    private static final Logger log = LoggerFactory.getLogger(LeadNotificationService.class);
    private static final String LOGO_CID = "fsgLogo";
    private static final String BANNER_CID = "fsgBanner";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a").withZone(ZoneId.of("Asia/Kolkata"));

    private final JavaMailSender mailSender;
    private final String recipientEmail;
    private final String senderEmail;
    private final String brandName;
    private final String brandTagline;
    private final String websiteUrl;

    public LeadNotificationService(
            JavaMailSender mailSender,
            @Value("${app.leads.notification.to}") String recipientEmail,
            @Value("${app.leads.notification.from}") String senderEmail,
            @Value("${app.leads.brand.name:Fix Set Go}") String brandName,
            @Value("${app.leads.brand.tagline:Your All-In-One Solution Partner}") String brandTagline,
            @Value("${app.leads.brand.website:https://fixsetgo.com}") String websiteUrl
    ) {
        this.mailSender = mailSender;
        this.recipientEmail = recipientEmail;
        this.senderEmail = senderEmail;
        this.brandName = brandName;
        this.brandTagline = brandTagline;
        this.websiteUrl = websiteUrl;
    }

    public void sendLeadNotification(Lead lead) {
        try {
            sendAdminLeadEmail(lead);
        } catch (MailException | MessagingException exception) {
            throw new IllegalStateException("Lead saved, but email delivery failed. Check SMTP configuration.", exception);
        }

        try {
            sendClientAcknowledgement(lead);
        } catch (MailException | MessagingException exception) {
            log.warn("Lead acknowledgement email could not be sent to {}", lead.getEmail(), exception);
        }
    }

    private void sendAdminLeadEmail(Lead lead) throws MessagingException {
        MimeMessageHelper helper = createHtmlMessage(
                resolveRecipientEmail(),
                "New Lead: " + lead.getService() + " | " + lead.getName(),
                lead.getEmail()
        );
        helper.setText(buildAdminHtml(lead), true);
        addInlineBrandAssets(helper);
        mailSender.send(helper.getMimeMessage());
    }

    private void sendClientAcknowledgement(Lead lead) throws MessagingException {
        String clientEmail = normalize(lead.getEmail());
        if (!isValidEmail(clientEmail)) {
            return;
        }

        MimeMessageHelper helper = createHtmlMessage(
                clientEmail,
                "We Received Your Request | " + brandName,
                null
        );
        helper.setText(buildClientHtml(lead), true);
        addInlineBrandAssets(helper);
        mailSender.send(helper.getMimeMessage());
    }

    private MimeMessageHelper createHtmlMessage(String to, String subject, String replyTo) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(mailSender.createMimeMessage(), true, "UTF-8");
        helper.setTo(to);
        helper.setFrom(resolveSenderEmail());
        if (isValidEmail(replyTo)) {
            helper.setReplyTo(normalize(replyTo));
        }
        helper.setSubject(subject);
        return helper;
    }

    private void addInlineBrandAssets(MimeMessageHelper helper) throws MessagingException {
        helper.addInline(LOGO_CID, new ClassPathResource("email-assets/fsg-logo-circle.png"));
        helper.addInline(BANNER_CID, new ClassPathResource("email-assets/fsg-banner.png"));
    }

    private String buildAdminHtml(Lead lead) {
        String company = lead.getCompany() == null || lead.getCompany().isBlank() ? "Not provided" : lead.getCompany();
        return """
                <!doctype html>
                <html>
                <body style="margin:0;padding:0;background:#eef3f8;font-family:Arial,sans-serif;color:#1a2b3b;">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="padding:22px 0;">
                    <tr>
                      <td align="center">
                        <table width="640" cellpadding="0" cellspacing="0" style="max-width:640px;background:#ffffff;border-radius:10px;overflow:hidden;border:1px solid #d6e1eb;">
                          <tr><td><img src="cid:%s" alt="Banner" style="display:block;width:100%%;height:auto;"></td></tr>
                          <tr>
                            <td style="padding:24px;">
                              <table width="100%%" cellpadding="0" cellspacing="0">
                                <tr>
                                  <td style="vertical-align:middle;">
                                    <img src="cid:%s" alt="Logo" width="44" height="44" style="border-radius:50%%;vertical-align:middle;">
                                    <span style="font-weight:700;font-size:18px;margin-left:10px;vertical-align:middle;display:inline-block;">%s</span>
                                  </td>
                                </tr>
                              </table>
                              <h2 style="margin:20px 0 10px;font-size:20px;">New Website Lead Received</h2>
                              <p style="margin:0 0 18px;color:#516579;">A new client has submitted the contact form.</p>
                              <table width="100%%" cellpadding="8" cellspacing="0" style="border:1px solid #dbe5ee;border-radius:8px;">
                                <tr><td style="font-weight:700;">Name</td><td>%s</td></tr>
                                <tr style="background:#f8fbfe;"><td style="font-weight:700;">Email</td><td>%s</td></tr>
                                <tr><td style="font-weight:700;">Company</td><td>%s</td></tr>
                                <tr style="background:#f8fbfe;"><td style="font-weight:700;">Service</td><td>%s</td></tr>
                                <tr><td style="font-weight:700;">Submitted</td><td>%s</td></tr>
                              </table>
                              <div style="margin-top:16px;padding:14px;border:1px solid #dbe5ee;border-radius:8px;background:#f8fbfe;">
                                <div style="font-weight:700;margin-bottom:8px;">Client Message</div>
                                <div style="white-space:pre-wrap;line-height:1.5;color:#233649;">%s</div>
                              </div>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(
                BANNER_CID,
                LOGO_CID,
                escapeHtml(brandName),
                escapeHtml(lead.getName()),
                escapeHtml(lead.getEmail()),
                escapeHtml(company),
                escapeHtml(lead.getService()),
                escapeHtml(DATE_FORMATTER.format(lead.getCreatedAt())),
                escapeHtml(lead.getMessage())
        );
    }

    private String buildClientHtml(Lead lead) {
        String firstName = lead.getName().trim().split("\\s+")[0];
        return """
                <!doctype html>
                <html>
                <body style="margin:0;padding:0;background:#eef3f8;font-family:Arial,sans-serif;color:#1a2b3b;">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="padding:22px 0;">
                    <tr>
                      <td align="center">
                        <table width="640" cellpadding="0" cellspacing="0" style="max-width:640px;background:#ffffff;border-radius:10px;overflow:hidden;border:1px solid #d6e1eb;">
                          <tr><td><img src="cid:%s" alt="Banner" style="display:block;width:100%%;height:auto;"></td></tr>
                          <tr>
                            <td style="padding:24px;">
                              <img src="cid:%s" alt="Logo" width="52" height="52" style="border-radius:50%%;">
                              <h2 style="margin:16px 0 10px;font-size:22px;">Thank you, %s.</h2>
                              <p style="margin:0 0 12px;color:#516579;line-height:1.55;">
                                We have received your request for <strong>%s</strong>. Our team will review your details and contact you shortly by email.
                              </p>
                              <p style="margin:0 0 18px;color:#516579;line-height:1.55;">
                                At %s, we focus on reliable delivery, clear communication, and business-first digital execution.
                              </p>
                              <div style="padding:14px;border:1px solid #dbe5ee;border-radius:8px;background:#f8fbfe;">
                                <div style="font-weight:700;margin-bottom:8px;">Request Summary</div>
                                <div style="color:#233649;line-height:1.5;">
                                  Name: %s<br>
                                  Email: %s<br>
                                  Service: %s
                                </div>
                              </div>
                              <p style="margin:18px 0 0;color:#516579;">
                                Website: <a href="%s" style="color:#0c6fa2;text-decoration:none;">%s</a>
                              </p>
                              <p style="margin:18px 0 0;color:#344a5f;font-weight:700;">%s</p>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(
                BANNER_CID,
                LOGO_CID,
                escapeHtml(firstName),
                escapeHtml(lead.getService()),
                escapeHtml(brandName),
                escapeHtml(lead.getName()),
                escapeHtml(lead.getEmail()),
                escapeHtml(lead.getService()),
                escapeHtml(websiteUrl),
                escapeHtml(websiteUrl),
                escapeHtml(brandTagline)
        );
    }

    private String resolveSenderEmail() {
        String from = normalize(senderEmail);
        if (isValidEmail(from)) {
            return from;
        }
        return resolveRecipientEmail();
    }

    private String resolveRecipientEmail() {
        String to = normalize(recipientEmail);
        if (isValidEmail(to)) {
            return to;
        }
        throw new IllegalStateException("Invalid mail recipient configuration. Check app.leads.notification.to.");
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        try {
            InternetAddress address = new InternetAddress(email, true);
            address.validate();
            return true;
        } catch (AddressException exception) {
            return false;
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
