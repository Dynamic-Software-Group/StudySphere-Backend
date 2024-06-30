package dev.dynamic.studysphere.services;

import dev.dynamic.studysphere.model.Notecard;
import dev.dynamic.studysphere.model.NotecardCategory;
import dev.dynamic.studysphere.model.NotecardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BatchInsertService {

    @Autowired
    private NotecardRepository notecardRepository;

    private final Map<UUID, NotecardCategory> notecards = new HashMap<>();

    public boolean existsInBatch(UUID id) {
        return notecards.containsKey(id);
    }

    public void addToBatch(Notecard notecard) {
        if (!existsInBatch(notecard.getId())) {
            notecards.put(notecard.getId(), notecard.getCategory());
        } else {
            notecards.replace(notecard.getId(), notecard.getCategory());
        }
    }

    @Scheduled(fixedRate = 3000)
    public void insertBatch() {
        List<Notecard> notecardList = new ArrayList<>();
        for (Map.Entry<UUID, NotecardCategory> entry : notecards.entrySet()) {
            Notecard notecard = new Notecard();
            notecard.setId(entry.getKey());
            notecard.setCategory(entry.getValue());
            notecardList.add(notecard);
        }
        notecardRepository.saveAll(notecardList);
        notecards.clear();
    }

}
