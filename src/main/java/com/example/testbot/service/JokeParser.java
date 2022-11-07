package com.example.testbot.service;

import com.example.testbot.model.Joke;
import com.example.testbot.repository.JokeRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class JokeParser {

    @Autowired
    private JokeRepository jokeRepository;

    public JokeParser() {
        try {
            Document document = Jsoup.connect("https://nekdo.ru/random/").get();
            Elements elements = document.select(".text");
            for (Element e : elements) {
                Long id = Long.parseLong(e.id());
                String [] arr = e.text().split("<?\\S\\w+>");
                String text = "";
                for (int i = 0; i < arr.length; i++) {
                    text = text + arr[i];
                }
                if (jokeRepository.findById(id).isEmpty()) {
                    Joke poke = new Joke();
                    poke.setJokeId(id);
                    poke.setText(text);
                    jokeRepository.save(poke);
                }
            }
        } catch (NullPointerException e) {
            log.info("Null pointer exception while working of JokeParser");
            e.printStackTrace();
        } catch (IOException r) {
            log.info("IOException while working of JokeParser");
            r.printStackTrace();
        }
    }
}
