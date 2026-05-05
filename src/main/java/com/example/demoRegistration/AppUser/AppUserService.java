package com.example.demoRegistration.AppUser;

import com.example.demoRegistration.registration.ConfirmationToken.ConfirmationToken;
import com.example.demoRegistration.registration.ConfirmationToken.ConfirmationTokenService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {
    
    private AppUserRepo appUserRepo;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    //email and username should be same
    // ✅ Correct — no semicolon, clean chain
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return appUserRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found"));
    }

    public String signUpUser(AppUser appUser) {
        Boolean userExists = appUserRepo.findByEmail(appUser.getEmail()).isPresent();
        if(userExists){
            throw new IllegalStateException("User Already Present");
        }
        String encoded = bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encoded);
        appUserRepo.save(appUser);

        // TODO: send confirmationToken

        String token= UUID.randomUUID().toString();
        ConfirmationToken confirmationToke=new ConfirmationToken(token, LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),appUser);
        confirmationTokenService.saveConfirmationToken(confirmationToke);



        return token;
    }
    @Transactional
    public void enableAppUser(String email){
        appUserRepo.enableAppUser(email);
    }

}
