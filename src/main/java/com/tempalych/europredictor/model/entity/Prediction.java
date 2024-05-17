package com.tempalych.europredictor.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Prediction {
    User user;
    Match match;
    Integer homeScore;
    Integer visitorScore;
}
