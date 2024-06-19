package com.tempalych.europredictor.model.repository;

import com.tempalych.europredictor.model.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findMatchesByHomeScoreIsNullAndVisitorScoreIsNull();
}
