package com.example.demoRegistration.registration;

import com.example.demoRegistration.AppUser.AppUser;
import com.example.demoRegistration.AppUser.AppUserRole;
import com.example.demoRegistration.AppUser.AppUserService;
import com.example.demoRegistration.EmailSender.EmailSender;
import com.example.demoRegistration.registration.ConfirmationToken.ConfirmationToken;
import com.example.demoRegistration.registration.ConfirmationToken.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final AppUserService appUserService;
    private final EmailValidator emailValidator;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;


    // ===========================
    // REGISTER
    // ===========================
    public String register(RegistrationRequest request) {

        // Step 1 — check if email is valid
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if (!isValidEmail) {
            throw new IllegalStateException("email not valid");
        }

        // Step 2 — sign up user and get token back
        String token = appUserService.signUpUser(
                new AppUser(
                        request.getFirstName(),
                        request.getLastName(),
                        request.getEmail(),
                        request.getPassword(),
                        AppUserRole.USER
                )
        );

        // Step 3 — build confirmation link
        String link = "http://localhost:8080/api/v1/registration/confirm?token=" + token;

        // Step 4 — send confirmation email
        // emailSender.send(request.getEmail(), buildEmail(request.getFirstName(), link));
        // ↑ uncomment this when email service is ready
        emailSender.send(request.getEmail(),buildEmail(request.getFirstName(),link));

        // Step 5 — return token (for testing)
        return token;
    }

    // ===========================
    // CONFIRM TOKEN
    // ===========================
    @Transactional
    public String confirmToken(String token) {

        // Step 1 — find token in database
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        // Step 2 — check if already confirmed
        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        // Step 3 — check if token expired
        LocalDateTime expiredAt = confirmationToken.getExpiredAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        // Step 4 — set confirmedAt
        confirmationTokenService.setConfirmedAt(token);

        // Step 5 — enable user account
        appUserService.enableAppUser(
                confirmationToken.getAppUser().getEmail()
        );

        return "confirmed";
    }
    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;" +
                "font-size:16px;margin:0;color:#0b0c0c\">" +

                "<div style=\"background-color:#0b0c0c;padding:20px\">" +
                "<h1 style=\"color:white;margin:0\">Confirm your email</h1>" +
                "</div>" +

                "<div style=\"padding:20px\">" +
                "<p>Hi " + name + ",</p>" +
                "<p>Thank you for registering. " +
                "Please click the button below to activate your account:</p>" +

                "<a href=\"" + link + "\" " +
                "style=\"display:inline-block;" +
                "background-color:#0b0c0c;" +
                "color:white;" +
                "padding:10px 20px;" +
                "text-decoration:none;" +
                "border-radius:5px\">" +
                "Activate Now" +
                "</a>" +

                "<p style=\"color:#666;margin-top:20px\">" +
                "Link will expire in 15 minutes.</p>" +

                "<p>If you did not register, " +
                "please ignore this email.</p>" +
                "</div>" +

                "</div>";
    }
}
