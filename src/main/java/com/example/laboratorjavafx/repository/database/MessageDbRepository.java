package com.example.laboratorjavafx.repository.database;

import com.example.laboratorjavafx.domain.Message;
import com.example.laboratorjavafx.repository.Repository;
import com.example.laboratorjavafx.domain.User;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class MessageDbRepository implements Repository<UUID, Message> {

    private UserDbRepository userRepo;
    private String url;
    private String user;
    private String password;

    public MessageDbRepository(String url, String user, String password,UserDbRepository userRepo) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.userRepo = userRepo;
    }

    @Override
    public Optional<Message> findOne(UUID id) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages WHERE id=?");) {
            //statement.setLong(1,id);
            statement.setObject(1, id);
            ResultSet r = statement.executeQuery();
            if (r.next()) {
                UUID id_from = (UUID) r.getObject("from_id");
                String list_to = r.getString("to_id");
                List<String> listToUUIDs = Arrays.asList(list_to.split(","));
                List<User> listUser = listToUUIDs.stream()
                        .map(uuidString -> {
                            try {
                                UUID uuid = UUID.fromString(uuidString.trim());
                                return userRepo.findOne(uuid).orElse(null);
                            } catch (IllegalArgumentException e) {
                                // Handle invalid UUID string
                                return null;
                            }
                        })
                        .filter(user -> user != null)
                        .collect(Collectors.toList());
                UUID id_reply = (UUID) r.getObject("id_reply");
                String mesaj = r.getString("mesaj");
                LocalDateTime date = r.getTimestamp("datamesaj").toLocalDateTime();
                Message m = new Message(id,userRepo.findOne(id_from).get(), listUser, mesaj, date);
                if(id_reply != null) {
                    m.setReply(findOne(id_reply).get());
                }
                m.setId(id);
                return Optional.ofNullable(m);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Message> findAll() {
        ArrayList<Message> mesaje = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(
                     "select * from messages;");) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                UUID id = (UUID) resultSet.getObject("id");
                UUID id_from = (UUID) resultSet.getObject("from_id");
                String list_to = resultSet.getString("to_id");
                List<String> listToUUIDs = Arrays.asList(list_to.split(","));
                List<User> listUser = listToUUIDs.stream()
                        .map(uuidString -> {
                            try {
                                UUID uuid = UUID.fromString(uuidString.trim());
                                return userRepo.findOne(uuid).orElse(null);
                            } catch (IllegalArgumentException e) {
                                // Handle invalid UUID string
                                return null;
                            }
                        })
                        .filter(user -> user != null)
                        .collect(Collectors.toList());
                UUID id_reply = (UUID) resultSet.getObject("id_reply");
                String mesaj = resultSet.getString("mesaj");
                LocalDateTime date = resultSet.getTimestamp("datamesaj").toLocalDateTime();
                Message m = new Message(id,userRepo.findOne(id_from).get(), listUser, mesaj, date);
                if(id_reply != null)  {
                    m.setReply(findOne(id_reply).get());
                }
                m.setId(id);
                mesaje.add(m);
            }
            return mesaje;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> save(Message entity) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement =
                     connection.prepareStatement("insert into messages(id,from_id,to_id,mesaj,datamesaj,id_reply)values (?,?,?,?,?,?);");) {
            statement.setObject(1,entity.getId());
            statement.setObject(2, entity.getFrom().getId());

            String toUUIDs = entity.getTo().stream()
                    .map(user -> user.getId().toString())
                    .collect(Collectors.joining(","));

            statement.setString(3, toUUIDs);
            statement.setString(4, entity.getMessage());

            LocalDateTime dt = entity.getData();
            statement.setTimestamp(5, Timestamp.valueOf(dt));

            // Assuming id_reply is set to null for new messages
            if (entity.getReply() == null) {
                statement.setNull(6, Types.OTHER);
            } else
            statement.setObject(6, entity.getReply().getId());

            statement.executeUpdate();

            // Retrieve the generated keys to get the new message ID
            //ResultSet generatedKeys = statement.getGeneratedKeys();
            // if (generatedKeys.next()) {
            //    UUID generatedId = (UUID) generatedKeys.getObject(1);
            //   entity.setId(generatedId);
            //}

            return Optional.of(entity);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> delete(UUID id) {
        return Optional.empty();
    }
    @Override
    public Optional<Message> update(Message entity) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(
                     "update messages set id_reply = ? where id = ?");) {
            statement.setObject(1, entity.getReply().getId());
            statement.setObject(2, entity.getId());
            statement.executeUpdate();

            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public int size() {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM messages");
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0; // Returnăm 0 în cazul în care nu s-au găsit înregistrări
    }
}