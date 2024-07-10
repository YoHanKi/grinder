package com.grinder.service.implement;

import com.grinder.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender emailSender;

    @Override
    public void sendEmail(String toEmail,
                          String title,
                          String content) {
//        SimpleMailMessage emailForm = createEmailForm(toEmail, title,content);
//        try {
//            emailSender.send(emailForm);
//        } catch (RuntimeException e) {
//            log.debug("MailService.sendEmail exception occur toEmail: {}, " +
//                    "title: {}, text: {}", toEmail, title, content);
//            throw new IllegalArgumentException("Unable to send email");
//        }
    }
    // 발신할 이메일 데이터 세팅
//    private SimpleMailMessage createEmailForm(String toEmail,
//                                              String title,
//                                              String content) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(toEmail);
//        message.setSubject(title);
//        message.setText(content);
//
//        return message;
//    }

}
