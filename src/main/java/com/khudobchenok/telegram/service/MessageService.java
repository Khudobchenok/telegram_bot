package com.khudobchenok.telegram.service;

import com.khudobchenok.telegram.exception.IncorrectEmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class MessageService {
    private static final Map<User, List<String>> messageMap = new HashMap<>();

    @Autowired
    EmailService emailService;

    public boolean validateUserExpectation(User user) {
        return messageMap.containsKey(user);
    }

    public Integer putUserMessage(User user, Message message) throws IncorrectEmailException {
        String text = message.getText();
        List<String> messageList = messageMap.get(user);
        if (messageList == null) {
            messageList = new ArrayList<>();
        }
        if (messageList.size() == 3) {
            if (!isValidMail(text)) {
                throw new IncorrectEmailException("Введите корректный email");
            }
        }
        messageList.add(text);
        if (messageList.size() >= 4) {
            emailService.sendMail(messageList.get(0), messageList.get(1), messageList.get(2), messageList.get(3), message);
            messageMap.remove(user);
        } else {
            messageMap.put(user, messageList);
        }
        return messageList.size();
    }

    public void addUserRow(User user) {
        messageMap.put(user, new ArrayList<>());
    }

    public void deleteUserRow(User user) {
        messageMap.remove(user);
    }

    public boolean isValidMail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (email == null) {
            return false;
        }
        return pattern.matcher(email).matches();
    }
}
