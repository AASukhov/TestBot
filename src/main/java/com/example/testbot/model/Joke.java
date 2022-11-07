package com.example.testbot.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "jokes_table")
public class Joke {
    @Id
    private Long jokeId;

    private String text;

    public Long getJokeId() {
        return jokeId;
    }

    public void setJokeId(Long jokeId) {
        this.jokeId = jokeId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String jokeToString() {
        return jokeId + ": " + text;
    }
}
