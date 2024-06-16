package com.tempalych.europredictor.bot;

import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {

    private final String token;
    private final String username;

    public Bot(String token, String username) {
        this.token = token;
        this.username = username;
    }

    @Value("${application.telegram.bot.chat}")
    private String chatId;

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
//        System.out.println(update);
    }

    public void sendText(String what){
        SendMessage sm = SendMessage.builder()
                .parseMode(ParseMode.HTML)
                .chatId(chatId)
                .text(what).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
