package com.arcticsoft.handlers;

import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

public class DestroySessionHandler implements io.undertow.server.HttpHandler {

    @Override
    public void handleRequest(io.undertow.server.HttpServerExchange exchange) throws Exception {
        ServerService.getSession(exchange).invalidate(exchange);
        exchange.setStatusCode(StatusCodes.TEMPORARY_REDIRECT);
        exchange.getResponseHeaders().put(Headers.LOCATION, "/");
        exchange.getResponseSender().close();
    }
}