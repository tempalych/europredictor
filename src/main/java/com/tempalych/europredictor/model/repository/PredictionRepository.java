package com.tempalych.europredictor.model.repository;

import com.tempalych.europredictor.model.dto.MatchPredictionDto;
import com.tempalych.europredictor.model.dto.ChartDatasetLine;
import com.tempalych.europredictor.model.dto.ScoreTableLine;
import com.tempalych.europredictor.model.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface PredictionRepository extends JpaRepository<Prediction, Long> {

    Prediction findByUserIdAndMatchId(Long userId, Long matchId);

    @Query("SELECT new com.tempalych.europredictor.model.dto.MatchPredictionDto(m.id, home_team.name, " +
            "visitor_team.name, m.groupName, m.time, m.homeScore, m.visitorScore, p.homeScore, p.visitorScore, false, p.predictionValue) " +
            "FROM Match m " +
            "LEFT JOIN Prediction p on m.id = p.match.id and p.user.id = :userId " +
            "LEFT JOIN Team home_team on home_team.id = m.homeTeam.id " +
            "LEFT JOIN Team visitor_team on visitor_team.id = m.visitorTeam.id " +
            "WHERE m.groupName = :groupName " +
            "ORDER BY m.time")
    List<MatchPredictionDto> findPredictionsByGroupAndUserId(@Param("groupName") String groupName,
                                                             @Param("userId") Long userId);

    List<Prediction> findByMatchId(Long matchId);

    List<Prediction> findByUserId(Long userId);

    @Query("SELECT new com.tempalych.europredictor.model.dto.ScoreTableLine(u.username, sum(p.predictionValueScore)) " +
            "FROM Prediction p " +
            "JOIN User u on u = p.user " +
            "GROUP BY u.username " +
            "ORDER BY sum(p.predictionValueScore) DESC")
    List<ScoreTableLine> getTable();

    @Query("SELECT p " +
            "FROM Prediction p " +
            "WHERE p.predictionValue is not null " +
            "ORDER BY p.match.time, p.match.id")
    List<Prediction> findAllPredictionsWithValues();
}
