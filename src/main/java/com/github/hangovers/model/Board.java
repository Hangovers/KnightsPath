package com.github.hangovers.model;

import java.util.Set;

public record Board(Integer width, Integer height, Set<Coordinates> obstacles) {

    public boolean isWithinBounds(Coordinates coords) {
        return coords.x() >= 0 &&
                coords.y() >= 0  &&
                coords.x() <= width() - 1 &&
                coords.y() <= height() -1;
    }

    public boolean checkCollision(Coordinates coords) {
        return obstacles.contains(coords);
    }
}
