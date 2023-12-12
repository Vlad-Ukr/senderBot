package com.example.bot.service;

import com.example.bot.entity.User;
import jakarta.ws.rs.NotFoundException;

import java.util.Optional;

public interface UserService {

    User saveUser(User user);

    Optional<User> getUser(Long id);

    void removeUser(Long id);

    /**
     * Returns a name of added contact
     *
     * @param user      current user
     * @param contactId id of adding contact
     * @return name of added contact
     */
    String addContact(User user, long contactId) throws NotFoundException;

    /**
     * Returns a name of removed contact
     *
     * @param user      current user
     * @param contactId id of removing contact
     * @return name of added contact
     */
    String removeContact(User user, long contactId) throws NotFoundException;
}
