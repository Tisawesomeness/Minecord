package com.tisawesomeness.minecord.mc;

import com.tisawesomeness.minecord.util.MathUtils;
import com.tisawesomeness.minecord.util.type.Either;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Value
public class ServerAddress {

    // modified from https://mkyong.com/regular-expressions/domain-name-regular-expression-example/
    private static final Pattern IP_PATTERN = Pattern.compile("((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|0?[1-9]?[0-9])\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|0?[1-9]?[0-9])");
    private static final Pattern SERVER_PATTERN = Pattern.compile("((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)*[A-Za-z]{2,24}");

    private static final int MC_PORT = 25565;
    private static final int MAX_PORT = 65535;

    String host;
    int port;
    Map<String, String> query;
    boolean ip;

    public static @NonNull Either<String, ServerAddress> parse(@NonNull String input) {
        Map<String, String> query;
        int queryIndex = input.lastIndexOf('?');
        if (queryIndex != -1) {
            String queryStr = input.substring(queryIndex + 1);
            input = input.substring(0, queryIndex);
            query = parseQuery(queryStr);
        } else {
            query = new HashMap<>();
        }

        int atIndex = input.indexOf('@');
        if (atIndex != -1) {
            String id = input.substring(0, atIndex);
            input = input.substring(atIndex + 1);
            query.put("_id", id);
        }

        int port;
        int portIndex = input.lastIndexOf(':');
        if (portIndex != -1) {
            String portStr = input.substring(portIndex + 1);
            input = input.substring(0, portIndex);
            OptionalInt portOpt = MathUtils.safeParseInt(portStr);
            if (!portOpt.isPresent()) {
                return Either.left("Port " + MarkdownUtil.monospace(portStr) + " must be a number from 1 to " + MAX_PORT + ".");
            }
            port = portOpt.getAsInt();
            if (port < 1 || MAX_PORT < port) {
                return Either.left("Port " + MarkdownUtil.monospace(portStr) + " must be a number from 1 to " + MAX_PORT + ".");
            }
        } else {
            port = MC_PORT;
        }

        String host = input;
        boolean ip;
        if (IP_PATTERN.matcher(host).matches()) {
            ip = true;
        } else if (SERVER_PATTERN.matcher(host).matches()) {
            ip = false;
        } else {
            return Either.left("Host " + MarkdownUtil.monospace(host) + " must be a valid domain name or IP address.");
        }
        return Either.right(new ServerAddress(host, port, query, ip));
    }

    private static Map<String, String> parseQuery(@NonNull String input) {
        Map<String, String> query = new HashMap<>();
        for (String parameter : input.split("&")) {
            int separatorIndex = parameter.indexOf('=');
            String key = unescape(parameter.substring(0, separatorIndex));
            String value = unescape(parameter.substring(separatorIndex + 1));
            query.put(key, value);
        }
        return query;
    }

    public String hostAndPort() {
        if (port == MC_PORT) {
            return host;
        }
        return host + ":" + port;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        String id = query.get("_id");
        if (id != null) {
            output.append(id).append('@');
        }
        output.append(host);
        if (port != MC_PORT) {
            output.append(':').append(port);
        }
        if (query.size() > (id != null ? 1 : 0)) {
            String queryStr = query.entrySet().stream()
                    .filter(en -> !en.getKey().equals("_id"))
                    .map(en -> escape(en.getKey()) + "=" + escape(en.getValue()))
                    .collect(Collectors.joining("&"));
            output.append('?').append(queryStr);
        }
        return output.toString();
    }

    @SneakyThrows
    private static String escape(String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8.name());
    }
    @SneakyThrows
    private static String unescape(String str) {
        return URLDecoder.decode(str, StandardCharsets.UTF_8.name());
    }

}
