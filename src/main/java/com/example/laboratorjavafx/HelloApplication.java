package com.example.laboratorjavafx;

import com.example.laboratorjavafx.controllers.UserController;
import com.example.laboratorjavafx.domain.validators.FriendshipValidator;
import com.example.laboratorjavafx.domain.validators.UserValidator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.laboratorjavafx.repository.FriendshipDbRepository;
import com.example.laboratorjavafx.repository.Repository;
import com.example.laboratorjavafx.repository.UserDbRepository;
import com.example.laboratorjavafx.service.Service;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FriendshipValidator friendshipValidator = new FriendshipValidator();
        UserValidator userValidator = new UserValidator();

        Repository userRepo = new UserDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Caba1234");
        Repository friendshipRepo = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Caba1234");
        Service serviceUserFriendship = new Service(userRepo, userValidator, friendshipRepo, friendshipValidator);

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("UserInterface.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        UserController controller = fxmlLoader.getController();
        controller.setService(serviceUserFriendship);

        stage.setTitle("User Management");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}