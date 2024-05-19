package com.tempalych.europredictor.model.repository;

import com.tempalych.europredictor.model.entity.Match;
import com.tempalych.europredictor.model.entity.Team;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MatchRepository {

    @Autowired
    TeamRepository teamRepository;

    List<Match> matches = new ArrayList<>();

    @PostConstruct
    void init() {
        var teams = teamRepository.findAll();

        var matchId = 0L;
        for (var group: teams.stream().map(Team::getGroup).collect(Collectors.toSet())) {
            matches.add(Match.builder().id(matchId).time(LocalDateTime.now())
                    .homeTeam(teams.stream().filter(team -> team.getGroup().equals(group)).toList().get(0))
                    .visitorTeam(teams.stream().filter(team -> team.getGroup().equals(group)).toList().get(1)).build());

            matches.add(Match.builder().id(matchId).time(LocalDateTime.now())
                    .homeTeam(teams.stream().filter(team -> team.getGroup().equals(group)).toList().get(0))
                    .visitorTeam(teams.stream().filter(team -> team.getGroup().equals(group)).toList().get(2)).build());

            matches.add(Match.builder().id(matchId).time(LocalDateTime.now())
                    .homeTeam(teams.stream().filter(team -> team.getGroup().equals(group)).toList().get(0))
                    .visitorTeam(teams.stream().filter(team -> team.getGroup().equals(group)).toList().get(3)).build());

            matches.add(Match.builder().id(matchId).time(LocalDateTime.now())
                    .homeTeam(teams.stream().filter(team -> team.getGroup().equals(group)).toList().get(1))
                    .visitorTeam(teams.stream().filter(team -> team.getGroup().equals(group)).toList().get(2)).build());

            matches.add(Match.builder().id(matchId).time(LocalDateTime.now())
                    .homeTeam(teams.stream().filter(team -> team.getGroup().equals(group)).toList().get(1))
                    .visitorTeam(teams.stream().filter(team -> team.getGroup().equals(group)).toList().get(3)).build());

            matches.add(Match.builder().id(matchId).time(LocalDateTime.now())
                    .homeTeam(teams.stream().filter(team -> team.getGroup().equals(group)).toList().get(2))
                    .visitorTeam(teams.stream().filter(team -> team.getGroup().equals(group)).toList().get(3)).build());
        }
    }

    public List<Match> findAll() {
        return matches;
    }

    public Match findById(Long id) {
        return matches.stream().filter(m -> m.getId().equals(id)).toList().getFirst();
    }
}
