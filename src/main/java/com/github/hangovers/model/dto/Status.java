package com.github.hangovers.model.dto;

public enum Status {
    SUCCESS("SUCCESS"),
    INVALID_START_POSITION("INVALID START POSITION"),
    OUT_OF_THE_BOARD("OUT OF THE BOARD"),
    GENERIC_ERROR("GENERIC ERROR");

    Status(String status) {};
}
