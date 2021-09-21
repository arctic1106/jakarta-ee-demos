package com.arcticsoft;

import com.arcticsoft.handlers.AddToSessionHandler;
import com.arcticsoft.handlers.DestroySessionHandler;
import com.arcticsoft.handlers.RootHandler;
import com.arcticsoft.handlers.ServerService;

import io.undertow.server.handlers.PathHandler;

public class Main {

    public static void main(String[] args) {
        var pathHandler = new PathHandler();
        pathHandler.addPrefixPath("/", new RootHandler());
        pathHandler.addPrefixPath("/addToSession", new AddToSessionHandler());
        pathHandler.addPrefixPath("/destroySession", new DestroySessionHandler());

        ServerService.startServer(pathHandler);
    }
}