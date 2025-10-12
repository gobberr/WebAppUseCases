package com.webapp.backend.session;

import java.util.Optional;

public interface SessionService {

    String login(String userId);

    String logout(String userId);

    Optional<String> getSession(String userId);
}
