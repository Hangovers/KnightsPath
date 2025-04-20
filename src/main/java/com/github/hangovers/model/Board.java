package com.github.hangovers.model;

import java.util.Set;

public record Board(Integer width, Integer height, Set<Coordinates> obstacles) {
}
