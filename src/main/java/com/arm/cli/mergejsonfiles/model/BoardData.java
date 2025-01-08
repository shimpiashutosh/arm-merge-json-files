package com.arm.cli.mergejsonfiles.model;

/**
 * Board data model.
 */
public class BoardData extends BoardDataSlice {
    private String name;
    private String vendor;

    private BoardData() {
        super();
    }

    public String getName() {
        return name;
    }

    public String getVendor() {
        return vendor;
    }
}
