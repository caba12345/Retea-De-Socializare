package com.example.laboratorjavafx.repository.file;

import com.example.laboratorjavafx.domain.Message;
import com.example.laboratorjavafx.domain.User;
import com.example.laboratorjavafx.domain.validators.Validator;
import com.example.laboratorjavafx.repository.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MessageFile extends AbstractFileRepository<UUID, Message>{

    Repository<UUID, User> userRepository;
    public MessageFile(String fileName, Repository<UUID, User> userRepository) {
        super(fileName);
        this.userRepository = userRepository;
        super.loadData();
    }

    @Override
    public Message extractEntity(List<String> attributes) {
        // Get the sender
        Optional<User> optionalSender = userRepository.findOne(UUID.fromString(attributes.get(1)));
        User sender = optionalSender.orElse(null);

        // Get the receiver
        Optional<User> optionalReceiver = userRepository.findOne(UUID.fromString(attributes.get(2)));
        User receiver = optionalReceiver.orElse(null);

        Message entity = new Message(attributes.get(3) ,sender, receiver);
        entity.setId(UUID.fromString(attributes.get(0)));
        entity.setDate(LocalDateTime.parse(attributes.get((4))));

        return entity;
    }

    @Override
    protected String createEntityAsString(Message entity) {
        return entity.getId() + ";" + entity.getSender().getId() + ";" + entity.getReciver().getId() + ";" + entity.getText() + ";" + entity.getDate();
    }
}
