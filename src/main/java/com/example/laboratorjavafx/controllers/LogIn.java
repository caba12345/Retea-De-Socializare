package com.example.laboratorjavafx.controllers;


import com.example.laboratorjavafx.domain.validators.FriendshipValidator;
import com.example.laboratorjavafx.domain.validators.UserValidator;
import com.example.laboratorjavafx.repository.Repository;
import com.example.laboratorjavafx.repository.database.FriendshipDbRepository;
import com.example.laboratorjavafx.repository.database.UserDbRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import com.example.laboratorjavafx.domain.User;
import com.example.laboratorjavafx.service.MessageService;
import com.example.laboratorjavafx.service.Service;
import com.example.laboratorjavafx.controllers.UserController;
import com.example.laboratorjavafx.HelloApplication;



import java.io.IOException;

import static com.example.laboratorjavafx.repository.database.UserDbRepository.hashPassword;

public class LogIn {
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;
    @FXML
    private Text emailErrorText;
    @FXML
    private Text passwordErrorText;


    private Service service;

    private MessageService messageService;

    public void setService(Service service) {
        this.service = service;
    }

    public Service getService() {
        return service;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @FXML
    protected void onLogInButtonCLick(ActionEvent event) throws Exception {
        User user = service.getUserByEmail(email.getText());
        System.out.println(user);

        if(user == null) // show a message
        {
            emailErrorText.setVisible(true);
            passwordErrorText.setVisible(false);
        }
        else if(!password.getText().equals(user.getPassword()) && !hashPassword(password.getText()).equals(user.getPassword())) { // show a message
            passwordErrorText.setVisible(true);
            emailErrorText.setVisible(false);
        }
        else { // enter Application
            emailErrorText.setVisible(false);
            passwordErrorText.setVisible(false);



            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("UserInterface.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load());

            UserController controller = fxmlLoader.getController();
            controller.setService(this.service);
            controller.setMessageService(messageService);

            stage.setTitle("User Management");
            stage.setScene(scene);

            controller.initApp(user);
            stage.show();
        }
    }


    @FXML
    public void onSignInClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("SignIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);

        SignUp signUpController = fxmlLoader.getController();
        signUpController.setService(this.service);
        signUpController.setMessageService(messageService);
        stage.show();
    }

    public void onTextChanged(KeyEvent evt) {
        emailErrorText.setVisible(false);
        passwordErrorText.setVisible(false);
    }

    public void onPasswordChanged(KeyEvent evt) {
        passwordErrorText.setVisible(false);
    }
}
