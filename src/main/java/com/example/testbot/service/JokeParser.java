package com.example.testbot.service;

import com.example.testbot.model.Joke;
import com.example.testbot.repository.JokeRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@Slf4j
@Service
@PropertySource("application.properties")
public class JokeParser {

    @Autowired
    JokeRepository repository;

    @Value("${joke.service.url}")
    String serviceUrl;

    @Value("${spring.datasource.url}")
    String dataBase;

    @Value("${spring.datasource.username}")
    String dbUserName;

    @Value("${spring.datasource.password}")
    String dbPassword;

    public void jokeParsing(JokeRepository repository) {
        this.repository = repository;

        try {
            Document document = Jsoup.connect(serviceUrl).get();
            Elements elements = document.select(".text");
            for (Element e : elements) {
                Long id = Long.parseLong(e.id());
                String[] arr = e.text().split("<?\\S\\w+>");
                String text = "";
                for (int i = 0; i < arr.length; i++) {
                    text = text + arr[i];
                    if(repository.findById(id).isEmpty()) {
                        Joke poke = new Joke();
                        poke.setJokeId(id);
                        poke.setText(text);
                        repository.save(poke);
                    }
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

    public String jokeGiving (){
        String url = dataBase;
        String username = dbUserName;
        String password = dbPassword;
        String text ="";
        try (Connection connection = DriverManager.getConnection(url,username,password)){
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM jokes_table ORDER BY RAND() LIMIT 1");
            while(resultSet.next()){
                text = resultSet.getString(2);
            }
        } catch (Exception e) {
            log.info("Problem in JokeGiving");
            e.printStackTrace();
        }
        return text;
    }
}