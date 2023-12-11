package com.example.bot.bot;

import com.example.bot.command.CommandInvoker;
import com.example.bot.command.WaitingCommandPool;
import com.example.bot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class SenderTelegramBot extends TelegramLongPollingBot {
    private final BotConfig config;
    private final CommandInvoker commandInvoker;
    private final WaitingCommandPool waitingCommandPool;


    public SenderTelegramBot(BotConfig config, CommandInvoker commandInvoker, WaitingCommandPool waitingCommandPool) {
        this.config = config;
        this.commandInvoker = commandInvoker;
        this.waitingCommandPool = waitingCommandPool;
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            try {
                if (waitingCommandPool.isCommandsOnWaiting(chatId)) {
                    execute(commandInvoker.invoke(waitingCommandPool.getCommand(chatId), update, waitingCommandPool));
                } else {
                    execute(commandInvoker.invoke(messageText, update, waitingCommandPool));
                }
            } catch (Exception exception) {
                log.info("Unexpected message");
            }
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

}