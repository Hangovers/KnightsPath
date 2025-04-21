package com.github.hangovers.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @param position knight's final position if command list was executed successfully
 * @param status response status. Out of bounds if the knight ended up exiting from the board, invalid start position if the knight's start position is
 *               out of bounds or on an obstacle, success if the command list was executed without issues. generic error for all other scenarios (fetching failed, mapping failed...)
 */
public record Response(@JsonInclude(JsonInclude.Include.NON_NULL) KnightPosition position,
                       Status status) { }
