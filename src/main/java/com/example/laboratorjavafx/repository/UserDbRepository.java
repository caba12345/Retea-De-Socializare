package com.example.laboratorjavafx.repository;

import com.example.laboratorjavafx.domain.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class UserDbRepository implements Repository<UUID ,User> {
    private String url;
    private String user;
    private String password;


    public UserDbRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public Optional<User> findOne(UUID id) {
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("SELECT * FROM Users WHERE UUID=?::uuid");)
        {
            //statement.setLong(1,id);
            statement.setObject(1,id.toString());
            ResultSet r = statement.executeQuery();
            if (r.next()){
                String FirstName = r.getString("FirstName");
                String LastName = r.getString("LastName");
                String email = r.getString("email");
                User u1 = new User(id, FirstName, LastName, email);
                return Optional.of(u1);
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<User> findAll() {
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("SELECT * FROM Users");)
        {
            ArrayList<User> list = new ArrayList<>();
            ResultSet r = statement.executeQuery();
            while (r.next()){
                UUID id = (UUID) r.getObject("UUID");
                String FirstName = r.getString("FirstName");
                String LastName = r.getString("LastName");
                String email = r.getString("email");
                User u1 = new User(id, FirstName, LastName, email);
                list.add(u1);
            }
            return list;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> save(User entity) {
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("INSERT INTO Users(UUID,FirstName,LastName,Email) VALUES (?,?,?,?)");)
        {
            statement.setObject(1,entity.getId());
            statement.setString(2,entity.getFirstName());
            statement.setString(3,entity.getLastName());
            statement.setString(4,entity.getEmail());

            //statement.setInt(3,entity.getYear());
            int affectedRows = statement.executeUpdate();
            return affectedRows!=0? Optional.empty():Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }    }

    @Override
    public Optional<User> delete(UUID uuid) {
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("DELETE FROM Users WHERE UUID = ?::uuid");)
        {
            var cv = findOne(uuid);
            //statement.setLong(1,uuid);
            statement.setObject(1,uuid.toString());
            int affectedRows = statement.executeUpdate();
            return affectedRows==0? Optional.empty():cv;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> update(User entity) {
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("UPDATE Users SET FirstName = ?, LastName = ? WHERE UUID = ?");)
        {
            statement.setString(1,entity.getFirstName());
            statement.setString(2,entity.getLastName());
            statement.setObject(3,entity.getId());

            int affectedRows = statement.executeUpdate();
            return affectedRows!=0? Optional.empty():Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int size(){
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("SELECT COUNT(*) FROM Users");)
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