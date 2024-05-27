package com.tempalych.europredictor.model.repository;

import com.tempalych.europredictor.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;


@Component
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
