package com.example.laboratorjavafx;

import com.example.laboratorjavafx.domain.RepoStrategy;
import com.example.laboratorjavafx.domain.validators.FriendshipValidator;
import com.example.laboratorjavafx.domain.validators.UserValidator;
import com.example.laboratorjavafx.repository.database.FriendshipDbRepository;
import com.example.laboratorjavafx.repository.memory.InMemoryRepository;
import com.example.laboratorjavafx.repository.Repository;
import com.example.laboratorjavafx.repository.database.UserDbRepository;
import com.example.laboratorjavafx.service.Service;
import com.example.laboratorjavafx.ui.UI;

public class  Main {
    public static void main(String[] args) {

        Repository repoUser;
        Repository repoFriendship;

        switch(RepoStrategy.valueOf(args[0])){
            case database:
                repoUser = new UserDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Caba1234");
                repoFriendship = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Caba1234");
                break;

            case memory:
            default:
                repoUser = new InMemoryRepository();
                repoFriendship = new InMemoryRepository();
                break;
        }

        UserValidator userValidator = new UserValidator();
        FriendshipValidator friendshipValidator = new FriendshipValidator();
        Service service = new Service(repoUser, userValidator, repoFriendship, friendshipValidator);
        UI ui = new UI(service);

        ui.run();




    }
}
