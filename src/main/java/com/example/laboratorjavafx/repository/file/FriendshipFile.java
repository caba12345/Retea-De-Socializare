package com.example.laboratorjavafx.repository.file;

import com.example.laboratorjavafx.domain.FriendRequest;
import com.example.laboratorjavafx.domain.FriendShip;
import com.example.laboratorjavafx.domain.User;
import com.example.laboratorjavafx.domain.validators.Validator;
import com.example.laboratorjavafx.repository.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FriendshipFile extends AbstractFileRepository<UUID, FriendShip>{

    Repository<UUID, User> userRepository;
    public FriendshipFile(String fileName, Repository<UUID, User> userRepository) {
        super(fileName);
        this.userRepository = userRepository;
        super.loadData();
    }

    @Override
    public FriendShip extractEntity(List<String> attributes) {

        // luam primul user
        Optional<User> optionalUser = userRepository.findOne(UUID.fromString(attributes.get(1)));
        User u1 = optionalUser.orElse(null);

        // Verificarea dacă obiectul Optional conține o valoare și apoi extragerea User-ului, dacă există
        if (optionalUser.isPresent()) {
            u1 = optionalUser.get();
        } else {
            // Dacă obiectul Optional nu conține o valoare, afișează un mesaj sau gestionează cazul în alt mod
            System.out.println("User not found");
        }

        // luam al doilea user
        optionalUser = userRepository.findOne(UUID.fromString(attributes.get(1)));
        User u2 = optionalUser.orElse(null);

        // Verificarea dacă obiectul Optional conține o valoare și apoi extragerea User-ului, dacă există
        if (optionalUser.isPresent()) {
            u2 = optionalUser.get();
        } else {
            // Dacă obiectul Optional nu conține o valoare, afișează un mesaj sau gestionează cazul în alt mod
            System.out.println("User not found");
        }

        FriendShip entity = new FriendShip(u1, u2);
        entity.setId(UUID.fromString(attributes.get(0)));
        entity.setFriendsFrom(LocalDateTime.parse(attributes.get((3))));

        if(FriendRequest.valueOf(attributes.get((4))).equals(FriendRequest.ACCEPTED))
        { // we add them to each-other's friendliest
            u1.addFriend(u2);
            u2.addFriend(u1);
        }
        entity.setRequest(FriendRequest.valueOf(attributes.get((4))));

        return entity;
    }

    @Override
    protected String createEntityAsString(FriendShip entity) {
        return entity.getId() + ";" + entity.getUser1().getId() + ";" + entity.getUser2().getId() + ";" + entity.getFriendsFrom() + ";" + entity.getRequest();
    }
}
