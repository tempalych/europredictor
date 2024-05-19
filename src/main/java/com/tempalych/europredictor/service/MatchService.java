package com.tempalych.europredictor.service;

import com.tempalych.europredictor.model.entity.Match;
import com.tempalych.europredictor.model.repository.MatchRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MatchService {

    MatchRepository matchRepository;

    public List<Match> getAllAvailableMatches() {
        return matchRepository.findAll();
    }
}
