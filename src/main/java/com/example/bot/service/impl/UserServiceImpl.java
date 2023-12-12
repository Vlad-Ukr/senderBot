package com.example.bot.service.impl;

import com.example.bot.entity.User;
import com.example.bot.repository.UserRepository;
import com.example.bot.service.UserService;
import jakarta.ws.rs.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public void removeUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    @Override
    public String addContact(User user, long contactId) {
        User contact = userRepository.findById(contactId).orElseThrow(NotFoundException::new);
        user.addContact(contact);
        userRepository.save(user);
        return contact.getName();
    }

    @Override
    public String removeContact(User user, long contactId) throws NotFoundException {
        User contact = userRepository.findById(contactId).orElseThrow(NotFoundException::new);
        user.removeContact(contact);
        userRepository.removeContact(user.getId(), contactId);
        return contact.getName();
    }
}
