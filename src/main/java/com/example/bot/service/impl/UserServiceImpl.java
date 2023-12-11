package com.example.bot.service.impl;

import com.example.bot.entity.User;
import com.example.bot.repository.UserRepository;
import com.example.bot.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @Override
    public void removeUser(Long id) {
        userRepository.deleteById(id);
    }
}
