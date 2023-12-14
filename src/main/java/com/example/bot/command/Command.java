package com.example.bot.command;

import com.example.bot.bot.Response;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {
    Response executeCommand(Update update, WaitingCommandPool waitingCommandPool);
}
