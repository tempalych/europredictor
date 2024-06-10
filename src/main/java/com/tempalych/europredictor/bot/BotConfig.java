package com.tempalych.europredictor.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class BotConfig {

    @Value("${application.telegram.bot.token}")
    private String token;

    @Value("${application.telegram.bot.name}")
    private String botName;

    @Bean
    @ConditionalOnProperty("application.telegram.enabled")
    Bot telegramBot() {
        var bot = new Bot(token, botName);
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            System.out.println(e.getLocalizedMessage());
        }
        return bot;
    }
}
