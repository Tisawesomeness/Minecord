package com.tisawesomeness.minecord.database;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.util.DiscordUtils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Scanner;

public class VoteHandler {

    private static HttpServer server;

    public static void init() throws IOException {
        server = HttpServer.create(new InetSocketAddress(Config.getWebhookPort()), 0);
        server.createContext("/" + Config.getWebhookURL(), new HttpHandler() {

            private static final String response = "OK";

            @Override
            public void handle(HttpExchange t) throws IOException {

                //Check if request is a POST request and the authorization is correct
                if ("POST".equals(t.getRequestMethod())
                        && t.getRequestHeaders().getOrDefault("Authorization", Collections.singletonList("N/A"))
                        .get(0).equals(Config.getWebhookAuth())) {

                    //Get post body
                    Scanner scanner = new Scanner(t.getRequestBody());
                    String body = scanner.useDelimiter("\\A").next();
                    scanner.close();

                    //Send DM to user and log vote
                    JSONObject o = new JSONObject(body);
                    boolean upvote = "upvote".equals(o.getString("type"));
                    String msg = upvote ? "Thanks for voting!" : "y u do dis";
                    Bot.shardManager.retrieveUserById(o.getString("user")).queue(u -> {

                        u.openPrivateChannel().queue(c -> c.sendMessage(msg).queue());

                        String logMsg = upvote ? "upvoted!" : "downvoted ;(";
                        logMsg = DiscordUtils.tagAndId(u) + " " + logMsg;
                        Bot.logger.joinLog(logMsg);
                        System.out.println(logMsg);

                    });
                }

                //Respond with "OK"
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });

        server.start();
        System.out.println("Web server started.");
    }

    public static void close() {
        if (server != null) server.stop(0);
    }

}
