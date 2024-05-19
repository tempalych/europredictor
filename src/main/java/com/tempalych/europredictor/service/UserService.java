package com.tempalych.europredictor.service;

import com.tempalych.europredictor.model.entity.User;
import com.tempalych.europredictor.model.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    UserRepository userRepository;

    public User getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var username = ((UserDetails) auth.getPrincipal()).getUsername();
        return userRepository.findByUsername(username);
    }
}
