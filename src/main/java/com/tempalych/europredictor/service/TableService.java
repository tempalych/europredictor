package com.tempalych.europredictor.service;

import com.tempalych.europredictor.model.dto.ChartDatasetLine;
import com.tempalych.europredictor.model.repository.PredictionRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor
@Service
public class TableService {

    private final PredictionRepository predictionRepository;

    private final static List<String> COLORS = List.of(
            "rgb(48, 106, 131, 1)",
            "rgb(216, 182, 101, 1)",
            "rgb(83, 62, 95, 1)",
            "rgb(153, 193, 105, 1)",
            "rgb(176, 75, 54, 1)",
            "rgb(241, 164, 100, 1)",
            "rgb(159, 219, 208, 1)"
    );

    public Pair<List<ChartDatasetLine>, Set<String>> getChartDatasetLinesAndLabels() {
        var predictionsWithValues = predictionRepository.findAllPredictionsWithValues();
        var labels = new LinkedHashSet<>(Set.of("🇪🇺"));
        var chartDatasetLines = new ArrayList<ChartDatasetLine>();
        var userScoresListByMatch = new HashMap<String, List<Integer>>();
        for (var prediction: predictionsWithValues) {
            var username = prediction.getUser().getUsername();
            if ("admin".equals(username)) {
                username = "🤖";
            }
            if (!userScoresListByMatch.containsKey(username)) {
                userScoresListByMatch.put(username, new ArrayList<>(List.of(0)));
            }
            var userScores = userScoresListByMatch.get(username);
            var currentScore = userScores.isEmpty() ? 0 : userScores.getLast();
            var newScore = currentScore + prediction.getPredictionValueScore();
            userScores.add(newScore);
            var matchLabel = prediction.getMatch().getHomeTeam().getFlag() + prediction.getMatch().getVisitorTeam().getFlag();
            labels.add(matchLabel);
        }

        int colorIdx = 0;
        for (var entry: userScoresListByMatch.entrySet()) {
            var color = colorIdx < COLORS.size() - 1 ? COLORS.get(colorIdx) : "0, 0, 0, 1";
            chartDatasetLines.add(new ChartDatasetLine(entry.getKey(), entry.getValue(), color));
            colorIdx++;
        }
        chartDatasetLines.sort(Comparator.comparingInt(it -> it.points().getLast()));
        return Pair.of(chartDatasetLines, labels);
    }
}
