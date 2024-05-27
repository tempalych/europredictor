package com.tempalych.europredictor.service;

import com.tempalych.europredictor.model.dto.MatchPredictionDto;
import com.tempalych.europredictor.model.entity.Prediction;
import com.tempalych.europredictor.model.repository.MatchRepository;
import com.tempalych.europredictor.model.repository.PredictionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PredictionService {

    private final MatchRepository matchRepository;
    private final PredictionRepository predictionRepository;
    private final UserService userService;

    public void savePrediction(Long matchId, Integer score, Boolean isHome) {
        var user = userService.getCurrentUser();
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
        return predictionRepository.findPredictionsByGroupAndUserId(groupName, user.getId());
    }
}
