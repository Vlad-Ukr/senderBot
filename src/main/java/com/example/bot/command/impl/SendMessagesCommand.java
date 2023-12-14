package com.example.bot.command.impl;

import com.example.bot.bot.Response;
import com.example.bot.command.Command;
import com.example.bot.command.WaitingCommandPool;
import com.example.bot.entity.User;
import com.example.bot.service.UserService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static com.example.bot.command.CommandKey.SEND_MESSAGE;

public class SendMessagesCommand implements Command {
    private final UserService userService;
    private List<String> callBackRequest = new LinkedList<>();

    public SendMessagesCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Response executeCommand(Update update, WaitingCommandPool waitingCommandPool) {
        long chatId;
        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        } else {
            chatId = update.getMessage().getChatId();
        }
        SendMessage message = new SendMessage();

        if (!waitingCommandPool.isCommandsOnWaiting(chatId)) {
            return Response.getResponse(handleFirstInput(chatId, message, waitingCommandPool));
        } else {
            return handleUserInput(update, chatId, waitingCommandPool);
        }
    }

    private SendMessage handleFirstInput(long chatId, SendMessage message, WaitingCommandPool waitingCommandPool) {
        List<User> users = userService.getAllUserContacts(chatId);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (User user : users) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(user.getName());
            button.setCallbackData(String.valueOf(user.getId()));
            row.add(button);
            keyboard.add(row);
        }
        keyboardMarkup.setKeyboard(keyboard);

        message.setChatId(chatId);
        message.setText("Выберите пользователей для отправки сообщения:");
        message.setReplyMarkup(keyboardMarkup);
        waitingCommandPool.setCommandWait(chatId, SEND_MESSAGE);
        return message;
    }


    private Response handleUserInput(Update update, long chatId,
                                     WaitingCommandPool waitingCommandPool) {
        if (update.hasCallbackQuery()) {
            callBackRequest.add(update.getCallbackQuery().getData());
            return Response.getEmptyResponse();
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            User user = userService.getUser(chatId).get();
            waitingCommandPool.stopCommandWaiting(chatId);
            List<SendMessage> sendMessageList = new LinkedList<>();
            for (String userId : callBackRequest) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText("U got new message from " + user.getName() + ":\n" + update.getMessage().getText());
                sendMessageList.add(sendMessage);
            }
            return Response.getResponse(sendMessageList);
        }
        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            List<PhotoSize> photos = update.getMessage().getPhoto();
            PhotoSize photo = photos.stream()
                    .max(Comparator.comparing(PhotoSize::getFileSize)).orElse(null);
            if (photo != null) {
                String fileId = photo.getFileId();
                User user = userService.getUser(chatId).get();
                waitingCommandPool.stopCommandWaiting(chatId);
                List<SendPhoto> sendPhotoList = new LinkedList<>();
                for (String userId : callBackRequest) {
                    SendPhoto sendPhoto = new SendPhoto();
                    sendPhoto.setChatId(chatId);
                    sendPhoto.setPhoto(new InputFile(fileId));
                    sendPhoto.setCaption(
                            "U got new message from " + user.getName() + ":\n" + update.getMessage().getCaption());
                    sendPhotoList.add(sendPhoto);
                }
                return Response.getResponsePhoto(sendPhotoList);
            }
        }
        return Response.getEmptyResponse();
    }

}
