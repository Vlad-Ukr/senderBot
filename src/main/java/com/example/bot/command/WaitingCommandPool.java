package com.example.bot.command;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WaitingCommandPool {
    private Map<Long, CommandKey> commandKeyMap = new ConcurrentHashMap<>();

    public void setCommandWait(long chatId, CommandKey commandKey) {
        commandKeyMap.put(chatId, commandKey);
    }

    public boolean isCommandsOnWaiting(long chatId) {
        return commandKeyMap.containsKey(chatId);
    }

    public String getCommand(long chatId) {
        return commandKeyMap.get(chatId).getKey();
    }

    public void stopCommandWaiting(long chatId) {
        commandKeyMap.remove(chatId);
    }
}
