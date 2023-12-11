package com.example.bot.command;

public enum CommandKey {
    START("/start");
    private final String key;

    CommandKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
