package com.example.testbot.service;

import com.example.testbot.config.BotConfig;
import com.example.testbot.model.User;
import com.example.testbot.model.UserRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TestBotService extends TelegramLongPollingBot {

    @Autowired
    private UserRepository repository;

    final BotConfig config;

    public TestBotService(BotConfig config) {
        this.config = config;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "get a welcome message"));
        listofCommands.add(new BotCommand("/mydata", "get your data stored"));
        listofCommands.add(new BotCommand("/deletedata", "delete my data"));
        listofCommands.add(new BotCommand("/help", "info how to use this bot"));
        listofCommands.add(new BotCommand("/settings", "set your preferences"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
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
                        registerUser(update.getMessage());
                        break;
                case "/help":
                        sendMessage(chatId, "This telegram bot is for demonstration \n " +
                                "Use menu and keyboard for navigation");
                        break;
                case "/mydata":
                        sendMessage(chatId, "Your data was saved when you have used '/start' command");
                        checkUserInDatabase(update.getMessage());
                        sendMessage(chatId, "If you want your data were deleted use '/deletedata' command");
                        break;
                case "/deletedata":
                        deleteUserFromDatabase(update.getMessage());
                        break;
                default: sendMessage(chatId, "Sorry, command was no recognized");
            }
        }
    }

    private void startCommandReceived(long chatId, String name){
        String answerParsed = EmojiParser.parseToUnicode("Hello, " + name + ", nice to meet you!" + " :blush:");
        log.info("Replied to user " + name);
        sendMessage(chatId, answerParsed);
    }

    private void registerUser(Message message) {
        if(repository.findById(message.getChatId()).isEmpty()){
            var chatId = message.getChatId();
            var chat = message.getChat();

            User user = new User();
            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            repository.save(user);
            log.info("user saved: " + user);
        }
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage msg = new SendMessage();
        msg.setChatId(String.valueOf(chatId));
        msg.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Weather");
        row.add("Get random joke");
        keyboardRows.add(row);

        row = new KeyboardRow();

        row.add("register");
        row.add("check my data");
        row.add("delete my data");

        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);

        msg.setReplyMarkup(keyboardMarkup);

        try{
            execute(msg);
        } catch (TelegramApiException e) {
            log.error("Error occured " + e.getMessage());
        }
    }

    private void checkUserInDatabase (Message message) {
        var chatId = message.getChatId();

        if (repository.findById(chatId).isEmpty()) {
            sendMessage(chatId, "Your data is absent");
        } else {
            sendMessage(chatId, "Your data is saved \n" +
                    repository.findById(chatId).get().toString());
        }
    }

    private void deleteUserFromDatabase (Message message) {
        var chatId = message.getChatId();
        if (repository.findById(chatId).isEmpty()) {
            sendMessage(chatId, "Your data is absent, nothing to delete");
        } else {
            repository.delete(repository.findById(chatId).get());
            sendMessage(chatId, "Your data is deleted");
        }
    }
}
