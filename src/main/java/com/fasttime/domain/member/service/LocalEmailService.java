package com.fasttime.domain.member.service;

import com.fasttime.domain.member.exception.EmailSendingException;
import com.fasttime.domain.member.exception.VerificationCodeExpiredException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Profile("local || develop || test")
@Service
@Transactional
public class LocalEmailService implements EmailUseCase {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public String sendVerificationEmail(String to) {
        String authCode = generateAuthCode();
        try {
            log.info("send verification email...");
            log.info("target: {}", to);
            log.info("code: {}", authCode);
            saveVerificationCode(to, authCode, 5);
            return authCode;
        } catch (MailException ex) {
            throw new EmailSendingException();
        }
    }

    private void saveVerificationCode(String email, String code, long timeout) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(email, code, timeout, TimeUnit.MINUTES);
    }

    @Override
    public boolean verifyEmailCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(email);
        if (storedCode == null) {
            throw new VerificationCodeExpiredException();
        }
        return storedCode.equals(code);
    }

    private String generateAuthCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(3);
            switch (index) {
                case 0:
                    key.append((char) (random.nextInt(26) + 97));
                    break;
                case 1:
                    key.append((char) (random.nextInt(26) + 65));
                    break;
                case 2:
                    key.append(random.nextInt(10));
                    break;
            }
        }
        return key.toString();
    }
}
