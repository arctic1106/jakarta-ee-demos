package com.arcticsoft.handlers;

import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.session.InMemorySessionManager;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionAttachmentHandler;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionCookieConfig;
import io.undertow.server.session.SessionManager;

public class ServerService {

    public static void startServer(PathHandler pathHandler) {
        var sessionAttachmentHandler = new SessionAttachmentHandler(
                new InMemorySessionManager("SESSION_MANAGER"),
                new SessionCookieConfig());

        sessionAttachmentHandler.setNext(pathHandler);

        var server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(sessionAttachmentHandler)
                .build();

        server.start();

        System.out.println("Serving on http://localhost:8080");
    }

    public static Session getSession(HttpServerExchange exchange) {
        var sessionManager = exchange.getAttachment(SessionManager.ATTACHMENT_KEY);
        var sessionConfig = exchange.getAttachment(SessionConfig.ATTACHMENT_KEY);

        var session = sessionManager.getSession(exchange, sessionConfig);
        if (session == null) session = sessionManager.createSession(exchange, sessionConfig);

        return session;
    }
}