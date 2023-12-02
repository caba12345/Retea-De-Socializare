package com.example.laboratorjavafx.service;

import com.example.laboratorjavafx.domain.Message;

import java.util.UUID;

public interface MessageService {

    boolean addMessage(Message message);

    Message deleteMessage(UUID id);

    /**
     * @return an Iterable of all the messages
     */
    Iterable<Message> getAllMessages();
}
