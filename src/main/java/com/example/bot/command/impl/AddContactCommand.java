package com.example.bot.command.impl;

import com.example.bot.bot.Response;
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
    public Response executeCommand(Update update, WaitingCommandPool waitingCommandPool) {
        long chatId = update.getMessage().getChatId();
        SendMessage message = new SendMessage();


        if (!waitingCommandPool.isCommandsOnWaiting(chatId)) {
            return Response.getResponse(
                    handleFirstInput(chatId, message, waitingCommandPool));
        } else {
            return Response.getResponse(
                    handleUserInput(update, chatId, message, waitingCommandPool));
        }

    }

    private SendMessage handleFirstInput(long chatId, SendMessage message, WaitingCommandPool waitingCommandPool) {
        message.setChatId(chatId);
        message.setText("Please enter the id of the user you wish to add to your mailing list.\n" +
                "(the user should have given you their id)");
        waitingCommandPool.setCommandWait(chatId, CommandKey.ADD_CONTACT);
        return message;
    }

    private SendMessage handleUserInput(Update update, long chatId, SendMessage message,
                                        WaitingCommandPool waitingCommandPool) {
        long contactId = Long.parseLong(update.getMessage().getText());
        message.setChatId(chatId);
        waitingCommandPool.stopCommandWaiting(chatId);
        User user = userService.getUser(chatId).orElseThrow();

        if (chatId == contactId) {
            message.setText("You cannot add yourself to your contacts");
            return message;
        }

        if (isUserAlreadyHasContact(user, contactId)) {
            message.setText("User already has this contact");
            return message;
        }

        return addUserContact(message, user, contactId);
    }

    private boolean isUserAlreadyHasContact(User user, long contactId) {
        return user.getContacts().stream().anyMatch(user1 -> user1.getId().equals(contactId));
    }

    private SendMessage addUserContact(SendMessage message, User user, long contactId) {
        try {
            String contactName = userService.addContact(user, contactId);
            message.setText("User has been successfully added " + contactName + " to your contacts");
        } catch (NotFoundException e) {
            message.setText("User with this id:" + contactId + " not found");
        }
        return message;
    }
}
