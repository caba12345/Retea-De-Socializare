package com.example.laboratorjavafx.service;

import com.example.laboratorjavafx.domain.Message;
import com.example.laboratorjavafx.repository.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

public class MessageService0 implements MessageService {
    private final Repository messageRepo;

    public MessageService0(Repository messageRepo) {
        this.messageRepo = messageRepo;
    }

    @Override
    public boolean addMessage(Message message) {
        try {
            Optional<Message> savedMessage = messageRepo.save(message);
            if (savedMessage.isPresent()) {
                Message m = savedMessage.get();
                // Mesajul a fost salvat cu succes
                // poți face ceva cu mesajul salvat aici, dacă este necesar
                return true;
            } else {
                System.err.println("Mesajul nu a putut fi salvat!");
                return false;
            }
        } catch (Exception e) {
            System.err.println(e);
            return false;
        }
    }

    @Override
    public Message deleteMessage(UUID id) {
        try {
            Optional<Message> deletedMessage = messageRepo.delete(id);
            if (deletedMessage.isPresent()) {
                return deletedMessage.get();
            } else {
                System.err.println("Mesajul nu a putut fi șters sau nu există!");
                return null;
            }
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

    @Override
    public Iterable<Message> getAllMessages() {
        ArrayList<Message> l =  (ArrayList) messageRepo.findAll();

        l.sort(new Comparator<Message>() {
            @Override
            public int compare(Message m1, Message m2) {
                return m1.getDate().compareTo(m2.getDate());
            }
        });

        return l;
    }
}
