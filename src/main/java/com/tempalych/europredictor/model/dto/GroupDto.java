package com.tempalych.europredictor.model.dto;

import com.tempalych.europredictor.model.entity.Team;

import java.util.List;

public record GroupDto(Long id, String name, List<Team> teams, String nameWithFlags) {
}
