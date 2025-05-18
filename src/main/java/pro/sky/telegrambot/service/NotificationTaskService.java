package pro.sky.telegrambot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repositories.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
public class NotificationTaskService {
    private final NotificationTaskRepository notificationTaskRepository;
    private final static Logger LOGGER = LoggerFactory.getLogger(NotificationTaskService.class);

    public NotificationTaskService(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    public void add(NotificationTask notificationTask) {
        LOGGER.debug("Adding notification task {}", notificationTask);
        notificationTaskRepository.save(notificationTask);
    }

    public Collection<NotificationTask> findAllByDateTime(LocalDateTime dateTime) {
        LOGGER.info("Finding all notification tasks by date {}", dateTime);
        return notificationTaskRepository.findAllByDateTime(dateTime);
    }
}
