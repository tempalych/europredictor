package com.tempalych.europredictor.model.dto;

import java.time.LocalDateTime;

public record MatchPredictionDto(Long matchId,
                                 String homeTeamName,
                                 String visitorTeamName,
                                 String groupName,
                                 LocalDateTime time,
                                 Integer homeScore,
                                 Integer visitorScore,
                                 Integer homeScorePrediction,
                                 Integer visitorScorePrediction
                                  ) {

}
