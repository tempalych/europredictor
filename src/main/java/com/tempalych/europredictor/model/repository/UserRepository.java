package com.tempalych.europredictor.model.repository;

import com.tempalych.europredictor.model.entity.User;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserRepository {

    List<User> users = new ArrayList<>();

    @PostConstruct
    void init() {
        var artem = User.builder()
                .id(1L)
                .username("artem")
                .password(new BCryptPasswordEncoder().encode("123"))
                .build();
        users.add(artem);
        var max = User.builder()
                .id(2L)
                .username("max")
                .password(new BCryptPasswordEncoder().encode("321"))
                .build();
        users.add(max);
    }

    public User save(User user) {
        users.add(user);
        return user;
    }

    public User findByUsername(String username) {
        for (var user: users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
}
