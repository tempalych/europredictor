package com.tempalych.europredictor.controller;

import com.tempalych.europredictor.service.MatchService;
import com.tempalych.europredictor.service.PredictionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@AllArgsConstructor
@Controller
public class PredictionController {

    PredictionService predictionService;
    MatchService matchService;

    @GetMapping("/prediction-form")
    public String showPredictionForm(Model model) {
        var matches = matchService.getAllAvailableMatches();
        model.addAttribute("matches", matches);
        var userPredictions = predictionService.getCurrentUserPredictions();
        model.addAttribute("predictions", userPredictions);
        return "prediction/form";
    }

    @PostMapping("/predictions")
    public String submitPrediction(@RequestParam Long matchId,
                                   @RequestParam Integer homeScore,
                                   @RequestParam Integer visitorScore,
                                   Model model) {

        predictionService.newPrediction(matchId, homeScore, visitorScore);
        var predictions = predictionService.getCurrentUserPredictions();
        model.addAttribute("predictions", predictions);
        return "prediction/list";
    }
}
