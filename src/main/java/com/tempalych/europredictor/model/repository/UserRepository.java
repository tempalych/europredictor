package com.tempalych.europredictor.model.repository;

import com.tempalych.europredictor.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserRepository {

    User user = User.builder().id(1L).username("artem").build();

    public User getUser() {
        return user;
    }
}
