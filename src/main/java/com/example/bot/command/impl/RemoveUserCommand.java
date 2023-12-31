package com.example.bot.command.impl;

import com.example.bot.bot.Response;
import com.example.bot.command.Command;
import com.example.bot.command.WaitingCommandPool;
import com.example.bot.service.UserService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class RemoveUserCommand implements Command {
    private final UserService userService;

    public RemoveUserCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Response executeCommand(Update update, WaitingCommandPool waitingCommandPool) {
        long chatId = update.getMessage().getChatId();
        SendMessage message = new SendMessage();
        if (userService.getUser(chatId).isEmpty()) {
            message.setChatId(chatId);
            message.setText("You are not registered");
            return Response.getResponse(message);
        }
        userService.removeUser(chatId);
        message.setChatId(update.getMessage().getChatId());
        message.setText("You have been removed from the bot's database. If you want to use the bot again," +
                " just enter the /start command\n");
        return Response.getResponse(message);
    }
}
