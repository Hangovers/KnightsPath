package com.github.hangovers.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ApiClientTest {

    private MockWebServer mockWebServer;
    private ApiClient apiClient;
    private ObjectMapper objectMapper;

    private static final String BOARD_API_PATH = "/jobrapido-backend-test/board.json";
    private static final String COMMANDS_API_PATH = "/jobrapido-backend-test/commands.json";

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        apiClient = new ApiClient();
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    private String getMockUrl(String path) {
        return mockWebServer.url(path).toString();
    }

    private String loadResourceFile(String resourcePath) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    void fetch_validBoardUrlReturns200() throws Exception {
        String expectedBody = loadResourceFile("expected_board_response.json");
        String url = getMockUrl(BOARD_API_PATH);

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(expectedBody));

        String actualBody = apiClient.fetch(url);

        assertEquals(expectedBody, actualBody);
        assertDoesNotThrow(() -> objectMapper.readTree(actualBody));

        var recordedRequest = mockWebServer.takeRequest();
        assertEquals(BOARD_API_PATH, recordedRequest.getPath());
    }

    @Test
    void fetch_validCommandsUrlReturns200() throws Exception {
        String expectedBody = loadResourceFile("expected_commands_response.json");
        String url = getMockUrl(COMMANDS_API_PATH);

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(expectedBody));

        String actualBody = apiClient.fetch(url);

        assertEquals(expectedBody, actualBody);
        assertDoesNotThrow(() -> objectMapper.readTree(actualBody));

        var recordedRequest = mockWebServer.takeRequest();
        assertEquals(COMMANDS_API_PATH, recordedRequest.getPath());
    }

    @Test
    void fetch_non200Status() {
        String url = getMockUrl("/some/error/path");

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("Not Found"));

        IOException exception = assertThrows(IOException.class, () -> apiClient.fetch(url));
        assertEquals("Fetch failed", exception.getMessage());
    }

    @Test
    void fetch_timeout() {
        String url = getMockUrl("/timeout/path");

        mockWebServer.enqueue(new MockResponse()
                .setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.NO_RESPONSE));

        assertThrows(IOException.class, () -> apiClient.fetch(url));
    }

    @Test
    void fetch_malformedUrl() {
        String malformedUrl = "ht tp://invalid-url";
        assertThrows(URISyntaxException.class, () -> apiClient.fetch(malformedUrl));
    }

    @Test
    void fetch_blankResponse() {
        String url = getMockUrl("/blank/response");

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(""));

        IOException exception = assertThrows(IOException.class, () -> apiClient.fetch(url));
        assertEquals("Fetch failed", exception.getMessage());
    }
} 