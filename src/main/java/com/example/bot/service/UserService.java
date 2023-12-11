package com.example.bot.service;

import com.example.bot.entity.User;

public interface UserService {

    User saveUser(User user);

    User getUser(Long id);

    void removeUser(Long id);
}
