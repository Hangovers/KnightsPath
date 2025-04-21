package com.github.hangovers.model.dto;

import com.github.hangovers.model.Board;
import com.github.hangovers.model.Coordinates;
import com.github.hangovers.model.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class KnightPositionTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board(8, 8, Set.of(new Coordinates(2, 2)));
    }

    @ParameterizedTest
    @EnumSource(Direction.class)
    void rotate(Direction direction) {
        Coordinates startCoords = new Coordinates(0, 0);
        KnightPosition position = new KnightPosition(startCoords, Direction.NORTH);

        position.rotate(direction);
        assertEquals(direction, position.getDirection());
    }

    @Test
    void moveValidSequence() throws IOException {
        Coordinates startCoords = new Coordinates(1, 1);
        KnightPosition position = new KnightPosition(startCoords, Direction.NORTH);

        position.move(1, board);
        assertEquals(new Coordinates(1, 2), position.getCoordinates());

        position.rotate(Direction.EAST);

        position.move(2, board);
        assertEquals(new Coordinates(1, 2), position.getCoordinates());

        position = new KnightPosition(new Coordinates(1, 1), Direction.NORTH);

        position.move(1, board);
        assertEquals(new Coordinates(1, 2), position.getCoordinates());

        position.rotate(Direction.EAST);
        assertEquals(Direction.EAST, position.getDirection());

        position.move(2, board);
        assertEquals(new Coordinates(1, 2), position.getCoordinates());

        position = new KnightPosition(new Coordinates(0, 0), Direction.EAST);

        position.move(1, board);
        assertEquals(new Coordinates(1, 0), position.getCoordinates());

        position.rotate(Direction.NORTH);
        assertEquals(Direction.NORTH, position.getDirection());

        position.move(3, board);
        assertEquals(new Coordinates(1, 3), position.getCoordinates());

        position.rotate(Direction.WEST);
        assertEquals(Direction.WEST, position.getDirection());

        position.move(1, board);
        assertEquals(new Coordinates(0, 3), position.getCoordinates());
    }


    @Test
    void moveOutOfBounds() {
        Coordinates startCoords = new Coordinates(0, 7);
        KnightPosition position = new KnightPosition(startCoords, Direction.NORTH);
        assertThrows(IOException.class, () -> position.move(1, board), "Out of bounds");
        assertEquals(startCoords, position.getCoordinates());
    }

    @Test
    void moveToObstacle() throws IOException {
        Coordinates startCoords = new Coordinates(1, 2);
        KnightPosition position = new KnightPosition(startCoords, Direction.EAST);

        position.move(1, board);

        assertEquals(startCoords, position.getCoordinates(), "Knight should not move onto an obstacle");
    }

     @Test
    void moveTowardsAndStopAtObstacle() throws IOException {
        Coordinates startCoords = new Coordinates(0, 2);
        KnightPosition position = new KnightPosition(startCoords, Direction.EAST);

        position.move(3, board);

        assertEquals(new Coordinates(1, 2), position.getCoordinates(), "Knight should stop just before the obstacle");
    }


    @Test
    void moveZeroSteps() throws IOException {
        Coordinates startCoords = new Coordinates(3, 3);
        KnightPosition position = new KnightPosition(startCoords, Direction.NORTH);
        position.move(0, board);
        assertEquals(startCoords, position.getCoordinates(), "Moving zero steps should not change position");
    }

    @Test
    void startPositionOutOfBounds() {
        Coordinates outCoordsNegative = new Coordinates(-1, 0);
        Coordinates outCoordsPositive = new Coordinates(8, 8);

        assertTrue(board.isOutOfBounds(outCoordsNegative), "Position (-1, 0) should be out of bounds");
        assertTrue(board.isOutOfBounds(outCoordsPositive), "Position (8, 8) should be out of bounds");
    }

    @Test
    void startPositionOnObstacle() {
        Coordinates obstacleCoords = new Coordinates(2, 2);

        assertTrue(board.checkCollision(obstacleCoords), "Position (2, 2) should collide with an obstacle");
    }
} 