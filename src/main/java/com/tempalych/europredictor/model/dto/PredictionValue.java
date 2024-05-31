package com.tempalych.europredictor.model.dto;

public enum PredictionValue {
    GUESSED_EXACT_SCORE(3, "Correct score guessed"),
    GUESSED_WINNER_AND_SCORE_DIFFERENCE(2, "Guessed winner and goal difference"),
    GUESSED_WINNER(1, "Guessed winner"),
    GUESSED_DRAW_BUT_NOT_SCORE(1, "Guessed a draw, but not the score"),
    GUESSED_NOTHING(0, "Match result is not guessed"),
    NO_PREDICTION(0, "No prediction made");

    public final Integer score;
    public final String description;

    PredictionValue(Integer score, String description) {
        this.score = score;
        this.description = description;
    }
}