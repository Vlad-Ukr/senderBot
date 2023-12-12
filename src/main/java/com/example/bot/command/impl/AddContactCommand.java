package com.example.bot.command.impl;

import com.example.bot.command.Command;
import com.example.bot.command.CommandKey;
import com.example.bot.command.WaitingCommandPool;
import com.example.bot.entity.User;
import com.example.bot.service.UserService;
import jakarta.ws.rs.NotFoundException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AddContactCommand implements Command {
    private final UserService userService;

    public AddContactCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public SendMessage executeCommand(Update update, WaitingCommandPool waitingCommandPool) {
        long chatId = update.getMessage().getChatId();
        SendMessage message = new SendMessage();
        if (!waitingCommandPool.isCommandsOnWaiting(chatId)) {
            message.setChatId(chatId);
            message.setText("Please enter the id of the user you wish to add to your mailing list.\n" +
                    "(the user should have given you their id)");
            waitingCommandPool.setCommandWait(chatId, CommandKey.ADD_CONTACT);
        } else {
            long contactId = Long.parseLong(update.getMessage().getText());
            message.setChatId(chatId);
            waitingCommandPool.stopCommandWaiting(chatId);
            User user = userService.getUser(chatId).orElseThrow();
            if (isUserAlreadyHasContact(user, contactId)) {
                message.setText("User have this contact already");
                return message;
            }
            try {
                String contactName = userService.addContact(user, contactId);
                message.setText("User have been successfully added " + contactName + " to your contacts");
            } catch (NotFoundException e) {
                message.setText("User with this id:" + contactId + " not found");
            }
        }
        return message;
    }

    private boolean isUserAlreadyHasContact(User user, long contactId) {
        return user.getContacts().stream().anyMatch(user1 -> user1.getId().equals(contactId));
    }
}
