package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_task")
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "date_time")
    private LocalDateTime dateTime;
    @Column(name = "task_text")
    private String taskText;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getTaskText() {
        return taskText;
    }

    public void setTaskText(String taskText) {
        this.taskText = taskText;
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "taskId=" + id +
                ", chatId=" + chatId +
                ", dateTime=" + dateTime +
                ", taskText='" + taskText + '\'' +
                '}';
    }
}
