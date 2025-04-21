package com.github.hangovers.model.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.github.hangovers.model.Board;
import com.github.hangovers.model.Coordinates;
import com.github.hangovers.model.Direction;

import java.io.IOException;

/**
 *  Stores Knight's position and direction variations during command executions.
 */
public class KnightPosition {

    @JsonUnwrapped
    private Coordinates coordinates;
    private Direction direction;

    public KnightPosition(Coordinates coords,
                          Direction direction) {

        this.coordinates = coords;
        this.direction = direction;
    }

    /**
     * Changes the direction the knight's facing
     * @param newDirection new direction the knight will face until next direction change.
     */
    public void rotate(Direction newDirection) {
        this.direction = newDirection;
    }

    /**
     * recursive method used to update knight's position based on the current MOVE command
     * @param numberOfSteps number of steps (and method calls) still to be done from the knight before ending
     * @param board Board data to check for out of bounds errors and obstacles.
     * @throws IOException gets thrown if the knight goes out of bounds.
     */
    public void move(int numberOfSteps, Board board) throws IOException {
        if(numberOfSteps > 0) {
            Coordinates newCoordinates = null;

            // coordinates update is different and based on direction knight's facing
            switch(direction) {
                case EAST -> newCoordinates = new Coordinates(coordinates.x() + 1, coordinates.y());
                case WEST -> newCoordinates = new Coordinates(coordinates.x() - 1, coordinates.y());
                case NORTH ->  newCoordinates = new Coordinates(coordinates.x(), coordinates.y() + 1);
                case SOUTH ->  newCoordinates = new Coordinates(coordinates.x(), coordinates.y() - 1);
            }

            if(board.isOutOfBounds(newCoordinates)) throw new IOException("Out of bounds");

            // if knight does not hit an obstacle, coodinates update happens and move gets called again until steps reach 0.
            if(!board.checkCollision(newCoordinates)) {
                this.coordinates = newCoordinates;
                move(numberOfSteps - 1, board);
            }
        }
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Direction getDirection() {
        return direction;
    }
}
