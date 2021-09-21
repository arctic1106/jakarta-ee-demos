package com.arcticsoft.handlers;

import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

public class AddToSessionHandler implements io.undertow.server.HttpHandler {

    @Override
    public void handleRequest(io.undertow.server.HttpServerExchange exchange) throws Exception {
        var reqParams = exchange.getQueryParameters();
        var session = ServerService.getSession(exchange);
        var attributeNames = reqParams.get("attrName");
        var attributeValues = reqParams.get("value");
        session.setAttribute(attributeNames.getLast(), attributeValues.getLast());

        exchange.setStatusCode(StatusCodes.TEMPORARY_REDIRECT);
        exchange.getResponseHeaders().put(Headers.LOCATION, "/");
        exchange.getResponseSender().close();
    }
}