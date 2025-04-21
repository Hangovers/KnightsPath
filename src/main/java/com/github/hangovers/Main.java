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

        // Env variables fetching
        var boardDataUrl = System.getenv("BOARD_API");
        var commandsDataUrl = System.getenv("COMMANDS_API");

        // Env variables validation
        validateEnvVariables(boardDataUrl, commandsDataUrl);

        // Needed initializations
        var client = new ApiClient();
        Board board = null;
        List<String> commands = List.of();

        // Board and commands mapping
        try {
            board = mapper.readValue(client.fetch(boardDataUrl), Board.class);
            commands = mapper.readValue(client.fetch(commandsDataUrl), CommandsList.class).commands();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            // Generic error if something goes wrong during mapping
            outputErrorResponse(Status.GENERIC_ERROR);
        }

        // Starting position logic and validation
        var position = getStartingPosition(commands.getFirst());
        validateStartingPosition(board, position);

        // Removal of starting position data in order to enable enhanced for loops usage
        commands.removeFirst();

        // Commands execution
        executeCommands(commands, position, board);

        // Output
        outputSuccessResponse(position);
    }

    //Knight movement logic

    /**
     * Initializes a KnightPosition object with starting position data or outputs a generic error if there are parsing problems in the command
     * @param startingCommand command with starting position data, e.g. "START 1,0,NORTH"
     * @return KnightPosition object
     * @throws JsonProcessingException
     */
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

    /**
     * Execute all the commands in the list for movement and direction changes
     * @param commands commands list
     * @param position knight's position to be updated
     * @param board board where the knight is moving
     * @throws JsonProcessingException
     */
    private static void executeCommands(List<String> commands, KnightPosition position, Board board) throws JsonProcessingException {
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
    }

    // Validation logic

    /**
     * Validates environmental variables retrieval
     * @param boardDataUrl url where board data is stored
     * @param commandsDataUrl url where knight commands are stored
     * @throws JsonProcessingException
     */
    private static void validateEnvVariables(String boardDataUrl, String commandsDataUrl) throws JsonProcessingException {
        if(boardDataUrl == null || commandsDataUrl == null ||
                boardDataUrl.isBlank() || commandsDataUrl.isBlank()) {
            outputErrorResponse(Status.GENERIC_ERROR);
        }
    }

    /**
     * Validates knight's starting position on the board
     * @param board Board's data
     * @param knightPosition Knight's starting position
     * @throws JsonProcessingException
     */
    private static void validateStartingPosition(Board board, KnightPosition knightPosition) throws JsonProcessingException {
        if (board == null || knightPosition == null) {
            outputErrorResponse(Status.GENERIC_ERROR);
            return;
        }
        if (board.isOutOfBounds(knightPosition.getCoordinates()) || board.checkCollision(knightPosition.getCoordinates())) {
            outputErrorResponse(Status.INVALID_START_POSITION);
        }
    }

    // Response methods

    /**
     * Outputs an error response
     * @param status error reason
     * @throws JsonProcessingException
     */
    private static void outputErrorResponse(Status status) throws JsonProcessingException {
        System.err.println(mapper.writeValueAsString(new Response(null, status)));
        System.exit(0);
    }

    /**
     * Outputs successful response
     * @param knightPosition Knight's position after commands execution
     * @throws JsonProcessingException
     */
    private static void outputSuccessResponse(KnightPosition knightPosition) throws JsonProcessingException {
        System.out.println(mapper.writeValueAsString(new Response(knightPosition, Status.SUCCESS)));
        System.exit(0);
    }
}