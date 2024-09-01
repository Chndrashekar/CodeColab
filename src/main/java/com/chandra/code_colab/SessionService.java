package com.chandra.code_colab;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {

    private final Map<String, StringBuilder> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionLanguages = new ConcurrentHashMap<>();

    private final Map<String, String> sessionDescriptions = new ConcurrentHashMap<>();

    public void createSession(String sessionId) {
        sessions.put(sessionId, new StringBuilder());
        sessionDescriptions.put(sessionId, "");
        sessionLanguages.put(sessionId, "javascript");  // Default language
    }

    public String getSessionContent(String sessionId) {
        return sessions.getOrDefault(sessionId, new StringBuilder()).toString();
    }

    public String getSessionDescription(String sessionId) {
        return sessionDescriptions.getOrDefault(sessionId, "");
    }

    public void updateSession(String sessionId, String newContent) {
        sessions.put(sessionId, new StringBuilder(newContent));
    }

    public void updateSessionDescription(String sessionId, String newDescription) {
        sessionDescriptions.put(sessionId, newDescription);
    }

    public boolean sessionExists(String sessionId) {
        return sessions.containsKey(sessionId);
    }

    public String getSessionLanguage(String sessionId) {
        return sessionLanguages.getOrDefault(sessionId, "javascript");  // Default language if not set
    }

    public void setSessionLanguage(String sessionId, String language) {
        sessionLanguages.put(sessionId, language);
    }
}
