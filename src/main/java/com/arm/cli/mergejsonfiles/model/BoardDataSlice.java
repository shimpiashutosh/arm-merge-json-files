package com.arm.cli.mergejsonfiles.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Partial Board data model.
 */
public class BoardDataSlice {
    private String core;

    @JsonProperty("has_wifi")
    private Boolean hasWifi;

    protected BoardDataSlice() {
    }

    public BoardDataSlice(final String core,
                          final boolean hasWifi) {
        this.core = core;
        this.hasWifi = hasWifi;
    }

    public String getCore() {
        return core;
    }

    public Boolean isHasWifi() {
        return hasWifi;
    }
}
