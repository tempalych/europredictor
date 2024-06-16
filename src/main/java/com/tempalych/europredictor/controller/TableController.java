package com.tempalych.europredictor.controller;

import com.tempalych.europredictor.model.dto.ScoreTableLine;
import com.tempalych.europredictor.service.TableService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@AllArgsConstructor
@Controller
@RequestMapping
public class TableController {

    private final TableService tableService;

    @GetMapping("/table")
    public String getTable(Model model) {

        var chartDatasetLinesAndLabels = tableService.getChartDatasetLinesAndLabels();
        model.addAttribute("users", chartDatasetLinesAndLabels.getFirst());
        model.addAttribute("labels", chartDatasetLinesAndLabels.getSecond());

        var scoreTableLines = new ArrayList<ScoreTableLine>();

        for (int i = chartDatasetLinesAndLabels.getFirst().size() - 1; i >= 0; i--) {
            var chartLine = chartDatasetLinesAndLabels.getFirst().get(i);
            scoreTableLines.add(new ScoreTableLine(chartLine.name(), chartLine.points().getLast().longValue()));
        }
        model.addAttribute("players", scoreTableLines);
        return "table/table";
    }
    
}
