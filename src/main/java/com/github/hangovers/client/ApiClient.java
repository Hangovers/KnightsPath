package com.github.hangovers.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.hangovers.model.Board;
import com.github.hangovers.model.CommandsList;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

import static java.net.http.HttpResponse.BodyHandlers.ofString;

public class ApiClient {


    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private static final String BOARD_API = System.getenv("BOARD_API");
    private static final String COMMANDS_API = System.getenv("COMMANDS_API");
    public static final Duration TIMEOUT = Duration.ofSeconds(10);

    public ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(TIMEOUT)
                .build();

        this.objectMapper = new ObjectMapper();
    }

    public Board fetchBoard() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder()
                .uri(new URI(BOARD_API))
                .header("Accept","application/json")
                .timeout(TIMEOUT)
                .GET()
                .build();

        var response = httpClient.send(request,
                ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch board: HTTP status code " + response.statusCode());
        }

        return objectMapper.readValue(response.body(), Board.class);
    }

    public CommandsList fetchCommands() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder()
                .uri(new URI(COMMANDS_API))
                .header("Accept","application/json")
                .timeout(TIMEOUT)
                .GET()
                .build();

        var response = httpClient.send(request,
                ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch commands: HTTP status code " + response.statusCode());
        }

        return objectMapper.readValue(response.body(), CommandsList.class);
    }


}
