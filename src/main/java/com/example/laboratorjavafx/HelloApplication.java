package com.example.laboratorjavafx;

import com.example.laboratorjavafx.controllers.LogIn;
import com.example.laboratorjavafx.domain.validators.FriendshipValidator;
import com.example.laboratorjavafx.domain.validators.UserValidator;
import com.example.laboratorjavafx.repository.database.FriendRequestDbRepository;
import com.example.laboratorjavafx.repository.database.MessageDbRepository;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import com.example.laboratorjavafx.repository.database.FriendshipDbRepository;
import com.example.laboratorjavafx.repository.Repository;
import com.example.laboratorjavafx.repository.database.UserDbRepository;
import com.example.laboratorjavafx.service.Service;
import com.example.laboratorjavafx.service.MessageService;


import java.io.IOException;

public class HelloApplication extends Application {

    private Color mainColorTheme;
    private Service service;
    private MessageService messageService;
    public void setService(Service service) {
        this.service = service;
    }
    public Service getService() {
        return service;
    }

    public Color getMainColorTheme() {
        return mainColorTheme;
    }

    public void setMainColorTheme(Color mainColorTheme) {
        this.mainColorTheme = mainColorTheme;
    }
    @Override
    public void start(Stage primarystage) throws Exception {

        FriendshipValidator friendshipValidator = new FriendshipValidator();
        UserValidator userValidator = new UserValidator();

        UserDbRepository userRepo = new UserDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Caba1234");
        FriendshipDbRepository friendshipRepo = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Caba1234");
        FriendRequestDbRepository friendRequestRepo = new FriendRequestDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Caba1234", userRepo);
        MessageDbRepository messageRepository = new MessageDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Caba1234", userRepo);
        service = new Service(userRepo, userValidator, friendshipRepo, friendRequestRepo, friendshipValidator);
        messageService = new MessageService(messageRepository, userRepo);

        initview(primarystage);
        primarystage.show();
    }

    private void initview(Stage primarystage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("LogIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        LogIn loginController = fxmlLoader.getController();
        loginController.setService(this.service);
        loginController.setMessageService(this.messageService);

        primarystage.setTitle("Log In");
        primarystage.setScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}