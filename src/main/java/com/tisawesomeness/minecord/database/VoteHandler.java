package com.tisawesomeness.minecord.database;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Scanner;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.entities.User;

@RequiredArgsConstructor
public class VoteHandler {
	
	private HttpServer server;
	private final @NonNull ShardManager sm;
	
	public void init() throws IOException {
		server = HttpServer.create(new InetSocketAddress(Config.getWebhookPort()), 0);
		server.createContext("/" + Config.getWebhookURL(), new HttpHandler() {
			
			private static final String response = "OK";
			
			@Override
			public void handle(HttpExchange t) throws IOException {
				
				//Check if request is a POST request and the authorization is correct
				if ("POST".equals(t.getRequestMethod())
						&& t.getRequestHeaders().getOrDefault("Authorization", Arrays.asList("N/A"))
						.get(0).equals(Config.getWebhookAuth())) {
					
					//Get post body
					Scanner scanner = new Scanner(t.getRequestBody());
					String body = scanner.useDelimiter("\\A").next();
					scanner.close();
					
					//Send DM to user and log vote
					JSONObject o = new JSONObject(body);
					boolean upvote = "upvote".equals(o.getString("type"));
					String msg = upvote ? "Thanks for voting!" : "y u do dis";
					String id = o.getString("user");
					User u = sm.getUserById(id);
					msg = upvote ? "upvoted!" : "downvoted ;(";
					if (u == null) {
						MessageUtils.log(String.format("(`%s`) %s", id, msg));
					} else {
						u.openPrivateChannel().complete().sendMessage(msg).queue();
						MessageUtils.log(String.format("%s (`%s`) %s", u.getAsTag(), id, msg));
					}
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
	
	public void close() {
		if (server != null) server.stop(0);
	}
	
}