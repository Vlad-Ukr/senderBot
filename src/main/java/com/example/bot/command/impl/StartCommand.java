package com.example.bot.command.impl;

import com.example.bot.command.Command;
import com.example.bot.command.CommandKey;
import com.example.bot.command.WaitingCommandPool;
import com.example.bot.entity.User;
import com.example.bot.service.UserService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class StartCommand implements Command {
    private final UserService userService;

    public StartCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public SendMessage executeCommand(Update update, WaitingCommandPool waitingCommandPool) {
        long chatId = update.getMessage().getChatId();
        SendMessage message = new SendMessage();
        if (!waitingCommandPool.isCommandsOnWaiting(chatId)) {
            message.setChatId(chatId);
            message.setText("Hello, Please enter your real First and Last Name. It will be used by other bot users");
            waitingCommandPool.setCommandWait(chatId, CommandKey.START);
        } else {
            String name = update.getMessage().getText();
            userService.saveUser(new User(chatId, name));
            waitingCommandPool.stopCommandWaiting(chatId);
            message.setChatId(chatId);
            message.setText("You have been successfully logged in");
        }
        return message;
    }
}
