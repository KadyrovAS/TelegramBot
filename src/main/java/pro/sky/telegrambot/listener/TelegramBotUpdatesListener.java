package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteWebhook;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final NotificationTaskService notificationTaskService;
    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    public TelegramBotUpdatesListener(NotificationTaskService notificationTaskService) {
        this.notificationTaskService = notificationTaskService;
    }

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.execute(new DeleteWebhook());
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        NotificationTask notificationTask = new NotificationTask();
        try {
            updates.forEach(update -> {
                notificationTask.setChat_id(update.message().chat().id());
                notificationTask.setDate_time(parseDateTime(update.message().chat().id(), update.message().text()));
                notificationTask.setTask_text(parseText(update.message().chat().id(), update.message().text()));
                logger.info("Processing update: {}", update);
                notificationTaskService.add(notificationTask);
                telegramBot.execute(new SendMessage(update.message().chat().id(),
                        "Your message has been received"));
            });
        }catch (Exception e) {}
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Scheduled(cron = "0 * * * * *")
    public void run() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        logger.info("Running schedule: {}", now);
        Collection<NotificationTask> notificationTaskList = notificationTaskService.findAllByDateTime(now);
        notificationTaskList.forEach(notificationTask -> {
            SendResponse response = telegramBot.execute(new SendMessage(notificationTask.getChat_id(), notificationTask.getTask_text()));
            logger.info("Send response: {}", response);
        });
    }


    private LocalDateTime parseDateTime(Long id, String text) {
        String[] dt = text.split(" ");
        if (dt.length < 3) {
            telegramBot.execute(new SendMessage(id, "Illegal argument"));
            logger.error("Illegal argument exception");
            throw new IllegalArgumentException("Illegal argument exception");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        text = dt[0] + " " + dt[1];
        LocalDateTime dateTime;
        try {
             dateTime = LocalDateTime.parse(text, formatter);
        } catch (DateTimeParseException e) {
            telegramBot.execute(new SendMessage(id, "Illegal DateTime format"));
            logger.error("Illegal DateTime format exception: {}", e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
        if (dateTime.isBefore(LocalDateTime.now())){
            logger.error("The appointed time is already in the past {}", dateTime);
            telegramBot.execute(new SendMessage(id, "The appointed time is already in the past"));
            throw new IllegalArgumentException("The appointed time is already in the past");
        }
        return dateTime;
    }

    private String parseText(Long id, String text) {
        String[] dt = text.split(" ");
        if (dt.length < 3) {
            SendMessage message = new SendMessage(id, "Illegal argument");
            telegramBot.execute(message);
            logger.error("Illegal argument exception");
            throw new IllegalArgumentException("Illegal argument");
        }
        StringBuilder result = new StringBuilder();
        for (int i = 2; i < dt.length; i++) {
            result.append(dt[i]).append(" ");
        }
        return result.toString();
    }
}
