package com.khudobchenok.telegram.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

@Service
@Slf4j
public class EmailService {

    private static final String DEFAULT_TOPIC = "Сообщение от telegram бота";

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendMail(String to, String body, String topic) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("mailfortestmyproject@gmail.com");
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(topic);
        simpleMailMessage.setText(body);
        javaMailSender.send(simpleMailMessage);
        log.info("Сообщение отправлено по адресу " + to);
        log.info("Текст сообщения:\n" + body);
    }

    public void sendMail(String name, String surname, String text, String email, Message message) {
        log.info("Пользователь " + message.getFrom().getUserName() + " указал почту " + message.getText());
        User user = message.getFrom();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String userName = user.getUserName();
        if (user.getFirstName() == null) firstName = "";
        if (user.getLastName() == null) lastName = "";
        if (user.getUserName() == null) userName = "";
        String body = String.format("Дорогой(ая) %s %s.\n%s\n\nОтправитель: %s %s\nTelegram: %s",
                name, surname, text, firstName, lastName, userName);
        sendMail(email, body, DEFAULT_TOPIC);
    }
}
