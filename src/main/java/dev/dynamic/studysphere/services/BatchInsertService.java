package dev.dynamic.studysphere.services;

import dev.dynamic.studysphere.model.Notecard;
import dev.dynamic.studysphere.model.NotecardCategory;
import dev.dynamic.studysphere.model.NotecardRepository;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BatchInsertService {

    @Autowired
    private NotecardRepository notecardRepository;

    private final Map<UUID, String> notecards = new HashMap<>();

    private Logger logger = LogManager.getLogger(BatchInsertService.class);

    public void addToBatch(UUID id, String content) {
        logger.info("Adding notecard to batch with id: " + id);
        if (!notecards.containsKey(id)) {
            notecards.put(id, content);
        } else {
            notecards.replace(id, content);
        }
    }

    @PostConstruct
    public void insertBatch() {
        new Thread(() -> {
            while (true) {
                logger.info("Inserting batch of notecards");
                List<Notecard> notecardList = new ArrayList<>();
                for (Map.Entry<UUID, String> entry : notecards.entrySet()) {
                    Notecard notecard = notecardRepository.findById(entry.getKey()).orElse(null);
                    if (notecard != null) {
                        notecard.setContent(entry.getValue());
                        notecardList.add(notecard);
                    }
                }
                notecardRepository.saveAll(notecardList);
                notecards.clear();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

}
