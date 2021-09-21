package com.arcticsoft.handlers;

import io.undertow.util.Headers;

public class RootHandler implements io.undertow.server.HttpHandler {

    @Override
    public void handleRequest(io.undertow.server.HttpServerExchange exchange) throws Exception {
        var response = """
                    <form action='addToSession'>
                        <label>Attribute Name</label>
                        <input name='attrName' />
                        <label>Attribute Value</label>
                        <input name='value' />
                        <button>Save to Session</button>
                    </form>
                    <a href='/destroySession'>Destroy Session</a>
                    <br/>
                    <ul>
                """;

        var session = ServerService.getSession(exchange);

        for (var attributeName : session.getAttributeNames()) {
            response += "<li> : " + session.getAttribute(attributeName) + "</li>";
        }
        response += "</ul>";

        exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "text/html;");
        exchange.getResponseSender().send(response);
    }
}