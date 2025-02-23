package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
//public class SocialMediaController {

//}

@RestController
public class SocialMediaController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageService messageService;

    // 1. POST /register: Create a new account.
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Account account) {
        // Validate input
        if (account.getUsername() == null || account.getUsername().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Username cannot be blank");
        }
        if (account.getPassword() == null || account.getPassword().length() < 4) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Password must be at least 4 characters");
        }
        // Try to register the account
        Account created = accountService.register(account);
        if (created == null) {
            // Means an account with the given username already exists
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username already exists");
        }
        return ResponseEntity.ok(created);
    }

    // 2. POST /login: Verify user credentials.
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Account account) {
        Account found = accountService.login(account);
        if (found == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
        return ResponseEntity.ok(found);
    }

    // 3. POST /messages: Create a new message.
    @PostMapping("/messages")
    public ResponseEntity<?> createMessage(@RequestBody Message message) {
        // Validate messageText
        if (message.getMessageText() == null || message.getMessageText().trim().isEmpty() ||
            message.getMessageText().length() > 255) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Message text is either blank or exceeds 255 characters");
        }
        // Validate that the user (postedBy) exists
        if (!accountService.exists(message.getPostedBy())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Account (postedBy) does not exist");
        }
        Message created = messageService.createMessage(message);
        return ResponseEntity.ok(created);
    }

    // 4. GET /messages: Retrieve all messages.
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    // 5. GET /messages/{messageId}: Retrieve a message by its ID.
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<?> getMessageById(@PathVariable Integer messageId) {
        Message message = messageService.getMessageById(messageId);
        // Return an empty body if the message does not exist.
        if (message == null) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok(message);
    }

    // 6. DELETE /messages/{messageId}: Delete a message by its ID.
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Integer messageId) {
        boolean existed = messageService.exists(messageId);
        int rowsAffected = messageService.deleteMessage(messageId);
        if (existed) {
            return ResponseEntity.ok(rowsAffected);
        }
        // If message did not exist, return empty body (idempotent behavior)
        return ResponseEntity.ok().build();
    }

    // 7. PATCH /messages/{messageId}: Update the text of a message.
    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<?> updateMessage(@PathVariable Integer messageId,
                                           @RequestBody Message updateInfo) {
        String newText = updateInfo.getMessageText();
        if (newText == null || newText.trim().isEmpty() || newText.length() > 255) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("New message text is either blank or exceeds 255 characters");
        }
        boolean updated = messageService.updateMessageText(messageId, newText);
        if (updated) {
            // Return 1 to indicate one row was updated
            return ResponseEntity.ok(1);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Update failed – message may not exist");
    }

    // 8. GET /accounts/{accountId}/messages: Retrieve all messages by a specific account.
    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getMessagesByAccount(@PathVariable Integer accountId) {
        List<Message> messages = messageService.getMessagesByAccountId(accountId);
        return ResponseEntity.ok(messages);
    }
}
