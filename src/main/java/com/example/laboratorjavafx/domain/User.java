package com.example.laboratorjavafx.domain;

import java.util.*;

public class User extends Entity<UUID>{
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Map<UUID, User> friends;

    public User(UUID id, String firstName, String lastName, String email){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.friends = new HashMap<>();
        this.setId(id);
    }
    public User(UUID id, String firstName, String lastName, String email, String password){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.friends = new HashMap<>();
        this.password = password;
        this.setId(id);
    }
    public User(String firstName, String lastName, String email, String password){

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.friends = new HashMap<>();
        this.password = password;

        //generates a random unique ID
        this.setId(UUID.randomUUID());
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword(){
        return password;
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o){
        if (this==o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getId().equals(user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getEmail());
    }

    //adds a new friend
    public void addFriend(User u){
        this.friends.put(u.getId(), u);
    }

    //removes a friend
    public boolean removeFriend(User u){
        return this.friends.remove(u.getId()) != null;
    }


    //returns number of friends
    public int getNumberOfFriends(){
        return this.friends.size();
    }

    //returns the list of friends
    public List<User> getFriends(){
        return new ArrayList<>(this.friends.values());
    }
}
