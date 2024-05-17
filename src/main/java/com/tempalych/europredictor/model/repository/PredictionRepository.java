package com.tempalych.europredictor.model.repository;

import com.tempalych.europredictor.model.entity.Prediction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PredictionRepository {

    List<Prediction> predictions = new ArrayList<>();

    public void save(Prediction prediction) {
        predictions.add(prediction);
    }

    public List<Prediction> findAll() {
        return predictions;
    }
}
