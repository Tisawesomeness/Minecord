package com.tisawesomeness.minecord.listing;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.network.StatusCodes;
import com.tisawesomeness.minecord.util.DiscordUtils;
import lombok.Cleanup;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Scanner;

public class VoteHandler implements HttpHandler {

    private static HttpServer server;

    public static void init() throws IOException {
        server = HttpServer.create(new InetSocketAddress(Config.getWebhookPort()), 0);
        server.createContext("/" + Config.getWebhookURL(), new VoteHandler());
        server.start();
        System.out.println("Web server started.");
    }

    public static void close() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        if (!"POST".equals(t.getRequestMethod())) {
            respond(t, StatusCodes.METHOD_NOT_ALLOWED, "Method Not Allowed");
        }

        String auth = t.getRequestHeaders().getOrDefault("Authorization", Collections.singletonList("N/A")).get(0);
        if (!auth.equals(Config.getWebhookAuth())) {
            respond(t, StatusCodes.FORBIDDEN, "Forbidden");
        }

        try {
            @Cleanup Scanner scanner = new Scanner(t.getRequestBody());
            String body = scanner.useDelimiter("\\A").next();
            JSONObject bodyJson = new JSONObject(body);

            boolean upvote = "upvote".equals(bodyJson.getString("type"));
            Bot.shardManager.retrieveUserById(bodyJson.getString("user")).queue(user -> {

                String logMsg = upvote ? "upvoted!" : "downvoted ;(";
                logMsg = DiscordUtils.tagAndId(user) + " " + logMsg;
                Bot.logger.joinLog(logMsg);
                System.out.println(logMsg);

                if (upvote) {
                    user.openPrivateChannel().queue(c -> c.sendMessage("Thanks for voting!").queue());
                }

            });
        } finally {
            respond(t, StatusCodes.OK, "OK");
        }
    }

    private static void respond(HttpExchange t, int statusCode, String message) throws IOException {
        t.sendResponseHeaders(statusCode, message.length());
        @Cleanup OutputStream os = t.getResponseBody();
        os.write(message.getBytes());
    }

}
