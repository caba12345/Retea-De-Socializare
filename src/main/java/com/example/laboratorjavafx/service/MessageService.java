package com.example.laboratorjavafx.service;

import com.example.laboratorjavafx.domain.Message;
import com.example.laboratorjavafx.repository.Repository;
import com.example.laboratorjavafx.domain.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessageService {
    private final Repository<UUID, Message> messageRepo;

    private final Repository<UUID, User> userRepo;

    public MessageService(Repository<UUID, Message> messageRepo, Repository<UUID, User> userRepo) {
        this.messageRepo = messageRepo;
        this.userRepo=userRepo;
    }

    public boolean addMessage(Message message) {
        Optional<Message> existingMessage = messageRepo.save(message);

        if (existingMessage.isPresent()) {
            System.err.println("A message with this ID already exists!");
            return false;
        }

        return true;
    }

    public Message deleteMessage(UUID id) {
        Optional<Message> deletedMessage = messageRepo.delete(id);

        if (deletedMessage.isPresent()) {
            return deletedMessage.get();
        } else {
            // Handle the case where the message with the given ID doesn't exist.
            return null;
        }
    }

    public List<Message> getAllMessages() {
        ArrayList<Message> l = new ArrayList<>((Collection) messageRepo.findAll());

        l.sort(Comparator.comparing(Message::getData));

        return l;
    }


    public void addMessage(User from, List<User> to, String message) {
        // Create a new message
        Message newMessage = new Message(from, to, message, LocalDateTime.now());

        // Save the new message
        messageRepo.save(newMessage);

        // Set reply for specific conditions
        /*for (Message existingMessage : messageRepo.findAll()) {
            if (to.contains(existingMessage.getFrom()) &&
                    existingMessage.getTo().contains(from) &&
                    existingMessage.getReply() == null) {

                existingMessage.setReply(newMessage);
                messageRepo.update(existingMessage);
            }
        }
         */

        System.out.println(newMessage);
    }

    public void addMessageReply(User from, List<User> to, String reply, Message selectedMessage) {
        // Create a new message
        Message newMessage = new Message(from, to, reply, LocalDateTime.now());

        // Save the new message
        newMessage.setReply(selectedMessage);
        messageRepo.save(newMessage);

        // Set reply for specific conditions
        /*for (Message existingMessage : messageRepo.findAll()) {
            if (to.contains(existingMessage.getFrom()) &&
                    existingMessage.getTo().contains(from) &&
                    existingMessage.getReply() == null) {

                existingMessage.setReply(newMessage);
                messageRepo.update(existingMessage);
            }
        }
         */

        System.out.println(newMessage);
    }






    public List<Message> conversation(UUID id1, UUID id2) {
        User user1 = userRepo.findOne(id1).orElseThrow(() -> new IllegalArgumentException("User with id " + id1 + " not found"));
        User user2 = userRepo.findOne(id2).orElseThrow(() -> new IllegalArgumentException("User with id " + id2 + " not found"));

        return StreamSupport.stream(messageRepo.findAll().spliterator(), false)
                .filter(msg -> isConversationMessage(msg, user1, user2))
                .sorted(Comparator.comparing(Message::getData))
                .collect(Collectors.toList());
    }

    private boolean isConversationMessage(Message message, User user1, User user2) {
        return (message.getTo().contains(user2) && message.getFrom().equals(user1)) ||
                (message.getTo().contains(user1) && message.getFrom().equals(user2));
    }



}

