package com.tisawesomeness.minecord.database;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.tisawesomeness.minecord.Bot;
import lombok.NonNull;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.tisawesomeness.minecord.Config;

import net.dv8tion.jda.api.entities.User;

public class VoteHandler {
	
	private HttpServer server;
	private final @NonNull Bot bot;
	private final @NonNull Config config;

	public VoteHandler(Bot bot, Config config) {
		this.bot = bot;
		this.config = config;
	}

	private ExecutorService exe = Executors.newSingleThreadExecutor();
	public Future<Boolean> start() {
		return exe.submit(() -> {
			if (!config.shouldReceiveVotes()) {
				return true;
			}
			try {
				init();
				return true;
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return false;
		});
	}
	private void init() throws IOException {
		server = HttpServer.create(new InetSocketAddress(config.getWebhookPort()), 0);
		server.createContext("/" + config.getWebhookURL(), new HttpHandler() {
			
			private static final String response = "OK";
			
			@Override
			public void handle(HttpExchange t) throws IOException {
				
				//Check if request is a POST request and the authorization is correct
				if ("POST".equals(t.getRequestMethod())
						&& t.getRequestHeaders().getOrDefault("Authorization", Arrays.asList("N/A"))
						.get(0).equals(config.getWebhookAuth())) {
					
					//Get post body
					Scanner scanner = new Scanner(t.getRequestBody());
					String body = scanner.useDelimiter("\\A").next();
					scanner.close();
					
					//Send DM to user and log vote
					JSONObject o = new JSONObject(body);
					boolean upvote = "upvote".equals(o.getString("type"));
					String msg = upvote ? "Thanks for voting!" : "y u do dis";
					String id = o.getString("user");
					User u = bot.getShardManager().getUserById(id);
					String logMsg = upvote ? "upvoted!" : "downvoted ;(";
					if (u == null) {
						bot.log(String.format("(`%s`) %s", id, logMsg));
					} else {
						u.openPrivateChannel().complete().sendMessage(msg).queue();
						bot.log(String.format("%s (`%s`) %s", u.getAsTag(), id, logMsg));
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
		exe.shutdownNow();
	}
	
}