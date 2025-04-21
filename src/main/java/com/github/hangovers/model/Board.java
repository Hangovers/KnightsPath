package com.github.hangovers.model;

import java.util.Set;

/**
 * @param width board width fetched from board api endpoint
 * @param height board height fetched from board api endpoint
 * @param obstacles obstacles in the board from board api endpoint
 */
public record Board(Integer width, Integer height, Set<Coordinates> obstacles) {

    /**
     * @param coords coordinates of knight's current position
     * @return true if the current coordinates are out of bounds based on Board size, false otherwise
     */
    public boolean isOutOfBounds(Coordinates coords) {
        return coords.x() < 0 ||
                coords.y() < 0 ||
                coords.x() > width() - 1 ||
                coords.y() > height() - 1;
    }

    /**
     *
     * @param coords coords coordinates of knight's current position
     * @return true if coords are in the position of an obstacle, false otherwise
     */
    public boolean checkCollision(Coordinates coords) {
        return obstacles.contains(coords);
    }
}
