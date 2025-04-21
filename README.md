# KnightsPath

This application calculates the final position of a knight on a board based on a series of movement commands. It fetches the board layout and the command sequence from external APIs.

## Features

*   Fetches board dimensions and obstacle locations from a specified API endpoint.
*   Fetches a sequence of commands (start position, rotations, movements) from another API endpoint.
*   Simulates the knight's movement according to the commands.
*   Handles board boundaries: Stops execution if the knight moves out of bounds.
*   Handles obstacles: Stops movement in the current direction if an obstacle is encountered.
*   Outputs the final position (x, y, direction) and status (SUCCESS or error code) in JSON format.

## Prerequisites

*   Java Development Kit (JDK), version 21 is suggested but 17 should work too.
*   Gradle (for building the project, the wrapper `gradlew` is included).
*   Alternatively, Docker can be used to build and run the application in a container.

## Setup

The application requires two environment variables to be set, pointing to the APIs that provide the board and command data:

*   `BOARD_API`: URL for the API endpoint returning the board configuration (JSON format expected, see below).
*   `COMMANDS_API`: URL for the API endpoint returning the list of commands (JSON format expected, see below).

Example (Linux/macOS):
```bash
export BOARD_API="https://storage.googleapis.com/jobrapido-backend-test/board.json"
export COMMANDS_API="https://storage.googleapis.com/jobrapido-backend-test/commands.json"
```

Example (Windows PowerShell):
```powershell
$env:BOARD_API="https://storage.googleapis.com/jobrapido-backend-test/board.json"
$env:COMMANDS_API="https://storage.googleapis.com/jobrapido-backend-test/commands.json"
```

## Building the Application

The project uses Gradle. The included Gradle wrapper (`gradlew` or `gradlew.bat`) can be used to build the application.

1.  Navigate to the project's root directory in your terminal.
2.  Run the build command:
    *   Linux/macOS: `./gradlew build`
    *   Windows: `.\gradlew.bat build`

This command compiles the code, runs tests, and creates a fat JAR file (including all dependencies) in the `build/libs/` directory (e.g., `KnightsPath-1.0-SNAPSHOT.jar`).

## Running the Application

Make sure the `BOARD_API` and `COMMANDS_API` environment variables are set before running.

### Option 1: Using Gradle

*   Linux/macOS: `./gradlew run`
*   Windows: `.\gradlew.bat run`

### Option 2: Running the JAR directly

1.  Build the application first (`./gradlew build`).
2.  Run the generated JAR file:
    `java -jar build/libs/KnightsPath-1.0-SNAPSHOT.jar`

### Option 3: Using Docker

1.  Build the Docker image:
    `docker build -t knight_board:latest .`
2.  Run the container, passing the environment variables:
    ```bash
    docker run --rm \
      -e BOARD_API="<your_board_api_url>" \
      -e COMMANDS_API="<your_commands_api_url>" \
      knight-predictor
    ```
    Replace `<your_board_api_url>` and `<your_commands_api_url>` with the actual API endpoints.
    Input commands you provided in the pdf works fine.

## Input API Formats

The application expects the following JSON structures from the APIs:

### Board API (`BOARD_API`)

```json
{
  "width": 8,
  "height": 8,
  "obstacles": [
    {"x": 2, "y": 3},
    {"x": 5, "y": 5}
  ]
}
```
*   `width`: Integer, the width of the board.
*   `height`: Integer, the height of the board.
*   `obstacles`: An array of coordinate objects representing impassable squares. Coordinates are 0-indexed.

### Commands API (`COMMANDS_API`)

```json
{
  "commands": [
    "START 0,0,NORTH",
    "MOVE 2",
    "ROTATE EAST",
    "MOVE 1",
    "ROTATE SOUTH",
    "MOVE 3"
  ]
}
```
*   `commands`: An array of strings representing the command sequence.
    *   `START x,y,DIRECTION`: Must be the first command. Sets the initial 0-indexed coordinates (`x`, `y`) and `DIRECTION` (NORTH, EAST, SOUTH, WEST).
    *   `MOVE steps`: Moves the knight `steps` squares in the current direction. Stops if an obstacle is hit.
    *   `ROTATE DIRECTION`: Changes the knight's facing direction to the specified `DIRECTION`.

## Output Format

The application prints a single JSON object to standard output (on success) or standard error (on failure).

### Success Output (stdout)

```json
{
  "position": {
    "x": 1,
    "y": 3,
    "direction": "SOUTH"
  },
  "status": "SUCCESS"
}
```
*   `position`: Contains the final `x`, `y` coordinates and `direction` of the knight. Note that `coordinates` are unwrapped directly into `x` and `y`.
*   `status`: Always `SUCCESS`.

### Error Output (stderr)

```json
{
  "position": null,
  "status": "OUT_OF_THE_BOARD"
}
```
or
```json
{
  "position": null,
  "status": "INVALID_START_POSITION"
}
```
or
```json
{
  "position": null,
  "status": "GENERIC_ERROR"
}
```
*   `position`: Always `null` on error.
*   `status`: Indicates the type of error encountered:
    *   `OUT_OF_THE_BOARD`: The knight attempted to move outside the board boundaries.
    *   `INVALID_START_POSITION`: The initial `START` position was outside the board or on an obstacle.
    *   `GENERIC_ERROR`: Catch-all for other issues like invalid environment variables, API fetch failures, malformed commands, or invalid start command format.

## Implementation Choices & Notes

### General choices
I chose to make this a runnable application instead of exposing API endpoints because requirements didn't explicitly ask for it. 
I believe that with some minor changes this application can be used as an api endpoint by passing proper input as the payload.

I really would have loved to put some more logging around, but it would end up in the output and I really wanted for the output to just be the requested json.

I tried to make this work with a minimal amount of external dependencies, successfully adding only jackson for easily handling some json situations and some libraries for testing purposes, specifically junit for obvious reasons and a mockwebserver dependency to test the ApiClient which was made with java's HttpClient.
My main focus was to make a frameworkless java application as simple as it can get, and I am quite content with the result.

### Implementation choices

First thing I did was laying out the Models and DTOs needed for my application to serve a response based on input and to easily lay out the logic needed for the knight to properly move as requested.
I figured out I needed at least a **Board** record to map out the json I got from the endpoint. Initially, I wanted to directly map the commands list from the commands endpoint inside a list of strings, but implementation was getting less clear as I would have needed to use a Map<String, List<String>> to get the response out before I could return the commands list, so I ended up adding the **CommandsList** record.
I also added some utility objects such as **Coordinates** record and the **Direction** enum and the needed structure to provide a response in the terminal in json format. **KnightPosition** is the only class I coded without using record because I needed standard object behaviour in order to code the logic of the application in the simplest possible way.
I used java records because they provide constructors and getters, drastically reducing boilerplate code without the use of lombok.

Standard java HttpClient was more than enough to fetch the json from the provided endpoints, I ended up coding a very simple fetch method that does its job and also managed to only have a single method for both endpoints.

After laying out the basic structure of the project, the basic logic of the application and the ApiClient needed to get the input, I coded directly in the main method the standard flow of the application.
I deliberately chose to do this to keep the structure as simple as possible even if I could probably add some more layers to keep the code there at a minimum, but that surely had a cost in terms of project's complexity.

### Logic choices

Initially, I figured out I could just calculate the ending position of the knight for each move instruction and check the range for an obstacle then look into out of bounds problems but I ended up making a check for each move in the end:
Making a check for obstacles/out of bounds problems instead of looking into ranges on each move is really cheap in terms of computational complexity and much simpler to implement.
By using a Set to store obstacles, I could check for hits on them with O(1) complexity. Checking for bounds is really cheap too. Implementation was really simple, so I didn't even put much effort in thinking about the range check implementation.

Having a matrix and making the knight move in it was not even a choice in my mind but that could probably work. I think is the most complex and less efficient solution tho.
Most of the validation logic is in the board record and in the main class, while the movement logic is in the KnightPosition, where Direction and current Coordinates are stored.

I used the main class to initialize the needed objects, do some of the needed validations on env variables, json mapping into classes and finally command processing in order to get the **KnightPosition** object ready to be returned in a successful response (or to not be returned in case of errors).

### Testing choices

I just tried to test the standard scenarios that could happen in the movement logic and json fetching. There is not much to say.
