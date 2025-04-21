package com.github.hangovers.model.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.github.hangovers.model.Board;
import com.github.hangovers.model.Coordinates;
import com.github.hangovers.model.Direction;

import java.io.IOException;

public class KnightPosition {

    @JsonUnwrapped
    private Coordinates coordinates;
    private Direction direction;

    public KnightPosition(Coordinates coords,
                          Direction direction) {

        this.coordinates = coords;
        this.direction = direction;
    }

    public void rotate(Direction newDirection) {
        this.direction = newDirection;
    }

    public void move(int numberOfSteps, Board board) throws IOException {
        if(numberOfSteps > 0) {
            Coordinates newCoordinates = null;
            switch(direction) {
                case EAST -> newCoordinates = new Coordinates(coordinates.x() + 1, coordinates.y());
                case WEST -> newCoordinates = new Coordinates(coordinates.x() - 1, coordinates.y());
                case NORTH ->  newCoordinates = new Coordinates(coordinates.x(), coordinates.y() + 1);
                case SOUTH ->  newCoordinates = new Coordinates(coordinates.x(), coordinates.y() - 1);
            }

            if(board.isOutOfBounds(newCoordinates)) throw new IOException("Out of bounds");
            if(board.checkCollision(newCoordinates)) {
                numberOfSteps = 0;
            } else {
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
