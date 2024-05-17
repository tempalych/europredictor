package com.tempalych.europredictor.controller;

import com.tempalych.europredictor.model.entity.Prediction;
import com.tempalych.europredictor.model.repository.MatchRepository;
import com.tempalych.europredictor.model.repository.PredictionRepository;
import com.tempalych.europredictor.model.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@AllArgsConstructor
@Controller
public class PredictionController {

    MatchRepository matchRepository;
    PredictionRepository predictionRepository;
    UserRepository userRepository;

    @GetMapping("/prediction-form")
    public String showPredictionForm(Model model) {
        var matches = matchRepository.findAll();
        model.addAttribute("matches", matches);
        return "prediction/form";
    }

    @PostMapping("/predictions")
    public String submitPrediction(@RequestParam Long matchId,
                                   @RequestParam Integer homeScore,
                                   @RequestParam Integer visitorScore,
                                   Model model) {
        var match = matchRepository.findById(matchId);
        var prediction = Prediction.builder()
                .user(userRepository.getUser())
                .match(match)
                .homeScore(homeScore)
                .visitorScore(visitorScore)
                .build();
        predictionRepository.save(prediction);
        model.addAttribute("predictions", predictionRepository.findAll());
        return "prediction/list";
    }
}
