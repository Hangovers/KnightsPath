package com.github.hangovers.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

import static java.net.http.HttpResponse.BodyHandlers.ofString;

public class ApiClient {

    private final HttpClient httpClient;

    public static final Duration TIMEOUT = Duration.ofSeconds(10);

    public ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(TIMEOUT)
                .build();
    }

    public String fetch(String url) throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Accept","application/json")
                .timeout(TIMEOUT)
                .GET()
                .build();

        var response = httpClient.send(request,
                ofString());

        if (response.statusCode() != 200 || response.body().isBlank()) {
            throw new IOException("Fetch failed");
        }

        return response.body();
    }
}
