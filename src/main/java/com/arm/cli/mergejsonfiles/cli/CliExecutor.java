package com.arm.cli.mergejsonfiles.cli;

import com.arm.cli.mergejsonfiles.constants.ApplicationStatus;
import com.arm.cli.mergejsonfiles.exception.ClientException;
import com.arm.cli.mergejsonfiles.service.IMergeFilesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

import static com.arm.cli.mergejsonfiles.constants.ApplicationStatus.APPLICATION_FAILED;
import static com.arm.cli.mergejsonfiles.constants.ApplicationStatus.SUCCESS;
import static com.arm.cli.mergejsonfiles.exception.ClientException.invalidFolderPath;
import static com.arm.cli.mergejsonfiles.exception.ClientException.writeProtectedFolderPath;
import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isWritable;

/**
 * CLI executor class to streamline merging process, applies diff. file validations.
 */
public class CliExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CliExecutor.class);
    private final IMergeFilesService mergeFilesService;

    public CliExecutor(final IMergeFilesService mergeFilesService) {
        this.mergeFilesService = mergeFilesService;
    }

    /**
     * Starts merging process
     *
     * @param optionParser {@link OptionParser} process & validate input & output.
     *
     * @return {@link ApplicationStatus} status of the application success/failure.
     */
    public ApplicationStatus execute(final OptionParser optionParser) {
        LOGGER.info("------------ PROCESS IS BEING STARTED ------------");
        try {
            LOGGER.info("File merging process started for folder {}", optionParser.getSourceDirPath().toString());
            validateFolderPath(optionParser.getSourceDirPath());
            checkWritePermissionForDestinationFolder(optionParser.getOutputFilePath());
            deleteOutputFileIfExists(optionParser.getOutputFilePath());
            mergeFilesService.merge(optionParser.getSourceDirPath(), optionParser.getOutputFilePath());
            LOGGER.info("File merging process completed for folder {}", optionParser.getSourceDirPath());
        } catch (Exception e) {
            LOGGER.error("Error while merging json files: %s".formatted(e.getMessage()), e);
            return APPLICATION_FAILED;
        }
        LOGGER.info("------------ PROCESS COMPLETED ------------");
        return SUCCESS;
    }

    /**
     * Validates folder path.
     *
     * @param sourceDirPath source dir path.
     *
     * @throws ClientException in case source dir path is not a directory or path doesn't exist.
     */
    protected void validateFolderPath(final Path sourceDirPath) throws ClientException {
        if (!isDirectory(sourceDirPath)) {
            throw invalidFolderPath(sourceDirPath.toString());
        }
    }

    /**
     * Check whether output file can be written, check for writing permission.
     *
     * @param outputFilePath output file path.
     *
     * @throws ClientException in case output file can't be written or path doesn't exist.
     */
    protected void checkWritePermissionForDestinationFolder(final Path outputFilePath) throws ClientException {
        if (!isWritable(outputFilePath.getParent())) {
            throw writeProtectedFolderPath(outputFilePath.toString());
        }
    }

    /**
     * Deletes previously generated output file if any.
     *
     * @param outputFilePath output file path.
     *
     * @throws IOException in case error while deleting file.
     */
    protected void deleteOutputFileIfExists(final Path outputFilePath) throws IOException {
        if (!outputFilePath.toFile().isDirectory() && deleteIfExists(outputFilePath)) {
            LOGGER.warn("Existing output file has been deleted! {}", outputFilePath);
        }
    }
}
