package com.tempalych.europredictor.model.repository;

import com.tempalych.europredictor.model.entity.Match;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class MatchRepository {

    @Autowired
    TeamRepository teamRepository;

    List<Match> matches = new ArrayList<>();

    @PostConstruct
    void init() {
        var teams = teamRepository.findAll();
        matches.add(Match.builder()
                .id(1L)
                .time(LocalDateTime.now())
                .homeTeam(teams.get(0))
                .visitorTeam(teams.get(1))
                .build());

        matches.add(Match.builder()
                .id(1L)
                .time(LocalDateTime.now())
                .homeTeam(teams.get(2))
                .visitorTeam(teams.get(3))
                .build());
    }

    public List<Match> findAll() {
        return matches;
    }

    public Match findById(Long id) {
        return matches.stream().filter(m -> m.getId().equals(id)).toList().getFirst();
    }
}
