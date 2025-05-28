package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class MessagesParser{
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagesParser.class);
    private static final String REGEX =
            "^(\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2})\\s(.+)$";
    private final TelegramBot telegramBot;
    private final NotificationTaskService notificationTaskService;
    private final Pattern pattern;
    public MessagesParser(TelegramBot telegramBot, NotificationTaskService notificationTaskService) {
        this.telegramBot = telegramBot;
        this.notificationTaskService = notificationTaskService;
        this.pattern = Pattern.compile(REGEX);
    }

    private void start(Long id){
        notificationTaskService.deleteAll();
        String message = "The reminder service is ready";
        telegramBot.execute(new SendMessage(id, message));
    }

    public void put(Update update) {
        Long id = update.message().chat().id();
        String text = update.message().text().trim();
        LOGGER.info(text);

        if (text.equals("/start")){
            start(id);
            return;
        }

        Matcher matcher = pattern.matcher(text);

        if (!matcher.matches()){
            String message = "The message does not match the set format";
            telegramBot.execute(new SendMessage(id, message));
            throw new NumberFormatException(message);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String dateTimeString = matcher.group(1);
        text = matcher.group(2);

        LocalDateTime localDateTime;
        try {
            localDateTime = LocalDateTime.parse(dateTimeString, formatter);
        }catch (DateTimeParseException e){
            String message = "DateTimeParseException " + dateTimeString;
            telegramBot.execute(new SendMessage(id, message));
            throw new DateTimeParseException(e.getMessage(), dateTimeString, e.getErrorIndex());
        }

        if (localDateTime.isBefore(LocalDateTime.now())){
            String message = "The appointed time is already in the past " + dateTimeString;
            telegramBot.execute(new SendMessage(id, message));
            throw new IllegalArgumentException(message);
        }

        NotificationTask notificationTask = new NotificationTask();
        notificationTask.setChatId(id);
        notificationTask.setDateTime(localDateTime);
        notificationTask.setTaskText(text);

        notificationTaskService.add(notificationTask);
        String message = "Your message \"" + text + "\" has been received";
        LOGGER.info(message);
        telegramBot.execute(new SendMessage(id, message));
    }
}
