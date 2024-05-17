package com.tempalych.europredictor.model.repository;

import com.tempalych.europredictor.model.entity.Team;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TeamRepository {

    List<Team> teams = new ArrayList<>(List.of(
            Team.builder().id(1L).name("Germany").build(),
            Team.builder().id(2L).name("Scotland").build(),
            Team.builder().id(3L).name("Spain").build(),
            Team.builder().id(4L).name("Norway").build()
    ));

    List<Team> findAll() {
        return teams;
    }
}
