package com.tempalych.europredictor.controller;

import com.tempalych.europredictor.model.entity.Match;
import com.tempalych.europredictor.model.repository.MatchRepository;
import com.tempalych.europredictor.service.AdminService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;

@AllArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;
    private final MatchRepository matchRepository;

    @GetMapping("/page")
    public String showAdminPage(Model model) {
        var matches = matchRepository.findAll().stream().sorted(Comparator.comparing(Match::getTime)).toList();
        model.addAttribute("matches", matches);
        return "admin/page";
    }

    @PostMapping("/save-result")
    public String saveMatchResult(@RequestParam Long matchId, @RequestParam Integer homeScore, @RequestParam Integer visitorScore, Model model) {
        logger.info("POST /save-actual-score: request: {}, {}, {}", matchId, homeScore, visitorScore);
        adminService.saveActualScore(matchId, homeScore, visitorScore);
        return "redirect:/admin/page";
    }
}
