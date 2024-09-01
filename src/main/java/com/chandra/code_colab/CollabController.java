package com.chandra.code_colab;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.Objects;

@Controller
@Slf4j
public class CollabController {

    @Autowired
    private SessionService sessionService;

    @MessageMapping("/edit")
    @SendTo("/topic/collab")
    public ResponseEntity<Object> handleEdit(String message, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("sessionId").toString();
        if (sessionId == null) {
            // Handle the case where sessionId is null (e.g., log an error or throw an exception)
            return new ResponseEntity<>("Session ID not found", HttpStatus.BAD_REQUEST);
        }
        sessionService.updateSession(sessionId, message);
        return new ResponseEntity<Object>(Map.of("content", message), HttpStatus.OK);
    }

    @MessageMapping("/join")
    @SendTo("/topic/collab")
    public ResponseEntity<Object> handleJoin(String sessionId, SimpMessageHeaderAccessor headerAccessor) {
        if (!sessionService.sessionExists(sessionId)) {
            sessionService.createSession(sessionId);
            log.info("Session created with ID: " + sessionId);
        }

        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("sessionId", sessionId);
        log.info("Message sent: " + sessionService.getSessionContent(sessionId));

        // Send the current session content to the joining user
        return getObjectResponseEntity(sessionId);
    }

    private ResponseEntity<Object> getObjectResponseEntity(String sessionId) {
        String sessionContent = sessionService.getSessionContent(sessionId);
        String sessionLanguage = sessionService.getSessionLanguage(sessionId);
        String sessionDescription = sessionService.getSessionDescription(sessionId);
        log.info(sessionContent, sessionDescription, sessionLanguage);
        return new ResponseEntity<>(Map.of("content", sessionContent, "description", sessionDescription, "language", sessionLanguage), HttpStatus.OK);
    }

    @MessageMapping("/setLanguage")
    @SendTo("/topic/collab")
    public ResponseEntity<Object> handleSetLanguage(@Payload String message, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("sessionId");

        if (sessionId == null) {
            throw new IllegalArgumentException("Session ID not found in headers");
        }

        // Update the session's language
        sessionService.setSessionLanguage(sessionId, message);
        // Send the current session content to the joining user
        return new ResponseEntity<>(Map.of("language", message), HttpStatus.OK);
    }

    @MessageMapping("/setDescription")
    @SendTo("/topic/collab")
    public ResponseEntity<Object> handleSetDescription(@Payload String message, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("sessionId");

        if (sessionId == null) {
            throw new IllegalArgumentException("Session ID not found in headers");
        }

        // Update the session's description
        sessionService.updateSessionDescription(sessionId, message);
        // Send the current session content to the joining user
        return new ResponseEntity<>(Map.of("description", message), HttpStatus.OK);

    }
}

