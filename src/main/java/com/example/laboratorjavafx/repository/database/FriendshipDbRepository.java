package com.example.laboratorjavafx.repository.database;

import com.example.laboratorjavafx.domain.FriendShip;
import com.example.laboratorjavafx.domain.User;
import com.example.laboratorjavafx.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class FriendshipDbRepository implements Repository<UUID, FriendShip> {
    private String url;
    private String user;
    private String password;

    public FriendshipDbRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public Optional<FriendShip> findOne(UUID id) {
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("SELECT * FROM Friendships WHERE UUID=?");)
        {
            //statement.setLong(1,id);
            statement.setObject(1,id);
            ResultSet r = statement.executeQuery();
            if (r.next()){
                String FirstNameU1 = r.getString("FirstNameU1");
                String LastNameU1 = r.getString("LastNameU1");
                String emailU1 = r.getString("emailU1");
                String FirstNameU2 = r.getString("FirstNameU2");
                String LastNameU2 = r.getString("LastNameU2");
                String emailU2 = r.getString("emailU2");
                UUID idu1 = (UUID) r.getObject("idu1");
                UUID idu2 = (UUID) r.getObject("idu2");
                Timestamp friendsFromTimestamp = r.getTimestamp("friendsFrom");
                User u1 = new User(idu1, FirstNameU1, LastNameU1, emailU1);
                User u2 = new User(idu2, FirstNameU2, LastNameU2, emailU2);
                LocalDateTime friendsFrom = friendsFromTimestamp.toLocalDateTime();
                FriendShip p1 = new FriendShip(id, u1,u2, friendsFrom);
                return Optional.of(p1);
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<FriendShip> findAll() {
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("SELECT * FROM Friendships");)
        {
            ArrayList<FriendShip> list = new ArrayList<>();
            ResultSet r = statement.executeQuery();
            while (r.next()){
                UUID id = (UUID)r.getObject("UUID");
                String FirstNameU1 = r.getString("FirstNameU1");
                String LastNameU1 = r.getString("LastNameU1");
                String emailU1 = r.getString("emailU1");
                String FirstNameU2 = r.getString("FirstNameU2");
                String LastNameU2 = r.getString("LastNameU2");
                String emailU2 = r.getString("emailU2");
                UUID idu1 = (UUID) r.getObject("idu1");
                UUID idu2 = (UUID) r.getObject("idu2");
                Timestamp friendsFromTimestamp = r.getTimestamp("friendsFrom");
                User u1 = new User(idu1, FirstNameU1, LastNameU1, emailU1);
                User u2 = new User(idu2, FirstNameU2, LastNameU2, emailU2);
                LocalDateTime friendsFrom = friendsFromTimestamp.toLocalDateTime();
                FriendShip p1 = new FriendShip(id, u1,u2, friendsFrom);
                list.add(p1);
            }
            return list;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendShip> save(FriendShip entity) {
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("INSERT INTO Friendships(FirstNameU1,LastNameU1,emailu1,FirstNameU2,LastNameU2,emailu2,UUID,friendsFrom,idu1,idu2) VALUES (?,?,?,?,?,?,?,?,?,?)");)
        {
            statement.setString(1,entity.getUser1().getFirstName());
            statement.setString(2,entity.getUser1().getLastName());
            statement.setString(3,entity.getUser1().getEmail());
            statement.setString(4,entity.getUser2().getFirstName());
            statement.setString(5,entity.getUser2().getLastName());
            statement.setString(6,entity.getUser2().getEmail());
            statement.setObject(7,UUID.randomUUID());
            statement.setTimestamp(8,Timestamp.valueOf(entity.getFriendsFrom()));
            statement.setObject(9,entity.getUser1().getId());
            statement.setObject(10,entity.getUser2().getId());
            int affectedRows = statement.executeUpdate();
            return affectedRows!=0? Optional.empty():Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }    }

    @Override
    public Optional<FriendShip> delete(UUID uuid) {
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("DELETE FROM Friendships WHERE UUID = ?");)
        {
            var cv = findOne(uuid);
            statement.setObject(1,uuid);
            int affectedRows = statement.executeUpdate();
            return affectedRows==0? Optional.empty():cv;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendShip> update(FriendShip entity) {
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("UPDATE Friendships SET FirstNameU1 = ?, LastNameU1 = ?,FirstNameU2 = ?,LastNameU2 =? WHERE UUID = ?");)
        {
            statement.setString(1,entity.getUser1().getFirstName());
            statement.setString(2,entity.getUser1().getLastName());
            statement.setString(3,entity.getUser2().getFirstName());
            statement.setString(4,entity.getUser2().getLastName());
            statement.setObject(5,entity.getId());
            int affectedRows = statement.executeUpdate();
            return affectedRows!=0? Optional.empty():Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int size(){
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("SELECT COUNT(*) FROM Friendships");)
        {
            ResultSet r = statement.executeQuery();
            if (r.next()){
                return r.getInt(1);
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return 0;
    }
}