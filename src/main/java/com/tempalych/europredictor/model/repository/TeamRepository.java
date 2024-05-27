package com.tempalych.europredictor.model.repository;

import com.tempalych.europredictor.model.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;


@Component
public interface TeamRepository extends JpaRepository<Team, Long> {

    Team findByName(String name);
}
