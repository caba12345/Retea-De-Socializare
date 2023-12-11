package com.example.laboratorjavafx.repository.database;

import com.example.laboratorjavafx.domain.*;
import com.example.laboratorjavafx.repository.Repository;

import java.sql.*;
import java.util.*;

public class FriendRequestDbRepository implements Repository<UUID, FriendRequest> {
    private String url;
    private String user;
    private String password;
    private final Repository userRepo;

    public FriendRequestDbRepository(String url, String user, String password, Repository userRepo) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.userRepo = userRepo;
    }

    @Override
    public Optional<FriendRequest> findOne(UUID id) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Friendrequests WHERE id = ?");
        ) {
            statement.setString(1, id.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String fromEmail = resultSet.getString("fromemail");
                    String toEmail = resultSet.getString("toemail");
                    String status = resultSet.getString("status");

                    User fromUser = getUserByEmail(fromEmail);
                    User toUser = getUserByEmail(toEmail);
                    FriendshipStatus requestStatus = FriendshipStatus.valueOf(status);

                    FriendRequest request = new FriendRequest(fromUser, toUser, requestStatus);
                    request.setId(id);

                    return Optional.of(request);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<FriendRequest> findAll() {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Friendrequests");)
        {
            ArrayList<FriendRequest> allRequests = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                UUID id = UUID.fromString(resultSet.getString("id"));
                String fromEmail = resultSet.getString("fromemail");
                String toEmail = resultSet.getString("toemail");
                String status = resultSet.getString("status");

                User fromUser = getUserByEmail(fromEmail);
                User toUser = getUserByEmail(toEmail);
                FriendshipStatus requestStatus = FriendshipStatus.valueOf(status);

                FriendRequest request = new FriendRequest(fromUser, toUser, requestStatus);
                request.setId(id);

                allRequests.add(request);
            }
            return allRequests;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private User getUserByEmail(String email) {
        Iterable<User> users = userRepo.findAll();

        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }

        return null;
    }

    @Override
    public Optional<FriendRequest> save(FriendRequest entity) {

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO Friendrequests(fromemail, toemail, status, id) VALUES (?, ?, ?, ?)")) {

            statement.setString(1, entity.getFromUser().getEmail());
            statement.setString(2, entity.getToUser().getEmail());
            statement.setString(3, entity.getStatus().toString());
            statement.setObject(4, entity.getId());


            int affectedRows = statement.executeUpdate();
            return affectedRows!=0? Optional.empty():Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<FriendRequest> delete(UUID id) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM Friendrequests WHERE id = ?"))
        {
            var cv = findOne(id);
            statement.setObject(1, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows==0? Optional.empty():cv;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<FriendRequest> update(FriendRequest entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE Friendrequests SET fromemail=?, toemail=?, status=? WHERE id=?")) {

            statement.setString(1, entity.getFromUser().getEmail());
            statement.setString(2, entity.getToUser().getEmail());
            statement.setString(3, entity.getStatus().toString());
            statement.setObject(4, entity.getId());

            int affectedRows = statement.executeUpdate();
            return affectedRows!=0? Optional.empty():Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int size() {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM Friendrequests");
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}
