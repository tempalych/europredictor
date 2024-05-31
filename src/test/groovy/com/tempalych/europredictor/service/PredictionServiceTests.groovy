package com.tempalych.europredictor.service

import com.tempalych.europredictor.EuroPredictorApplication
import com.tempalych.europredictor.model.dto.PredictionValue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@SpringBootTest
@ActiveProfiles(profiles = ["test"])
@ContextConfiguration(classes = [EuroPredictorApplication])
class PredictionServiceTests extends Specification {

    @Autowired
    PredictionService predictionService

    def "For prediction #pHome:#pVisitor and actual #aHome:#aVisitor value is #value"() {
        expect:
        def result = predictionService.calculatePredictionValue(aHome, aVisitor, pHome, pVisitor)
        result.score == value
        result == predictionValue
        where:
        aHome | aVisitor | pHome | pVisitor || value | predictionValue
        4     | 0        | 4     | 0        || 3     | PredictionValue.GUESSED_EXACT_SCORE
        4     | 4        | 4     | 4        || 3     | PredictionValue.GUESSED_EXACT_SCORE
        4     | 2        | 2     | 0        || 2     | PredictionValue.GUESSED_WINNER_AND_SCORE_DIFFERENCE
        4     | 2        | 1     | 0        || 1     | PredictionValue.GUESSED_WINNER
        4     | 4        | 1     | 1        || 1     | PredictionValue.GUESSED_DRAW_BUT_NOT_SCORE
        4     | 4        | 1     | 0        || 0     | PredictionValue.GUESSED_NOTHING
        4     | 1        | 0     | 1        || 0     | PredictionValue.GUESSED_NOTHING
        4     | 1        | 0     | 0        || 0     | PredictionValue.GUESSED_NOTHING
        1     | 1        | null  | 1        || 0     | PredictionValue.NO_PREDICTION
        1     | 1        | 1     | null     || 0     | PredictionValue.NO_PREDICTION
        1     | 1        | null  | null     || 0     | PredictionValue.NO_PREDICTION
    }
}
