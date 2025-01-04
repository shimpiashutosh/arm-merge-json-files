package com.arm.cli.mergejsonfiles.service;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Interface for merging files from a source directory into a single output file.
 * <p>
 * Implementations of this interface should provide logic to combine multiple files
 * from the source directory into one output file.
 * </p>
 */
public interface IMergeFilesService {
    /**
     * Merges files from the specified source directory into a single output file.
     *
     * <p>
     * The method processes all files in the provided source directory and combines
     * them into a single output file located at the specified output path.
     * </p>
     *
     * @param sourceDirPath the path to the directory containing the source files
     *         to be merged. Must not be {@code null}.
     * @param outputFilePath the path to the output file where the merged content
     *         will be written. Must not be {@code null}.
     *
     * @throws IOException if an I/O error occurs during the file reading or writing process.
     */
    void merge(Path sourceDirPath, Path outputFilePath) throws IOException;
}
