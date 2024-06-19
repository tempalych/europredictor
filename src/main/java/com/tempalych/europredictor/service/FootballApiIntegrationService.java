package com.tempalych.europredictor.service;

import com.tempalych.europredictor.model.dto.rq.FootballApiResponse;
import com.tempalych.europredictor.model.entity.ApiCall;
import com.tempalych.europredictor.model.entity.Match;
import com.tempalych.europredictor.model.repository.ApiCallRepository;
import com.tempalych.europredictor.model.repository.MatchRepository;
import com.tempalych.europredictor.utils.TeamNamingUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;


@RequiredArgsConstructor
@Service
public class FootballApiIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(FootballApiIntegrationService.class);

    private final RestTemplate restTemplate;
    private final AdminService adminService;
    private final ApiCallRepository apiCallRepository;
    private final MatchRepository matchRepository;

    @Value("${football.api.url}")
    private String apiUrl;

    @Value("${football.api.key}")
    private String apiKey;

    private final int DAILY_CALLS_LIMIT = 99;

    public void checkFinishedMatches() {
        var now = Instant.now();
        var matchesToCheck = matchRepository.findMatchesByHomeScoreIsNullAndVisitorScoreIsNull()
                .stream().filter(m ->  {
                    var matchStartTime = m.getTime().toInstant(ZoneOffset.UTC);
                    var pollingStart = matchStartTime.plus(105, ChronoUnit.MINUTES);
                    var pollingEnd = pollingStart.plus(25, ChronoUnit.MINUTES);
                    return m.getTime().toLocalDate().equals(LocalDate.now())
                            && (now.isAfter(pollingStart) && now.isBefore(pollingEnd));
                }).toList();
        logger.info("Matches found to check: {}", matchesToCheck.size());
        if (!matchesToCheck.isEmpty()) {
            updateMatchResultsByApi(matchesToCheck);
        }
    }

    private void updateMatchResultsByApi(List<Match> matches) {
        var today = LocalDate.now();
        var callCount = 0;
        var apiCallEntity = apiCallRepository.findByCallDate(today);
        if (apiCallEntity == null) {
            apiCallEntity = apiCallRepository.save(ApiCall.builder().callDate(today).callCount(0).build());
        } else {
            callCount = apiCallEntity.getCallCount();
        }

        if (callCount > DAILY_CALLS_LIMIT) {
            return;
        }

        var apiResponse = requestResults(matches.getFirst().getTime().toLocalDate());

        callCount++;
        apiCallEntity.setCallCount(callCount);
        apiCallRepository.save(apiCallEntity);

        if (apiResponse == null) {
            return;
        }

        for (var match: matches) {
            if (match.getHomeScore() != null || match.getVisitorScore() != null) {
                continue;
            }
            var matchHomeTeam = match.getHomeTeam().getName();
            var matchVisitorTeam = match.getVisitorTeam().getName();
            for (var response: apiResponse.getResponse()) {
                var apiHomeTeam = response.getTeams().getHome().getName();
                var apiVisitorTeam = response.getTeams().getAway().getName();
                if (teamsNamesEquals(matchHomeTeam, apiHomeTeam) && teamsNamesEquals(matchVisitorTeam, apiVisitorTeam)) {
                    var apiHomeScore = response.getScore().getFulltime().getHome();
                    var apiVisitorScore = response.getScore().getFulltime().getAway();
                    if (apiHomeScore != null && apiVisitorScore != null) {
                        logger.info("Saving actual score from API: matchId: {} - {}:{},",
                                match.getId(), apiHomeScore, apiVisitorScore);
                        adminService.saveActualScore(match.getId(), apiHomeScore, apiVisitorScore);
                    }
                }
            }
        }
    }

    private FootballApiResponse requestResults(LocalDate date) {
        var uriBuilder = UriComponentsBuilder.fromHttpUrl("https://" + apiUrl + "/v3/fixtures")
                .queryParam("date", date.toString())
                .queryParam("league", 4)
                .queryParam("season", "2024");
        String url = uriBuilder.toUriString();

        var headers = new HttpHeaders();
        headers.add("x-rapidapi-key", apiKey);
        headers.add("x-rapidapi-host", apiUrl);
        var requestEntity = new HttpEntity<Void>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, requestEntity, FootballApiResponse.class).getBody();
    }

    private boolean teamsNamesEquals(String matchTeam, String apiTeam) {
        if (matchTeam == null || apiTeam == null) {
            return false;
        }

        if (matchTeam.equals(apiTeam)) {
            return true;
        }

        var possibleNames = TeamNamingUtils.teamNames.get(matchTeam);
        return possibleNames != null && possibleNames.contains(apiTeam);
    }

}