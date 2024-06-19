package com.tempalych.europredictor.utils;

import java.util.List;
import java.util.Map;

public class TeamNamingUtils {

    public static final Map<String, List<String>> teamNames = Map.of(
            "Czechia", List.of("Czech Republic"),
            "Türkiye", List.of("T\\u00fcrkiye")
    );
}
