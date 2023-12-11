package com.example.bot.command;

import com.example.bot.command.impl.GetOwnIdCommand;
import com.example.bot.command.impl.RemoveUserCommand;
import com.example.bot.command.impl.StartCommand;
import com.example.bot.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;

@Component
public class CommandInvoker {
    private final UserService userService;
    private final HashMap<String, Command> commandHashMap = new HashMap<>();

    public CommandInvoker(UserService userService) {
        this.userService = userService;

        injectCommands();
    }

    private void injectCommands() {
        commandHashMap.put("/start", new StartCommand(this.userService));
        commandHashMap.put("/get_id", new GetOwnIdCommand());
        commandHashMap.put("/remove_user", new RemoveUserCommand(this.userService));
    }

    public SendMessage invoke(String commandKey, Update update, WaitingCommandPool waitingCommandPool)
            throws IllegalArgumentException {
        if (commandHashMap.containsKey(commandKey)) {
            return commandHashMap.get(commandKey).executeCommand(update, waitingCommandPool);
        } else {
            throw new IllegalArgumentException();
        }
    }
}