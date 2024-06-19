package com.tempalych.europredictor.model.repository;

import com.tempalych.europredictor.model.entity.ApiCall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface ApiCallRepository extends JpaRepository<ApiCall, Long> {

    ApiCall findByCallDate(@Param("callDate")LocalDate callDate);
}
