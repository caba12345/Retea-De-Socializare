package com.example.laboratorjavafx.controllers;

import com.example.laboratorjavafx.Paging.Page;
import com.example.laboratorjavafx.Paging.Pageable;
import com.example.laboratorjavafx.domain.FriendRequest;
import com.example.laboratorjavafx.domain.FriendShip;
import com.example.laboratorjavafx.domain.Message;
import com.example.laboratorjavafx.domain.User;
import com.example.laboratorjavafx.service.Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.example.laboratorjavafx.service.MessageService;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class UserController implements Initializable {
    @FXML
    private Text username, pageSizeText;
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, String> emailColumn, mesajColumn, numeColumn, dataColumn;
    @FXML
    private TextField firstNameField, lastNameField, emailField, passwordField, getMesajField;
    @FXML
    private ListView<String> friendRequestsSent, friendsList, friendsRequestList, userList;
    @FXML
    private TableView<Message> conversatii;

    @FXML
    private Button addButton, updateButton, deleteButton, btnAdMesaj, previousButton, nextButton;

    private final ObservableList<String> friendsObs = FXCollections.observableArrayList();

    private final ObservableList<String> friendsReqObs = FXCollections.observableArrayList();

    private final ObservableList<String> friendsReqSentObs = FXCollections.observableArrayList();

    private final ObservableList<String> userObs = FXCollections.observableArrayList();
    private MessageService messageService;
    private Service service;
    private int currentPage = 0;
    private int pageSize = 5;
    private int totalNumberOfElements = 0;
    private User user;
    private ObservableList<User> users = FXCollections.observableArrayList();


    public MessageService getMessageService() {
        return messageService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public void setService(Service service) {
        this.service = service;
        initializeTable();
        loadUsers();
        initModel();
    }

    private void initModel()
    {

        Page<User> page = service.findAll(new Pageable(currentPage, pageSize));

        int maxPage = (int) Math.ceil((double) page.getTotalElementCount() / pageSize)-1;
        if(maxPage == -1)
        {
            maxPage = 0;
        }
        if(currentPage > maxPage){
            currentPage = maxPage;
            page = service.findAll(new Pageable(currentPage, pageSize));
        }

        users.setAll(StreamSupport.stream(page.getElementsOnPage().spliterator(),
                false).collect(Collectors.toList()));
        totalNumberOfElements=page.getTotalElementCount();

        previousButton.setDisable(currentPage == 0);
        nextButton.setDisable((currentPage+1)*pageSize >+ totalNumberOfElements);
        usersTable.refresh();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usersTable.setItems(users);
        usersTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); // pot selecta mai multi useri
        friendsList.setItems(friendsObs);
        userList.setItems(userObs);
        friendsRequestList.setItems(friendsReqObs);
        //friendRequestsSent.setItems(friendsReqSentObs);
    }

    private void initializeTable() {
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        usersTable.setItems(users);
    }

    public void initApp(User user) {
        this.user = user;
        username.setText(user.getFirstName() + " " + user.getLastName());
        pageSizeText.setText(String.valueOf(pageSize));

        //user.getFriends().forEach(u -> friendsObs.add(u.getFirstName() + " " + u.getLastName() + " " + u.getEmail())); nush dc nu merge

        service.getAllFriendships().forEach(f -> {
            FriendShip friendShip = (FriendShip) f;
            if(friendShip.getUser1().getId().equals(user.getId()))
                friendsObs
                        .add(friendShip.getUser2().getFirstName() + " " + friendShip.getUser2().getLastName() +
                                " " + friendShip.getUser2().getEmail());
            if(friendShip.getUser2().getId().equals(user.getId()))
                friendsObs
                        .add(friendShip.getUser1().getFirstName() + " " + friendShip.getUser1().getLastName() +
                                " " + friendShip.getUser1().getEmail());
        });

        service.getAllFriendrequests().forEach(f -> {
            FriendRequest friendRequest = (FriendRequest) f;
            if(friendRequest.getToUser().getId().equals(user.getId()))
                friendsReqObs
                        .add(friendRequest.getFromUser().getFirstName() + " " + friendRequest.getFromUser().getLastName() +
                                " " + friendRequest.getFromUser().getEmail() + " " + friendRequest.getRequestFrom() + " " + friendRequest.getStatus());
        });

        service.getAllUsers().forEach(u -> {
            User user1 = (User) u;
            AtomicReference<String> additionalMessage = new AtomicReference<>("");
            if(user1.equals(this.user))
                additionalMessage.set("YOU");

            this.user.getFriends().forEach(u2 -> {
                User user2 = (User) u2;
                if(u2.equals(u))
                    additionalMessage.set("FRIEND");
            });

            userObs.add(user1.getFirstName() + " " + user1.getLastName() + " " + user1.getEmail() + " " + additionalMessage);
        });

        service.getAllFriendships().forEach(f -> {
            FriendShip friendShip = (FriendShip) f;
            if(friendShip.getUser1().getId().equals(user.getId()))
                friendsReqSentObs
                        .add(friendShip.getUser2().getFirstName() + " " + friendShip.getUser2().getLastName() +
                                " " + friendShip.getUser2().getEmail() + " " + friendShip.getFriendsFrom() + " "
                                + friendShip.getRequest());
        });
    }


    public void removeFriend() {
        if(friendsList.getSelectionModel().getSelectedItem() == null)
            return;

        String userInfo = friendsList.getSelectionModel().getSelectedItem().toString();
        String email = userInfo.split(" ")[2];
        service.deleteFriendship(email, user.getEmail());

        friendsObs.remove(userInfo);
        friendsReqObs.removeIf(line -> {
            return line.split(" ")[2].equals(email);
        });
    }
    private void loadUsers() {
        users.clear();
        for (User user : service.getAllUsers()) {
            users.add(user);
        }
    }

    @FXML
    private void addUser() {
        try {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            // Validate inputs
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()){
                showAlert("Input Error", "First name, last name and email cannot be empty.");
                return;
            }
            User user = new User(firstName, lastName, email, password);
            service.addUser(user);
            loadUsers();
            clearForm();
            userObs.add(user.getFirstName() + " " + user.getLastName() + " " + user.getEmail());
            userList.refresh();
            initModel();
            showConfirmation("User Added", "User successfully added.");
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

// Similar changes for updateUser and deleteUser

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        passwordField.clear();
    }

    @FXML
    private void updateUser() {
        try {
            User selectedUser = usersTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                String newFirstName = firstNameField.getText();
                String newLastName = lastNameField.getText();
                String newemail = emailField.getText();
                service.updateUser(selectedUser.getId(), newFirstName, newLastName, newemail);
                loadUsers();
            }
        } catch (Exception e) {
            e.printStackTrace(); // Replace with proper error handling
        }
    }

    @FXML
    private void deleteUser() {
        try {
            String selectedUser = userList.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                String[] parts = selectedUser.split(" ");
                service.deleteUser(parts[2]);

                // Identifică utilizatorul în userObs și elimină-l
                Iterator<String> iterator = userObs.iterator();
                while (iterator.hasNext()) {
                    String user = iterator.next();
                    if (user.contains(parts[2])) {
                        iterator.remove();
                        break; // După ce am găsit și eliminat utilizatorul, ieșim din buclă
                    }
                }

                userList.refresh(); // Reîmprospătare ListView
            }
        } catch (Exception e) {
            e.printStackTrace(); // Înlocuiește cu gestionarea adecvată a erorilor
        }
    }

    public void acceptFriendRequest(ActionEvent actionEvent) {
        if(friendsRequestList.getSelectionModel().getSelectedItem() == null)
            return;

        String userInfo = friendsRequestList.getSelectionModel().getSelectedItem().toString();
        String email = userInfo.split(" ")[2];
        String status = userInfo.split(" ")[4];

        if(!status.equals("PENDING"))
            return;

        service.acceptFriendRequest(email, user.getEmail());

        friendsObs.removeAll(friendsObs.stream().toList());
        friendsReqObs.removeAll(friendsReqObs.stream().toList());
        userObs.removeAll(userObs.stream().toList());
        initApp(this.user);
    }

    public void declineFriendRequest(ActionEvent actionEvent) {
        if(friendsRequestList.getSelectionModel().getSelectedItem() == null)
            return;

        String userInfo = friendsRequestList.getSelectionModel().getSelectedItem().toString();
        String email = userInfo.split(" ")[2];
        String status = userInfo.split(" ")[4];

        if(!status.equals("PENDING"))
            return;

        service.declineFriendRequest(user.getEmail(), email);

        friendsObs.removeAll(friendsObs.stream().toList());
        friendsReqObs.removeAll(friendsReqObs.stream().toList());
        userObs.removeAll(userObs.stream().toList());
        initApp(this.user);
    }

    public void sendRequest(ActionEvent actionEvent) {
        if(userList.getSelectionModel().getSelectedItem() == null)
            return;

        String userInfo = userList.getSelectionModel().getSelectedItem().toString();
        String email = userInfo.split(" ")[2];

        if (email.equals(user.getEmail())) // nu mi pot trimite mie cerere de prietenie
            return;

        service.createFriendRequest(user.getEmail(), email);
        friendsReqSentObs.removeAll(friendsReqSentObs.stream().toList());
        friendsObs.removeAll(friendsObs.stream().toList());
        userObs.removeAll(userObs.stream().toList());
        initApp(this.user);
    }

    public void cancelFriendRequest(ActionEvent actionEvent) {
        if(friendRequestsSent.getSelectionModel().getSelectedItem() == null)
            return;

        String userInfo = friendRequestsSent.getSelectionModel().getSelectedItem().toString();
        String email = userInfo.split(" ")[2];

        service.deleteFriendship(user.getEmail(), email);
        friendsReqSentObs.removeAll(friendsReqSentObs.stream().toList());
        userObs.removeAll(userObs.stream().toList());
        initApp(this.user);
    }

    @FXML
    private void adaugaMesaj(ActionEvent actionEvent) {
        try {
            // Get the selected users from the table
            List<User> selectedUsers = usersTable.getSelectionModel().getSelectedItems();

            // Check if users are selected
            if (selectedUsers.isEmpty()) {
                throw new Exception("No users selected!");
            }

            List<User> id_toList = selectedUsers.stream().collect(Collectors.toList());

            String mesaj = this.getMesajField.getText();
            if (mesaj.isEmpty()) throw new Exception("Mesaj null!");

            // Assuming MessageService.addMessage expects a List<UUID> for recipients
            messageService.addMessage(user, id_toList, mesaj);

            getMesajField.clear();
            initApp(this.user);
        } catch (Exception e) {
            e.printStackTrace(); // Replace with proper error handling
        }
    }

    @FXML
    private void replyMesaj(ActionEvent actionEvent){
        try {
            // Get the selected users from the table
            Message selectedMessage = conversatii.getSelectionModel().getSelectedItem();

            // Check if users are selected
            if (selectedMessage==null) {
                throw new Exception("No message selected!");
            }

            List<User> id_toList = Collections.singletonList(selectedMessage.getFrom());

            String reply = this.getMesajField.getText();
            if (reply.isEmpty()) throw new Exception("Mesaj null!");

            // Assuming MessageService.addMessage expects a List<UUID> for recipients
            messageService.addMessageReply(user, id_toList, reply, selectedMessage);

            getMesajField.clear();
            initApp(this.user);
        } catch (Exception e) {
            e.printStackTrace(); // Replace with proper error handling
        }
    }

    @FXML
    private void afisareConversatii(ActionEvent actionEvent) {
        UUID from = user.getId();
        try {
            User to = usersTable.getSelectionModel().getSelectedItem();
            UUID id_to = null;
            if (to != null) {
                id_to = to.getId();
            }

            String str = messageService.conversation(from, id_to).stream()
                    .map(tup -> tup.getFrom().getLastName() + " " + tup.getFrom().getFirstName() + ": " +
                            tup.getMessage() + "\nTrimis la: " +
                            tup.getData().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)) + '\n')
                    .collect(Collectors.joining("\n"));

            List<Message> conversatie = messageService.conversation(from, id_to);
            numeColumn.setCellValueFactory(new PropertyValueFactory<>("nume"));
            mesajColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
            dataColumn.setCellValueFactory(new PropertyValueFactory<>("data"));

            conversatii.setRowFactory(new Callback<TableView<Message>, TableRow<Message>>() {

                public TableRow<Message> call(TableView<Message> tableView) {
                    return new TableRow<Message>() {
                        @Override
                        protected void updateItem(Message item, boolean empty) {
                            super.updateItem(item, empty);

                            // Verificați dacă este un rând valid și conține un mesaj
                            if (!empty && item != null) {
                                // Verificați dacă acest mesaj este un reply
                                if (item.getReply() != null) {
                                    // Setați un stil pentru rândurile care sunt reply-uri
                                    setStyle("-fx-background-color: lightyellow;");
                                } else {
                                    // Setați un alt stil pentru rândurile care nu sunt reply-uri
                                    setStyle("");
                                }
                            } else {
                                // Dacă rândul este gol, asigurați-vă că stilurile sunt șterse
                                setStyle("");
                            }
                        }
                    };
                }
            });

            TableColumn<Message, Long> idCol = new TableColumn<>("ID Mesaj");
            idCol.setCellValueFactory(new PropertyValueFactory<>("idMesaj"));

            conversatii.getItems().setAll(conversatie);
            //openSecondaryWindow(str);

        } catch (Exception e) {
            e.printStackTrace(); // Replace with proper error handling
        }
    }


    public void onPrevious(ActionEvent actionEvent){
        currentPage--;
        initModel();
    }

    public void onNext(ActionEvent actionEvent){
        currentPage++;
        initModel();
    }

    public void increasePageSize(ActionEvent actionEvent){
        pageSize++;
        pageSizeText.setText(String.valueOf(pageSize));
        initModel();
    }

    public void decreasePageSize(ActionEvent actionEvent){
        if(pageSize > 1) {
            pageSize--;
            pageSizeText.setText(String.valueOf(pageSize));
            initModel();
        }
    }
}
