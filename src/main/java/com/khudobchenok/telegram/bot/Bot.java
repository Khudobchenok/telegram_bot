package com.khudobchenok.telegram.bot;

import com.khudobchenok.telegram.exception.IncorrectEmailException;
import com.khudobchenok.telegram.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {

    @Autowired
    private MessageService messageService;

    private static final String BOT_NAME = "TestSendMailBot";
    private static final String TOKEN = "1553458726:AAHSMHTQwVWmww2Jngx4-Nf5cP_6CsvQjEg";

    @PostConstruct
    public void init() {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText(text);
        try {
            setButtons(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            if (!handleServiceMessage(message)) {
                try {
                    handleMessage(message);
                } catch (IncorrectEmailException e) {
                    log.debug("???????????????????????? " + message.getFrom().getUserName() + " ???????? ???????????????????????? ?????????? ??????????");
                    sendMsg(message, e.getMessage());
                }
            }
        }
    }

    private void handleMessage(Message message) throws IncorrectEmailException {
        if (messageService.validateUserExpectation(message.getFrom())) {
            Integer messageNumber = messageService.putUserMessage(message.getFrom(), message);
            switch (messageNumber) {
                case 1:
                    log.debug("???????????????????????? " + message.getFrom().getUserName() + " ???????? ?????? " + message.getText());
                    sendMsg(message, "?????????????? ??????????????:");
                    break;
                case 2:
                    log.debug("???????????????????????? " + message.getFrom().getUserName() + " ???????? ?????????????? " + message.getText());
                    sendMsg(message, "?????????????? ???????? ??????????????????:");
                    break;
                case 3:
                    log.debug("???????????????????????? " + message.getFrom().getUserName() + " ???????? ?????????????????? " + message.getText());
                    sendMsg(message, "?????????????? ?????????? ????????????????????:");
                    break;
                case 4:
                    sendMsg(message, "?????????????????? ????????????????????");
                    break;
            }
        } else sendMsg(message, "?????? ???????????????? ?????????????????? ?????????????? /begin");
    }

    private boolean handleServiceMessage(Message message) {
        switch (message.getText()) {
            case "/start":
                sendMsg(message, "?????????? ???????????????????? " + message.getFrom().getFirstName());
                log.info("?????????? ???????????????????????? " + message.getFrom().getUserName());
                messageService.deleteUserRow(message.getFrom());
                return true;
            case "/help":
                sendMsg(message, "???????????? ?????????????????? ?????????????? /begin, ?????????????? ???????????? ?????????????? ???????????? ?????? ?? ???????????????? ?????????????????? ???? ?????????????????? ??????????");
                messageService.deleteUserRow(message.getFrom());
                return true;
            case "/begin":
                messageService.deleteUserRow(message.getFrom());
                messageService.addUserRow(message.getFrom());
                sendMsg(message, "?????????????? ??????:");
                return true;
            default:
                return false;
        }
    }

    public void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("/help"));
        keyboardFirstRow.add(new KeyboardButton("/begin"));

        keyboardRowList.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }
}
