package dev.dynamic.studysphere.endpoints;

import dev.dynamic.studysphere.model.*;
import dev.dynamic.studysphere.model.request.CreateNotecardRequest;
import dev.dynamic.studysphere.model.response.CreateNotecardResponse;
import dev.dynamic.studysphere.model.response.GetNotecardsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notecard")
public class NotecardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotecardCategoryRepository categoryRepository;

    @Autowired
    private NotecardRepository notecardRepository;

    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity createNotecard(@RequestBody CreateNotecardRequest request) {
        String email = request.getEmail();
        if (userRepository.findByEmail(email).isEmpty()) {
            return ResponseEntity.status(401).body("User not found");
        }
        User user = userRepository.findByEmail(email).get();

        String name = request.getName();
        String category = request.getCategory();

        if (categoryRepository.findByNameAndOwner(category, user) == null) {
            return ResponseEntity.status(401).body("Category not found");
        }

        NotecardCategory notecardCategory = categoryRepository.findByNameAndOwner(category, user);

        Notecard notecard = new Notecard();
        notecard.setName(name);
        notecard.setCategory(notecardCategory);
        notecard.setContent("");
        notecard.setOwner(user);

        notecardRepository.save(notecard);
        CreateNotecardResponse response = new CreateNotecardResponse(notecard.getId(), notecard.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity getNotecards(@RequestParam String email) {
        if (userRepository.findByEmail(email).isEmpty()) {
            return ResponseEntity.status(401).body("User not found");
        }
        User user = userRepository.findByEmail(email).get();
        GetNotecardsResponse response = new GetNotecardsResponse(notecardRepository.findByOwner(user));
        return ResponseEntity.ok(response.toString());
    }
}
