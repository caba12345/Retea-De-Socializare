package com.example.laboratorjavafx;

import com.example.laboratorjavafx.Paging.PagingRepository;
import com.example.laboratorjavafx.domain.RepoStrategy;
import com.example.laboratorjavafx.domain.User;
import com.example.laboratorjavafx.domain.validators.FriendshipValidator;
import com.example.laboratorjavafx.domain.validators.UserValidator;
import com.example.laboratorjavafx.repository.database.FriendRequestDbRepository;
import com.example.laboratorjavafx.repository.database.FriendshipDbRepository;
import com.example.laboratorjavafx.repository.memory.InMemoryRepository;
import com.example.laboratorjavafx.repository.Repository;
import com.example.laboratorjavafx.repository.database.UserDbRepository;
import com.example.laboratorjavafx.service.Service;
import com.example.laboratorjavafx.ui.UI;

import java.util.UUID;

public class  Main {
    public static void main(String[] args) {

        Repository repoUser;
        Repository repoFriendship;

        UserValidator userValidator;
        FriendshipValidator friendshipValidator;
        FriendRequestDbRepository friendRequestRepo;
        Service service;
        UI ui;


        switch(RepoStrategy.valueOf(args[0])){
            case database:
                repoUser = new UserDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Caba1234");
                repoFriendship = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Caba1234");
                userValidator = new UserValidator();
                friendshipValidator = new FriendshipValidator();
                friendRequestRepo = new FriendRequestDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Caba1234", repoUser);
                service = new Service((UserDbRepository) repoUser, userValidator, repoFriendship, friendRequestRepo, friendshipValidator);
                ui = new UI(service);
                ui.run();
                break;

            /*case memory:
            default:
                repoUser = new InMemoryRepository();
                repoFriendship = new InMemoryRepository();
                userValidator = new UserValidator();
                friendshipValidator = new FriendshipValidator();
                service = new Service(repoUser, userValidator, repoFriendship, friendshipValidator);
                ui = new UI(service);
                ui.run();
                break;*/
        }
    }
}
