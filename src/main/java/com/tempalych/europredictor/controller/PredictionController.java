package com.tempalych.europredictor.controller;

import com.tempalych.europredictor.service.MatchService;
import com.tempalych.europredictor.service.PredictionService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@AllArgsConstructor
@Controller
public class PredictionController {

    private static final Logger logger = LoggerFactory.getLogger(PredictionController.class);

    private final MatchService matchService;
    private final PredictionService predictionService;

    @GetMapping("/predictions")
    public String showPredictionForm(Model model) {
        model.addAttribute("groups", matchService.getGroups());
        return "prediction/form";
    }

    @GetMapping("/")
    public String showPredictionFormRoot(Model model) {
        return showPredictionForm(model);
    }

    @GetMapping("/group-matches")
    public String getGroupMatches(@RequestParam String groupName, Model model, @RequestHeader String tzid) {
        var matchPredictions = predictionService.getGroupMatchesWithUserPredictions(groupName, tzid);
        model.addAttribute("matchPredictions", matchPredictions);
        return "prediction/list";
    }

    @PostMapping("/save-prediction-home")
    public String savePredictionHome(@RequestParam Integer homeScore,
                                     @RequestParam Long matchId) {
        logger.info("POST /save-prediction-home: homeScore: {}, matchId: {}", homeScore, matchId);
        predictionService.savePrediction(matchId, homeScore, true);
        return "redirect:/predictions";
    }

    @PostMapping("/save-prediction-visitor")
    public String savePredictionVisitor(@RequestParam Integer visitorScore,
                                        @RequestParam Long matchId) {
        logger.info("POST /save-prediction-visitor: score: {}, matchId: {}", visitorScore, matchId);
        predictionService.savePrediction(matchId, visitorScore, false);
        return "redirect:/predictions";
    }
}
