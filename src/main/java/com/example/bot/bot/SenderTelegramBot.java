package com.example.bot.bot;

import com.example.bot.command.CommandInvoker;
import com.example.bot.command.WaitingCommandPool;
import com.example.bot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Objects;

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
            long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            try {
                if (waitingCommandPool.isCommandsOnWaiting(chatId)) {
                    executeResponse(
                            commandInvoker.invoke(waitingCommandPool.getCommand(chatId), update, waitingCommandPool));
                } else {
                    executeResponse(commandInvoker.invoke(messageText, update, waitingCommandPool));
                }
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
        } else if (update.hasCallbackQuery()) {
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            try {
                executeResponse(
                        commandInvoker.invoke(waitingCommandPool.getCommand(chatId), update, waitingCommandPool));
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
            long chatId = update.getMessage().getChatId();
            try {
                executeResponse(
                        commandInvoker.invoke(waitingCommandPool.getCommand(chatId), update, waitingCommandPool));
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
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

    private void executeResponse(Response response) throws TelegramApiException {
        if (response.isResponseEmpty()) {
            return;
        }
        if (Objects.nonNull(response.getMessage())) {
            execute(response.getMessage());
        } else if (Objects.nonNull(response.getMessageList())) {
            List<SendMessage> messageList = response.getMessageList();
            messageList.stream().forEach(message -> {
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            });
        } else if (Objects.nonNull(response.getSendPhotoList())) {
            List<SendPhoto> photoList = response.getSendPhotoList();
            photoList.stream().forEach(photo -> {
                try {
                    execute(photo);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}