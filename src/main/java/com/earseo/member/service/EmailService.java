package com.earseo.member.service;

import com.earseo.member.common.exception.BaseException;
import com.earseo.member.common.exception.MemberErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationCode(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("[EarSEO] 이메일 인증코드");
            helper.setText(buildEmailContent(code), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new BaseException(MemberErrorCode.EMAIL_SEND_FAILED);
        }
    }

    private String buildEmailContent(String code) {
        return """
                <div style="max-width: 500px; margin: 0 auto; padding: 20px; font-family: Arial, sans-serif;">
                    <h2 style="color: #333;">이메일 인증</h2>
                    <p>안녕하세요, EarSEO입니다.</p>
                    <p>아래 인증코드를 입력해주세요.</p>
                    <div style="background-color: #f4f4f4; padding: 15px; text-align: center; margin: 20px 0;">
                        <span style="font-size: 24px; font-weight: bold; letter-spacing: 3px;">%s</span>
                    </div>
                    <p style="color: #666; font-size: 14px;">인증코드는 5분간 유효합니다.</p>
                </div>
                """.formatted(code);
    }
}