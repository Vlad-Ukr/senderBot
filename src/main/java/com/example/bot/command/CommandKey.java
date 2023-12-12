package com.example.bot.command;

public enum CommandKey {
    START("/start"), GET_ID("/get_id"), REMOVE_USER("/remove_user"), ADD_CONTACT("/add_contact"),
    REMOVE_CONTACT("/remove_contact");
    private final String key;

    CommandKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
