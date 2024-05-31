package com.tempalych.europredictor.service;

import com.tempalych.europredictor.model.dto.MatchPredictionDto;
import com.tempalych.europredictor.model.dto.PredictionValue;
import com.tempalych.europredictor.model.entity.Prediction;
import com.tempalych.europredictor.model.repository.MatchRepository;
import com.tempalych.europredictor.model.repository.PredictionRepository;
import com.tempalych.europredictor.model.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PredictionService {

    private final MatchRepository matchRepository;
    private final PredictionRepository predictionRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    public void savePrediction(Long matchId, Integer score, Boolean isHome) {
        var user = userService.getCurrentUser();
        var match = matchRepository.getReferenceById(matchId);
        if (isPredictionEditRestricted(match.getHomeScore(), match.getVisitorScore(), match.getTime())) {
            throw new RuntimeException("It is impossible to make a prediction for a match that has started");
        }
        var prediction = predictionRepository.findByUserIdAndMatchId(user.getId(), matchId);
        if (prediction == null) {
            prediction = Prediction.builder()
                    .user(user)
                    .match(matchRepository.getReferenceById(matchId))
                    .build();
        }

        if (isHome) {
            prediction.setHomeScore(score);
        } else {
            prediction.setVisitorScore(score);
        }

        predictionRepository.save(prediction);
    }

    public List<MatchPredictionDto> getGroupMatchesWithUserPredictions(String groupName) {
        var user = userService.getCurrentUser();
        var predictions = predictionRepository.findPredictionsByGroupAndUserId(groupName, user.getId());
        predictions.forEach(it -> it.setDisabled(isPredictionEditRestricted(it.getHomeScore(), it.getVisitorScore(), it.getTime())));
        return predictions;
    }

    public PredictionValue calculatePredictionValue(Integer actualHomeScore, Integer actualVisitorScore,
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
    }

    public boolean isPredictionEditRestricted(Integer actualHomeScore, Integer actualVisitorScore, LocalDateTime matchTime) {
        if (actualHomeScore != null || actualVisitorScore != null) {
            return true;
        }
        var now = LocalDateTime.now().atZone(ZoneId.of("CET"));
        var timeToRestrictMatchEdit = matchTime.minusHours(1).atZone(ZoneId.of("CET"));
        return now.isAfter(timeToRestrictMatchEdit);
    }
}
