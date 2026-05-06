package com.example.demoRegistration.security.config;

import com.example.demoRegistration.AppUser.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@AllArgsConstructor
@EnableWebSecurity

//@RequiredArgsConstructor
public class WebSecurityConfig {

    private final AppUserService appUserService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/registration",
                                "/api/v1/registration/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(daoAuthenticationProvider())
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(appUserService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
/*
CSRF = Cross Site Request Forgery.
Imagine you are logged into your bank. You visit an evil website. That evil website secretly sends:
        "Transfer ₹10,000 to hacker account"
to your bank — using YOUR logged in session. Your bank thinks it is you!
CSRF protection prevents this attack.
But why disable it here?
CSRF attacks happen through BROWSERS
Your app is a REST API used with POSTMAN
No browser = No CSRF attack possible
So we safely disable it ✅*/


/*1. User sends POST /login
with email="john@gmail.com" password="password123"
        ↓
        2. SecurityFilterChain receives the request
          ↓
                  3. .formLogin() handles /login endpoint
          ↓
                  4. DaoAuthenticationProvider takes over
          ↓
                  5. Calls appUserService.loadUserByUsername("john@gmail.com")
   → finds user in database
          ↓
                  6. Calls passwordEncoder.matches("password123", hashedPassword)
   → checks if password is correct
          ↓
                  7. Checks isEnabled() → has email been verified?
        ↓
        8. All good → Login SUCCESS ✅
Something wrong → 401 Unauthorized ❌*/

/*
WebSecurityConfig        =  the entire building security system

@EnableWebSecurity       =  turning the security system ON

SecurityFilterChain      =  the series of security checkpoints
                            at the building entrance

.csrf().disable()        =  disabling anti-tailgating system
                            (not needed for this building)

.requestMatchers(
  "/registration/**")
.permitAll()             =  reception area — open to everyone

.anyRequest()
.authenticated()         =  all other floors — need a keycard

DaoAuthenticationProvider =  the security guard who verifies
                             your identity at the desk

setUserDetailsService    =  giving the guard access to the
                            employee database

setPasswordEncoder       =  giving the guard the password
                            checker machine

.formLogin()             =  setting up the reception desk
                            where people get their keycard
*/