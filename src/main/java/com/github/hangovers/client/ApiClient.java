package com.github.hangovers.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

import static java.net.http.HttpResponse.BodyHandlers.ofString;

/**
 * Fetches board's data and commands list
 */
public class ApiClient {

    private final HttpClient httpClient;

    public static final Duration TIMEOUT = Duration.ofSeconds(10);

    public ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(TIMEOUT)
                .build();
    }

    /**
     *
     * @param url json url to be fetched
     * @return json data in string format
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
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
