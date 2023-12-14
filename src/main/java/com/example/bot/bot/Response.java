package com.example.bot.bot;

import lombok.Data;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

import java.util.List;
import java.util.Objects;

@Data
public class Response {
    private SendMessage message;
    private List<SendMessage> messageList;
    private SendPhoto sendPhoto;
    private List<SendPhoto> sendPhotoList;
    private boolean isResponseEmpty;

    public static Response getResponse(SendMessage message) {
        Response response = new Response();
        if (Objects.isNull(message)) {
            response.setResponseEmpty(true);
            return response;
        }
        response.setMessage(message);
        return response;
    }

    public static Response getResponse(List<SendMessage> messageList) {
        Response response = new Response();
        if (Objects.isNull(messageList)) {
            response.setResponseEmpty(true);
            return response;
        }
        response.setMessageList(messageList);
        return response;
    }

    public static Response getResponsePhoto(List<SendPhoto> photoList) {
        Response response = new Response();
        if (Objects.isNull(photoList)) {
            response.setResponseEmpty(true);
            return response;
        }
        response.setSendPhotoList(photoList);
        return response;
    }

    public static Response getEmptyResponse() {
        Response response = new Response();
        response.setResponseEmpty(true);
        return response;
    }

}
