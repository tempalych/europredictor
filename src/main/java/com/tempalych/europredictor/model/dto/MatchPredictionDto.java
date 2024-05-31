package com.tempalych.europredictor.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class MatchPredictionDto {
    private Long matchId;
    private String homeTeamName;
    private String visitorTeamName;
    private String groupName;
    private LocalDateTime time;
    private Integer homeScore;
    private Integer visitorScore;
    private Integer homeScorePrediction;
    private Integer visitorScorePrediction;
    private Boolean disabled;
    private PredictionValue predictionValue;
}
