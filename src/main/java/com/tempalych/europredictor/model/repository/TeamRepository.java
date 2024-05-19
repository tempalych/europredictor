package com.tempalych.europredictor.model.repository;

import com.tempalych.europredictor.model.entity.Team;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TeamRepository {

    List<Team> teams = new ArrayList<>(List.of(
            Team.builder().id(1L).name("Germany").group("A").build(),
            Team.builder().id(2L).name("Scotland").group("A").build(),
            Team.builder().id(3L).name("Hungary").group("A").build(),
            Team.builder().id(4L).name("Switzerland").group("A").build(),

            Team.builder().id(1L).name("Spain").group("B").build(),
            Team.builder().id(2L).name("Croatia").group("B").build(),
            Team.builder().id(3L).name("Italy").group("B").build(),
            Team.builder().id(4L).name("Albania").group("B").build(),

            Team.builder().id(1L).name("Slovenia").group("C").build(),
            Team.builder().id(2L).name("Denmark").group("C").build(),
            Team.builder().id(3L).name("Serbia").group("C").build(),
            Team.builder().id(4L).name("England").group("C").build(),

            Team.builder().id(1L).name("Poland").group("D").build(),
            Team.builder().id(2L).name("Netherlands").group("D").build(),
            Team.builder().id(3L).name("Austria").group("D").build(),
            Team.builder().id(4L).name("France").group("D").build(),

            Team.builder().id(1L).name("Belgium").group("E").build(),
            Team.builder().id(2L).name("Slovakia").group("E").build(),
            Team.builder().id(3L).name("Romania").group("E").build(),
            Team.builder().id(4L).name("Ukraine").group("E").build(),

            Team.builder().id(1L).name("Türkiye").group("F").build(),
            Team.builder().id(2L).name("Georgia").group("F").build(),
            Team.builder().id(3L).name("Portugal").group("F").build(),
            Team.builder().id(4L).name("Czechia").group("F").build()
    ));

    List<Team> findAll() {
        return teams;
    }
}
