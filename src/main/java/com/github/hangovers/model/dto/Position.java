package com.github.hangovers.model.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.github.hangovers.model.Coordinates;
import com.github.hangovers.model.Direction;

public class Position {

    @JsonUnwrapped
    private Coordinates coordinates;
    private Direction direction;

    public Position(Coordinates coords,
                    Direction direction) {

        this.coordinates = coords;
        this.direction = direction;
    }

    public void rotate(Direction newDirection) {
        this.direction = newDirection;
    }

    public void move(Coordinates coords) {
        this.coordinates = coords;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }
}
