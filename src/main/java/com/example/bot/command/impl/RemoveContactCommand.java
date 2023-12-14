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

public class RemoveContactCommand implements Command {
    private final UserService userService;

    public RemoveContactCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Response executeCommand(Update update, WaitingCommandPool waitingCommandPool) {
        long chatId = update.getMessage().getChatId();
        SendMessage message = new SendMessage();
        if (!waitingCommandPool.isCommandsOnWaiting(chatId)) {
            message.setChatId(chatId);
            message.setText("Please enter the id of the user you wish to remove from contacts");
            waitingCommandPool.setCommandWait(chatId, CommandKey.REMOVE_CONTACT);
        } else {
            long contactId = Long.parseLong(update.getMessage().getText());
            message.setChatId(chatId);
            waitingCommandPool.stopCommandWaiting(chatId);
            User user = userService.getUser(chatId).orElseThrow();
            try {
                String contactName = userService.removeContact(user, contactId);
                message.setText("User have been successfully removed " + contactName + " from your contacts");
            } catch (NotFoundException e) {
                message.setText("User with this id:" + contactId + " not found");
            }
        }
        return Response.getResponse(message);
    }
}
