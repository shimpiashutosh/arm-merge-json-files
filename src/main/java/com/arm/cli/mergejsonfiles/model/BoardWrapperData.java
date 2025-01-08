package com.arm.cli.mergejsonfiles.model;

import java.util.List;

/**
 * Board data wrapper class.
 *
 * @param boards list of {@link BoardData}.
 */
public record BoardWrapperData(List<BoardData> boards) {
}
