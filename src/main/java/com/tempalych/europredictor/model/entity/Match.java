package com.tempalych.europredictor.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Match {
    Long id;
    LocalDateTime time;
    Team homeTeam;
    Team visitorTeam;
    Integer homeScore;
    Integer visitorScore;
}
