package com.example.bot.repository;

import com.example.bot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM user_contacts WHERE user_id =:userId AND contact_id =:contactId")
    void removeContact(@Param("userId") long userId, @Param("contactId") long contactId);

    @Query(nativeQuery = true, value =
            "SELECT * FROM users INNER JOIN user_contacts ON users.id = user_contacts.contact_id" +
                    " WHERE user_id=?1")
    List<User> findUsersContactsByUserId(long userId);
}

