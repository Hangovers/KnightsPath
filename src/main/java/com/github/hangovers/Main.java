package com.github.hangovers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.hangovers.client.ApiClient;
import com.github.hangovers.model.Board;
import com.github.hangovers.model.CommandsList;
import com.github.hangovers.model.Coordinates;
import com.github.hangovers.model.Direction;
import com.github.hangovers.model.dto.KnightPosition;
import com.github.hangovers.model.dto.Response;
import com.github.hangovers.model.dto.Status;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class Main {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws JsonProcessingException {
        var boardDataUrl = System.getenv("BOARD_API");
        var commandsDataUrl = System.getenv("COMMANDS_API");

        validateEnvVariables(boardDataUrl, commandsDataUrl);

        var client = new ApiClient();
        Board board = null;
        List<String> commands = List.of();

        try {
            board = mapper.readValue(client.fetch(boardDataUrl), Board.class);
            commands = mapper.readValue(client.fetch(commandsDataUrl), CommandsList.class).commands();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            outputErrorResponse(Status.GENERIC_ERROR);
        }

        var position = getStartingPosition(commands.getFirst());
        validateStartingPosition(board, position);
        commands.removeFirst();

        for(String command: commands) {
            if(command.startsWith("ROTATE")) {
                position.rotate(Direction.valueOf(command.split(" ")[1]));
                continue;
            } else if (command.startsWith("MOVE")) {
                try {
                    position.move(Integer.parseInt(command.split(" ")[1]), board);
                } catch(IOException e) {
                    outputErrorResponse(Status.OUT_OF_THE_BOARD);
                }
            }
        }
        outputSuccessResponse(position);
    }

    private static KnightPosition getStartingPosition(String startingCommand) throws JsonProcessingException {
        if(startingCommand.startsWith("START ")) {
            startingCommand = startingCommand.substring(6);
            var startingPosition = startingCommand.split(",");

            return new KnightPosition(new Coordinates(Integer.parseInt(startingPosition[0]),
                    Integer.parseInt(startingPosition[1])),
                    Direction.valueOf(startingPosition[2]));
        }
        outputErrorResponse(Status.GENERIC_ERROR);
        return null;
    }

    private static void validateEnvVariables(String boardDataUrl, String commandsDataUrl) throws JsonProcessingException {
        if(boardDataUrl == null || commandsDataUrl == null ||
                boardDataUrl.isBlank() || commandsDataUrl.isBlank()) {
            outputErrorResponse(Status.GENERIC_ERROR);
        }
    }

    private static void validateStartingPosition(Board board, KnightPosition knightPosition) throws JsonProcessingException {
        if (board == null || knightPosition == null) {
            outputErrorResponse(Status.GENERIC_ERROR);
            return;
        }
        if (board.isOutOfBounds(knightPosition.getCoordinates()) || board.checkCollision(knightPosition.getCoordinates())) {
            outputErrorResponse(Status.INVALID_START_POSITION);
        }
    }

    private static void outputErrorResponse(Status status) throws JsonProcessingException {
        System.err.println(mapper.writeValueAsString(new Response(null, status)));
        System.exit(0);
    }

    private static void outputSuccessResponse(KnightPosition knightPosition) throws JsonProcessingException {
        System.out.println(mapper.writeValueAsString(new Response(knightPosition, Status.SUCCESS)));
        System.exit(0);
    }
}