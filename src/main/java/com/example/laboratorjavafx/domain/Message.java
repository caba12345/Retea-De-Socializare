package com.example.laboratorjavafx.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Message extends Entity<UUID> {
    private User from;
    private List<User> to;
    private String message;
    private LocalDateTime data;
    private Message reply;
    private String nume;
    public Message(User from, List<User> to, String message, LocalDateTime data) {
        this.setId(UUID.randomUUID());
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = data;
        this.reply = null;
        this.nume = from.getFirstName() + " " + from.getLastName() + ":";
    }

    public Message(UUID id, User from, List<User> to, String message, LocalDateTime data) {
        this.setId(id);
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = data;
        this.reply = null;
        this.nume = from.getFirstName() + " " + from.getLastName() + ":";
    }



    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public List<User> getTo() {
        return to;
    }

    public void setTo(List<User> to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }

    public String getNume() {
        return nume;
    }

    public boolean isReceivedFrom(User user) {
        return this.getTo().contains(user) && this.getFrom().equals(user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Message message1 = (Message) o;
        return Objects.equals(from, message1.from) && Objects.equals(to, message1.to) && Objects.equals(message, message1.message) && Objects.equals(data, message1.data) && Objects.equals(reply, message1.reply);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), from, to, message, data, reply);
    }

    @Override
    public String toString() {
        return "from " + from +
                " to " + to +
                " message '" + message + '\'' + data +
                " reply " + reply +
                " id " + id ;
    }
}

