package dev.dynamic.studysphere.tasks;

import dev.dynamic.studysphere.model.Notecard;
import dev.dynamic.studysphere.model.NotecardRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;

@Service
public class RemoveExpiredNotecards {

    @Autowired
    public NotecardRepository repository;

    @PostConstruct
    public void init() {
        new Thread(() -> {
            while (true) {
                try {
                    Set<Notecard> notecardsMarkedForDeletion = repository.findByDeletedTrue();

                    for (Notecard card : notecardsMarkedForDeletion) {
                        if (card.getScheduledDeletionTime().toEpochSecond(ZoneOffset.UTC) <= LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) {
                            repository.delete(card);
                        }
                    }

                    Thread.sleep(30000);
                } catch (Exception e) {
                    System.err.println(STR."Failed to delete expired notecards: \{e.getMessage()}");
                }
            }
        }).start();
    }

}
