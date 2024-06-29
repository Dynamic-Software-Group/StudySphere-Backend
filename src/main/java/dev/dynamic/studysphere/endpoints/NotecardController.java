package dev.dynamic.studysphere.endpoints;

import dev.dynamic.studysphere.model.*;
import dev.dynamic.studysphere.model.request.CreateNotecardRequest;
import dev.dynamic.studysphere.model.request.FavoriteNotecardRequest;
import dev.dynamic.studysphere.model.request.UpdateNotecardRequest;
import dev.dynamic.studysphere.model.response.CreateNotecardResponse;
import dev.dynamic.studysphere.model.response.GetNotecardsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/notecard")
public class NotecardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotecardCategoryRepository categoryRepository;

    @Autowired
    private NotecardRepository notecardRepository;

    // Create method

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

    // Add favorite method

    @PostMapping(value = "/favorite", consumes = "application/json", produces = "application/json")
    public ResponseEntity favoriteNotecard(@RequestBody FavoriteNotecardRequest request) {
        String email = request.getEmail();
        if (userRepository.findByEmail(email).isEmpty()) {
            return ResponseEntity.status(401).body("User not found");
        }
        User user = userRepository.findByEmail(email).get();
        String notecardId = request.getNotecardId();
        if (notecardRepository.findById(Long.parseLong(notecardId)).isEmpty()) {
            return ResponseEntity.status(401).body("Notecard not found");
        }
        Notecard notecard = notecardRepository.findById(Long.parseLong(notecardId)).get();
        user.getFavoriteNotecards().add(notecard);
        return ResponseEntity.ok("Notecard added to favorites");
    }

    // List methods

    @GetMapping(value = "/list", produces = "application/json")
    public ResponseEntity getNotecards(@RequestParam String email) {
        if (userRepository.findByEmail(email).isEmpty()) {
            return ResponseEntity.status(401).body("User not found");
        }
        User user = userRepository.findByEmail(email).get();
        GetNotecardsResponse response = new GetNotecardsResponse(notecardRepository.findByOwnerAndDeletedFalse(user));
        return ResponseEntity.ok(response.toString());
    }

    @GetMapping(value = "/list_category", produces = "application/json")
    public ResponseEntity getNotecardsByCategory(@RequestParam String email, @RequestParam String category) {
        if (userRepository.findByEmail(email).isEmpty()) {
            return ResponseEntity.status(401).body("User not found");
        }
        if (categoryRepository.findByNameAndOwner(category, userRepository.findByEmail(email).get()) == null) {
            return ResponseEntity.status(401).body("Category not found");
        }
        User user = userRepository.findByEmail(email).get();
        NotecardCategory notecardCategory = categoryRepository.findByNameAndOwner(category, user);
        GetNotecardsResponse response = new GetNotecardsResponse(notecardRepository.findByCategoryAndOwnerAndDeletedFalse(notecardCategory, user));
        return ResponseEntity.ok(response.toString());
    }

    @GetMapping(value = "/favorites", produces = "application/json")
    public ResponseEntity getFavorites(@RequestParam String email) {
        if (userRepository.findByEmail(email).isEmpty()) {
            return ResponseEntity.status(401).body("User not found");
        }
        User user = userRepository.findByEmail(email).get();
        GetNotecardsResponse response = new GetNotecardsResponse(user.getFavoriteNotecards());
        return ResponseEntity.ok(response.toString());
    }

    // Updating methods

    @PatchMapping(value = "/update", consumes = "application/json", produces = "application/json")
    public ResponseEntity updateNotecard(@RequestBody UpdateNotecardRequest request) {
        String email = request.getEmail();
        if (userRepository.findByEmail(email).isEmpty()) {
            return ResponseEntity.status(401).body("User not found");
        }
        User user = userRepository.findByEmail(email).get();
        String notecardId = request.getNotecardId();
        if (notecardRepository.findById(Long.parseLong(notecardId)).isEmpty()) {
            return ResponseEntity.status(401).body("Notecard not found");
        }
        Notecard notecard = notecardRepository.findById(Long.parseLong(notecardId)).get();
        if (!notecard.getOwner().equals(user)) {
            return ResponseEntity.status(401).body("Notecard not owned by user");
        }
        if (request.getName() != null) {
            notecard.setName(request.getName());
        }
        if (request.getContent() != null) {
            notecard.setContent(request.getContent());
        }
        if (request.getCategory() != null) {
            notecard.setCategory(categoryRepository.findByNameAndOwner(request.getCategory(), user));
        }
        notecardRepository.save(notecard);
        return ResponseEntity.ok("Notecard updated");
    }

    // Deleting methods

    @DeleteMapping(value = "/delete", produces = "application/json")
    public ResponseEntity deleteNotecard(@RequestParam String email, @RequestParam String notecardId) {
        if (userRepository.findByEmail(email).isEmpty()) {
            return ResponseEntity.status(401).body("User not found");
        }
        User user = userRepository.findByEmail(email).get();
        if (notecardRepository.findById(Long.parseLong(notecardId)).isEmpty()) {
            return ResponseEntity.status(401).body("Notecard not found");
        }
        Notecard notecard = notecardRepository.findById(Long.parseLong(notecardId)).get();
        if (!notecard.getOwner().equals(user)) {
            return ResponseEntity.status(401).body("Notecard not owned by user");
        }
        notecard.setDeleted(true);
        notecardRepository.save(notecard);
        return ResponseEntity.ok("Notecard deleted");
    }

    @DeleteMapping(value = "/delete_category", produces = "application/json")
    public ResponseEntity deleteCategory(@RequestParam String email, @RequestParam String category) {
        if (userRepository.findByEmail(email).isEmpty()) {
            return ResponseEntity.status(401).body("User not found");
        }
        User user = userRepository.findByEmail(email).get();
        if (categoryRepository.findByNameAndOwner(category, user) == null) {
            return ResponseEntity.status(401).body("Category not found");
        }
        NotecardCategory notecardCategory = categoryRepository.findByNameAndOwner(category, user);
        Set<Notecard> notecards = notecardRepository.findByCategoryAndOwnerAndDeletedFalse(notecardCategory, user);
        for (Notecard notecard : notecards) {
            notecard.setCategory(null);
            notecardRepository.save(notecard);
        }
        categoryRepository.delete(notecardCategory);
        return ResponseEntity.ok("Category deleted");
    }

}