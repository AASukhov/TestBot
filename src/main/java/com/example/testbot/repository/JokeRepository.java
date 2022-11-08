package com.example.testbot.repository;

import com.example.testbot.model.Joke;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JokeRepository extends CrudRepository<Joke, Long> {
}
