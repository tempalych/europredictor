package com.tempalych.europredictor.model.repository;

import com.tempalych.europredictor.model.dto.MatchPredictionDto;
import com.tempalych.europredictor.model.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface PredictionRepository extends JpaRepository<Prediction, Long> {

    List<Prediction> findAllByUserId(Long userId);

    Prediction findByUserIdAndMatchId(Long userId, Long matchId);

    @Query("SELECT new com.tempalych.europredictor.model.dto.MatchPredictionDto(m.id, home_team.name, " +
            "visitor_team.name, m.groupName, m.time, m.homeScore, m.visitorScore, p.homeScore, p.visitorScore) " +
            "FROM Match m " +
            "LEFT JOIN Prediction p on m.id = p.match.id and p.user.id = :userId " +
            "LEFT JOIN Team home_team on home_team.id = m.homeTeam.id " +
            "LEFT JOIN Team visitor_team on visitor_team.id = m.visitorTeam.id " +
            "WHERE m.groupName = :groupName ")
    List<MatchPredictionDto> findPredictionsByGroupAndUserId(@Param("groupName") String groupName,
                                                             @Param("userId") Long userId);
}
