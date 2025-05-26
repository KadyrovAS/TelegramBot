package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteWebhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.service.MessagesParser;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final NotificationTaskService notificationTaskService;
    private final TelegramBot telegramBot;
    private final MessagesParser messagesParser;

    public TelegramBotUpdatesListener(NotificationTaskService notificationTaskService,
                                      TelegramBot telegramBot,
                                      MessagesParser messagesParser) {
        this.notificationTaskService = notificationTaskService;
        this.telegramBot = telegramBot;
        this.messagesParser = messagesParser;
    }

    @PostConstruct
    public void init() {
        telegramBot.execute(new DeleteWebhook());
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        for (Update update: updates){
            try {
                messagesParser.put(update);
            }catch(Exception e){
                LOGGER.error(e.getMessage());
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
