package com.tempalych.europredictor.controller;

import com.tempalych.europredictor.model.entity.Team;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class TeamController {

    @GetMapping("/teams")
    public String getTeams(Model model) {
        List<Team> teams = new ArrayList<>();
        teams.add(Team.builder().id(1L).name("Germany").build());
        teams.add(Team.builder().id(2L).name("Scotland").build());
        model.addAttribute("teams", teams);
        return "team/list";
    }
}
