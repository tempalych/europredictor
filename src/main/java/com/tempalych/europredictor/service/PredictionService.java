package com.tempalych.europredictor.service;

import com.tempalych.europredictor.model.entity.Prediction;
import com.tempalych.europredictor.model.repository.MatchRepository;
import com.tempalych.europredictor.model.repository.PredictionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PredictionService {

    MatchRepository matchRepository;
    PredictionRepository predictionRepository;
    UserService userService;

    public void newPrediction(Long matchId, Integer homeScore, Integer visitorScore) {
        var match = matchRepository.findById(matchId);
        var prediction = Prediction.builder()
                .user(userService.getCurrentUser())
                .match(match)
                .homeScore(homeScore)
                .visitorScore(visitorScore)
                .build();
        predictionRepository.save(prediction);
    }

    public List<Prediction> getCurrentUserPredictions() {
        return predictionRepository.findAllByUserId(userService.getCurrentUser().getId());
    }
}
