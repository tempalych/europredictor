package com.tempalych.europredictor.service;

import com.tempalych.europredictor.model.dto.PredictionValue;
import com.tempalych.europredictor.model.entity.BotMessage;
import com.tempalych.europredictor.model.entity.Match;
import com.tempalych.europredictor.model.entity.Prediction;
import com.tempalych.europredictor.model.repository.BotMessageRepository;
import com.tempalych.europredictor.model.repository.MatchRepository;
import com.tempalych.europredictor.model.repository.PredictionRepository;
import com.tempalych.europredictor.model.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminService {

    private final MatchRepository matchRepository;
    private final PredictionRepository predictionRepository;
    private final UserRepository userRepository;
    private final BotMessageRepository botMessageRepository;

    public void saveActualScore(Long matchId, Integer homeScore, Integer visitorScore) {
        var match = matchRepository.getReferenceById(matchId);
        match.setHomeScore(homeScore);
        match.setVisitorScore(visitorScore);
        matchRepository.save(match);
        var predictions = predictionRepository.findByMatchId(matchId);
        var usersWhoMadePredictionsOnMatch = new HashSet<>(predictions.stream().map(Prediction::getUser).toList());
        for (var prediction: predictions) {
            var predictionValue = calculatePredictionValue(homeScore, visitorScore, prediction.getHomeScore(), prediction.getVisitorScore());
            prediction.setPredictionValue(predictionValue);
            prediction.setPredictionValueScore(predictionValue.score);
        }

        var usersWhoDidNotPredictions = userRepository.findAll().stream()
                .filter(user -> !usersWhoMadePredictionsOnMatch.contains(user))
                .collect(Collectors.toUnmodifiableSet());
        for (var userWithoutPrediction: usersWhoDidNotPredictions) {
            predictions.add(Prediction.builder()
                    .user(userWithoutPrediction)
                    .match(match)
                    .predictionValueScore(0)
                    .predictionValue(PredictionValue.NO_PREDICTION)
                    .build());
        }

        predictionRepository.saveAll(predictions);
        prepareMessage(match, predictions, homeScore, visitorScore);
    }

    public static PredictionValue calculatePredictionValue(Integer actualHomeScore, Integer actualVisitorScore,
                                                    Integer predictionHomeScore, Integer predictionVisitorScore) {
        if (predictionHomeScore == null || predictionVisitorScore == null) {
            return PredictionValue.NO_PREDICTION;
        }

        if (actualHomeScore.equals(predictionHomeScore) && actualVisitorScore.equals(predictionVisitorScore)) {
            return PredictionValue.GUESSED_EXACT_SCORE;
        }

        if (actualHomeScore - predictionHomeScore == actualVisitorScore - predictionVisitorScore &&
                !actualHomeScore.equals(actualVisitorScore)) {
            return PredictionValue.GUESSED_WINNER_AND_SCORE_DIFFERENCE;
        }

        if ((actualHomeScore > actualVisitorScore && predictionHomeScore > predictionVisitorScore) ||
                actualHomeScore < actualVisitorScore && predictionHomeScore < predictionVisitorScore) {
            return PredictionValue.GUESSED_WINNER;
        }

        if (actualHomeScore.equals(actualVisitorScore) && predictionHomeScore.equals(predictionVisitorScore)) {
            return PredictionValue.GUESSED_DRAW_BUT_NOT_SCORE;
        }

        return PredictionValue.GUESSED_NOTHING;
    }

    private void prepareMessage(Match match, List<Prediction> matchPredictions, Integer homeScore, Integer visitorScore) {
        StringBuilder message = new StringBuilder("Match is over: ")
                .append(match.getHomeTeam().getName())
                .append(" ").append(homeScore).append(":").append(visitorScore).append(" ")
                .append(match.getVisitorTeam().getName()).append("\n");
        matchPredictions.sort(Comparator
                .comparingLong(Prediction::getPredictionValueScore).reversed()
                .thenComparingLong(p -> p.getUser().getId()));
        for (var prediction: matchPredictions) {
            var username = prediction.getUser().getUsername();
            if ("admin".equals(username)) {
                username = "🤖";
            }
            message.append("<b>").append(username).append("</b> ")
                    .append(prediction.getHomeScore())
                    .append(":")
                    .append(prediction.getVisitorScore())
                    .append(" ")
                    .append(prediction.getPredictionValue().description)
                    .append(" (+")
                    .append(prediction.getPredictionValueScore())
                    .append(")")
                    .append("\n");
        }

        message.append("\n");
        var tableRows = predictionRepository.getTable();
        for (var row: tableRows) {
            message.append("admin".equals(row.name()) ? "🤖" : row.name()).append(": ").append(row.points()).append("\n");
        }
        BotMessage botMessage = BotMessage.builder().timeAdded(LocalDateTime.now()).message(message.toString()).build();
        botMessageRepository.save(botMessage);
    }
}
