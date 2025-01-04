package com.arm.cli.mergejsonfiles.exception;

/**
 * Exception class represents exceptions occurred due to client error.
 */
public class ClientException extends RuntimeException {
    public ClientException(final String message) {
        super(message);
    }

    public static ClientException invalidFolderPath(final String folderPath) {
        return new ClientException("Invalid folder path received: %s".formatted(folderPath));
    }

    public static ClientException writeProtectedFolderPath(final String folderPath) {
        return new ClientException("Destination folder path is write protected: %s. Output file will not be generated!".formatted(folderPath));
    }

    public static ClientException argumentMissing(final String argumentName) {
        return new ClientException("Command line argument: --%s is missing OR doesn't have valid value!".formatted(argumentName));
    }
}
