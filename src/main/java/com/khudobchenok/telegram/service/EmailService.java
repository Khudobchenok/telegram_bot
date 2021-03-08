package com.khudobchenok.telegram.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private static final String DEFAULT_TOPIC = "Сообщение от telegram бота";

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendMail(String to, String body, String topic) {
        log.info("sending email");
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("mailfortestmyproject@gmail.com");
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(topic);
        simpleMailMessage.setText(body);
        javaMailSender.send(simpleMailMessage);
        log.info("email sent to " + to);
        log.info("text message: " + body);
    }

    public void sendMail(String name, String surname, String text, String email) {
        String body = String.format("Дорогой(ая) %s %s.\nВаш текст собранный telegram ботом представлен ниже:\n%s", name, surname, text );
        sendMail(email, body, DEFAULT_TOPIC);
    }
}
