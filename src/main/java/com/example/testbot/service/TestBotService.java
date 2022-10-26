package com.example.testbot.service;

import com.example.testbot.config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TestBotService extends TelegramLongPollingBot {

    final BotConfig config;

    public TestBotService(BotConfig config) {
        this.config = config;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()){
            String messageTest = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageTest) {
                case "/start":
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;
                default: sendMessage(chatId, "Sorry, command was no recognized");
            }
        }
    }

    private void startCommandReceived(long chatId, String name){
        String answer = "Hello, " + name + ", nice to meet you!";
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage msg = new SendMessage();
        msg.setChatId(String.valueOf(chatId));
        msg.setText(textToSend);

        try{
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
