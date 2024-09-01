package com.chandra.code_colab;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {

    private final Map<String, StringBuilder> sessions = new ConcurrentHashMap<>();

    public void createSession(String sessionId) {
        sessions.put(sessionId, new StringBuilder());
    }

    public String getSessionContent(String sessionId) {
        return sessions.getOrDefault(sessionId, new StringBuilder()).toString();
    }

    public void updateSession(String sessionId, String newContent) {
//        if (sessions.containsKey(sessionId)) {
//            sessions.get(sessionId).append(newContent);
//        } else {
//            // Handle case where session doesn't exist (unlikely if session management is correct)
//            createSession(sessionId);
//            sessions.get(sessionId).append(newContent);
//        }
        sessions.put(sessionId, new StringBuilder(newContent));
    }

    public boolean sessionExists(String sessionId) {
        return sessions.containsKey(sessionId);
    }
}
