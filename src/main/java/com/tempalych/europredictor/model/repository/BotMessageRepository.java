package com.tempalych.europredictor.model.repository;

import com.tempalych.europredictor.model.entity.BotMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BotMessageRepository extends JpaRepository<BotMessage, Long> {

    List<BotMessage> findByTimeSentIsNull();
}
