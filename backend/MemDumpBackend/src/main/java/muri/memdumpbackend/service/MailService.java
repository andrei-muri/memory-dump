package muri.memdumpbackend.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import muri.memdumpbackend.exception.AuthException;
import muri.memdumpbackend.model.Recovery;
import muri.memdumpbackend.model.User;
import muri.memdumpbackend.repo.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    public void sendMail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User " + email + " does not exist"));
        String recoveryToken = UUID.randomUUID().toString();
        Recovery recovery = Recovery.builder()
                .token(recoveryToken)
                .expiration(LocalDateTime.now().plusMinutes(15))
                .build();
        user.setRecovery(recovery);
        userRepository.save(user);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Recovery Mail");
        mailMessage.setText("Recovery Token: " + recoveryToken);
        mailSender.send(mailMessage);
    }
}
