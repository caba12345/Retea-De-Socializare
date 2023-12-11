package com.example.laboratorjavafx.domain;

import com.example.laboratorjavafx.domain.FriendshipStatus;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class FriendRequest extends Entity<UUID> {
    private User fromUser;
    private User toUser;
    private FriendshipStatus status;
    private LocalDateTime requestFrom;

    public FriendRequest(User fromUser, User toUser, FriendshipStatus status) {
        this.setId(UUID.randomUUID());
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.status = status;
        requestFrom = LocalDateTime.now();
    }

    // Getters and Setters
    public void setId(UUID uuid) {
        this.id = uuid;
    }

    public UUID getId(){
        return id;
    }

    public User getFromUser() {
        return fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public LocalDateTime getRequestFrom() {
        return requestFrom;
    }

    public void setStatus(FriendshipStatus status){
        this.status = status;
    }

    @Override
    public String toString() {
        return "FriendRequest {" +
                "fromUser=" + fromUser +
                ", toUser=" + toUser +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    // Implementare pentru equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FriendRequest)) return false;
        FriendRequest that = (FriendRequest) o;
        return id.equals(that.getId());
    }

    // Implementare pentru hashCode
    @Override
    public int hashCode() {
        return Objects.hash(fromUser, toUser);
    }
}