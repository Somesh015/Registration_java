package com.example.demoRegistration.EmailSender;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailSenderService implements EmailSender {
    private final static Logger logger= LoggerFactory.getLogger(EmailSenderService.class);
    private final JavaMailSender javaMailSender;



    @Override
    @Async
    public void send(String to, String email) {
        try {
            MimeMessage mimeMessage=javaMailSender.createMimeMessage();
            MimeMessageHelper helper=new MimeMessageHelper(mimeMessage,"utf-8");
            helper.setText(email,true);
            helper.setTo(to);
            helper.setFrom("noreply@somesh.com");
            helper.setSubject("Confirm you email");
            javaMailSender.send(mimeMessage);
        }
        catch (MessagingException e){
            logger.error("failed to send email",e);
            throw new IllegalStateException("failed to send email");
        }


    }
}
