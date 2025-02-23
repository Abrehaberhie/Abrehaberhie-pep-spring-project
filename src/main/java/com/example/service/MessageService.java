package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Message;
import com.example.repository.MessageRepository;

//public class MessageService {
//}

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    // Creates and persists a new message.
    public Message createMessage(Message message) {
        return messageRepository.save(message);
    }

    // Retrieves all messages.
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    // Retrieves a message by its ID.
    public Message getMessageById(Integer messageId) {
        Optional<Message> opt = messageRepository.findById(messageId);
        return opt.orElse(null);
    }

    // Checks if a message exists.
    public boolean exists(Integer messageId) {
        if (messageId == null) return false;
        return messageRepository.existsById(messageId);
    }

    // Deletes a message if it exists; returns 1 if deleted, 0 if not.
    public int deleteMessage(Integer messageId) {
        if (messageRepository.existsById(messageId)) {
            messageRepository.deleteById(messageId);
            return 1;
        }
        return 0;
    }

    // Updates the text of an existing message.
    public boolean updateMessageText(Integer messageId, String newText) {
        Optional<Message> opt = messageRepository.findById(messageId);
        if (opt.isPresent()) {
            Message message = opt.get();
            message.setMessageText(newText);
            messageRepository.save(message);
            return true;
        }
        return false;
    }

    // Retrieves all messages posted by a particular account.
    public List<Message> getMessagesByAccountId(Integer accountId) {
        return messageRepository.findByPostedBy(accountId);
    }
}