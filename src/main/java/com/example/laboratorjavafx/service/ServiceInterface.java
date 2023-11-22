package com.example.laboratorjavafx.service;

import com.example.laboratorjavafx.domain.Entity;
import com.example.laboratorjavafx.domain.FriendShip;
import com.example.laboratorjavafx.domain.User;
import com.example.laboratorjavafx.domain.validators.ValidationException;

import java.util.List;

public interface ServiceInterface<ID> {

    /**
     *
     * @param user - the entity that has to be added
     * @return true if the entity was added
     *         false otherwise
     * @throws IllegalArgumentException
     *                  if entity is null
     * @throws ValidationException
     *                  if the entity validation did not work.
     */
    boolean addUser(User user);


    /**
     *
     * @param email - the id of the entity that we have to remove
     * @return the entity that was removed if there was any
     *         null otherwise
     * @throws IllegalArgumentException
     *                  if id is null
     */
    Entity<ID> deleteUser(String email);


    /**
     *
     * @param email1 and
     * @param email2 - the emails of the user we have to create a friendship between
     *
     * @return true if the entity was added
     *         false otherwise
     * @throws IllegalArgumentException
     *                  if any of the emails are null
     * @throws ValidationException
     *                  if the emails are the same (you cannot add yourself as a friend)
     */
    boolean createFriendship(String email1, String email2);


    /**
     * The function deletes a friendship between two users
     *  @param email1 and
     *  @param email2 - the emails of the user we have to create a friendship between
     *
     *  @return the friendship if it exists
     *          null otherwise
     *  @throws IllegalArgumentException
     *                if any of the emails are null
     */
    Entity<ID> deleteFriendship(String email1, String email2);


    /**
     * @return an Iterable of all the users
     */
    Iterable<User> getAllUsers();


    /**
     * @return an Iterable of all the friendships
     */
    Iterable<FriendShip> getAllFriendships();


    /**
     * Adds predefined users and friendships
     */
    void add_Predefined_Values();

    /**
     * @return an int that represents the number of communities
     */
    int numberOfCommunities();


    /**
     * Returns the most sociable community
     * the most sociable community is the community of users with the longest path
     *
     * @return an Iterable of all the most sociable communities users
     */
    Iterable<Iterable<User>> mostSociableCommunity();

    /**
     * @returns a list of all the communities
     */
    List<List<User>> getAllCommunities();

    /**
     * @param email a string that represents the user's email we have to find
     *
     * @return the user that has that specific email
     *         or null if there is no user with that email
     */
    User getUserByEmail(String email);

    /**
     * @param email1
     * @param email2
     */
    void acceptFriendship(String email1, String email2);

    /**
     * @param email1
     * @param email2
     */
    void declineFriendRequest(String email1, String email2);

    /**
     * @param email1
     * @param email2
     */
    void createFriendRequest(String email1, String email2);

}