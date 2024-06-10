package com.tempalych.europredictor.bot;

import com.tempalych.europredictor.model.repository.BotMessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@ConditionalOnProperty("application.telegram.enabled")
public class SendMessageScheduler {

    private final BotMessageRepository botMessageRepository;
    private final Bot bot;

    @Scheduled(fixedDelay = 10000)
    private void checkMessagesNeedToBeSent() {
        var messages = botMessageRepository.findByTimeSentIsNull();
        for (var message: messages) {
            bot.sendText(message.getMessage());
            message.setTimeSent(LocalDateTime.now());
        }
        botMessageRepository.saveAll(messages);
    }
}
