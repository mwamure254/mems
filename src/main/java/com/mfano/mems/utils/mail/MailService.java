package com.mfano.mems.utils.mail;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.mfano.mems.auth.models.Branch;
import com.mfano.mems.auth.services.AuditService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {
  private final JavaMailSender mailSender;
  private final AuditService auditService;

  public void sendSimpleMessage(String to, String subject, String text, Long b) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setFrom("no-reply@mfano.com");
    message.setSubject(subject);
    message.setText(text);
    mailSender.send(message);

    auditService.record(b, "RESEND_VERIFICATION", "SUCCESS",
        "Resent token to user " + to);
  }
}
