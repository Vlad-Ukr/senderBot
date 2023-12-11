package com.example.bot.command.impl;

import com.example.bot.command.Command;
import com.example.bot.command.WaitingCommandPool;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class GetOwnIdCommand implements Command {

    @Override
    public SendMessage executeCommand(Update update, WaitingCommandPool waitingCommandPool) {
        long chatId = update.getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("This is your unique identifier, share it only with those you want to receive messages from " +
                "\n" + chatId);
        return message;
    }
}
