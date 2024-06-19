package com.tempalych.europredictor.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
@ConditionalOnProperty(value = "football.api.scheduling.enable", havingValue = "true")
public class CheckMatchStateScheduler {

    private static final Logger logger = LoggerFactory.getLogger(CheckMatchStateScheduler.class);

    private final FootballApiIntegrationService footballApiIntegrationService;

    @Scheduled(fixedDelay = 60000)
    public void checkFinishedMatchesTask() {

        logger.info("Scheduled task checkFinishedMatches() fired");
        footballApiIntegrationService.checkFinishedMatches();
    }
}
