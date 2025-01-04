package com.arm.cli.mergejsonfiles.constants;

/**
 * Application exit status enum.
 */
public enum ApplicationStatus {
    SUCCESS(0),
    APPLICATION_FAILED(1);

    private final int value;

    ApplicationStatus(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
