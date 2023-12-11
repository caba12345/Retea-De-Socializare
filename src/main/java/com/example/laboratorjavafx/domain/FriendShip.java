package com.example.laboratorjavafx.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class FriendShip extends Entity<UUID>{

    private User user1;
    private User user2;
    private LocalDateTime friendsFrom;
    private FriendshipStatus request;

    public FriendShip(UUID id, User user1, User user2, LocalDateTime friendsFrom, FriendshipStatus request){
        this.id = id;
        this.user1 = user1;
        this.user2 = user2;
        this.friendsFrom = friendsFrom;
        this.request = request;
    }

    public FriendShip(User user1, User user2, FriendshipStatus request){
        this.user1 = user1;
        this.user2 = user2;
        this.setId(UUID.randomUUID());
        friendsFrom = LocalDateTime.now();
        this.request = request;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    public void setFriendsFrom(LocalDateTime friendsFrom) {
        this.friendsFrom = friendsFrom;
    }

    public FriendshipStatus getRequest() {
        return request;
    }

    public void setRequest(FriendshipStatus request) {
        this.request = request;
    }


    @Override
    public String toString() {
        return "FriendShip {" +
                "user1=" + user1 +
                ", user2=" + user2 +
                ", id=" + id +
                ", request=" + request +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FriendShip))    return false;
        FriendShip that = (FriendShip) o;
        return id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser1(), getUser2());
    }
}


