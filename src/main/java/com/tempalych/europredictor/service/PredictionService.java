package com.tempalych.europredictor.service;

import com.tempalych.europredictor.model.dto.MatchPredictionDto;
import com.tempalych.europredictor.model.dto.PredictionValue;
import com.tempalych.europredictor.model.entity.Prediction;
import com.tempalych.europredictor.model.repository.MatchRepository;
import com.tempalych.europredictor.model.repository.PredictionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PredictionService {

    private final MatchRepository matchRepository;
    private final PredictionRepository predictionRepository;
    private final UserService userService;

    public void savePrediction(Long matchId, Integer score, Boolean isHome) {
        var user = userService.getCurrentUser();
        var match = matchRepository.getReferenceById(matchId);
        if (LocalDateTime.now().isAfter(match.getTime().minusHours(1))) {
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
        predictions.forEach(it -> it.setDisabled(LocalDateTime.now().isAfter(it.getTime().minusHours(1))));
        return predictions;
    }

    public PredictionValue calculatePredictionValue(int actualHomeScore, int actualVisitorScore,
                                                    int predictionHomeScore, int predictionVisitorScore) {
        if (actualHomeScore == predictionHomeScore && actualVisitorScore == predictionVisitorScore) {
            return PredictionValue.GUESSED_EXACT_SCORE;
        }

        if (actualHomeScore - predictionHomeScore == actualVisitorScore - predictionVisitorScore &&
                actualHomeScore != actualVisitorScore) {
            return PredictionValue.GUESSED_WINNER_AND_SCORE_DIFFERENCE;
        }

        if ((actualHomeScore > actualVisitorScore && predictionHomeScore > predictionVisitorScore) ||
                actualHomeScore < actualVisitorScore && predictionHomeScore < predictionVisitorScore) {
            return PredictionValue.GUESSED_WINNER;
        }

        if (actualHomeScore == actualVisitorScore && predictionHomeScore == predictionVisitorScore) {
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
        for (var prediction: predictions) {
            var predictionValue = calculatePredictionValue(homeScore, visitorScore, prediction.getHomeScore(), prediction.getVisitorScore());
            prediction.setPredictionValue(predictionValue);
            prediction.setPredictionValueScore(predictionValue.score);
        }
        predictionRepository.saveAll(predictions);
    }
}
