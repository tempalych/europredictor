package com.tempalych.europredictor.model.dto.rq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FootballApiResponse {
    List<Response> response;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        Teams teams;
        Goals goals;
        Score score;

        @Data
        public static class Teams {
            Team home;
            Team away;

            @Data
            public static class Team {
                Long id;
                String name;
            }
        }

        @Data
        public static class Goals {
            Integer home;
            Integer away;
        }

        @Data
        public static class Score {
            Goals halftime;
            Goals fulltime;
            Goals extratime;
            Goals penalty;
        }
    }
}
