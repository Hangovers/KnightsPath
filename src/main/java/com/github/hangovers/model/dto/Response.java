package com.github.hangovers.model.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

public record Response(@JsonInclude(JsonInclude.Include.NON_NULL) Position position,
                       Status status) { }
