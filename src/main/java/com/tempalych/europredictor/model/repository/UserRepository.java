package com.tempalych.europredictor.model.repository;

import com.tempalych.europredictor.model.entity.User;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserRepository {

    List<User> users = new ArrayList<>();

    public User getUser() {
        return users.getFirst();
    }

    public User save(User user) {
        users.add(user);
        return user;
    }

    public UserDetails findByUsername(String username) {
        for (var user: users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
}
