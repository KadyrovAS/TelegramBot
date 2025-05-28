package pro.sky.telegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.util.Collection;

public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {
    Collection<NotificationTask> findByDateTime(LocalDateTime dateTime);

}
