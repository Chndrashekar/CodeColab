package com.chandra.code_colab;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Objects;

@Controller
@Slf4j
public class CollabController {

    @Autowired
    private SessionService sessionService;

    @MessageMapping("/edit")
    @SendTo("/topic/collab")
    public String handleEdit(String message, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("sessionId").toString();
        if (sessionId == null) {
            // Handle the case where sessionId is null (e.g., log an error or throw an exception)
            return "Session ID not found";
        }
        log.info("Session ID: " + sessionId);
        sessionService.updateSession(sessionId, message);
        return message;
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
        String sessionContent = sessionService.getSessionContent(sessionId);
        return new ResponseEntity<Object>(sessionContent, HttpStatus.OK);
    }
}

