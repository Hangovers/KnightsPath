package com.github.hangovers.model;

import java.util.List;

/**
 * @param commands list of commands fetched from the commands API
 */
public record CommandsList(List<String> commands) {
}
