package com.arm.cli.mergejsonfiles.cli;

import org.springframework.boot.ApplicationArguments;

import java.nio.file.Path;
import java.util.UUID;

import static com.arm.cli.mergejsonfiles.exception.ClientException.argumentMissing;

/**
 * Options parser class. Handles input parameters/arguments.
 */
public class OptionParser {
    private final Path sourcePath;
    private final Path outputFilePath;
    private final static String SOURCE_PATH_ARG = "source-path";
    private final static String OUTPUT_FILE_PATH_ARG = "output-file-path";

    public OptionParser(final ApplicationArguments args) {
        this.sourcePath = extractSourcePath(args);
        this.outputFilePath = extractOutputPath(args);
    }

    /**
     * @param args {@link ApplicationArguments} program arguments.
     *
     * @return source path to merge files.
     */
    private Path extractSourcePath(final ApplicationArguments args) {
        if (!args.containsOption(SOURCE_PATH_ARG) || args.getOptionValues(SOURCE_PATH_ARG).isEmpty()) {
            throw argumentMissing(SOURCE_PATH_ARG);
        }
        return Path.of(args.getOptionValues(SOURCE_PATH_ARG).get(0));
    }

    /**
     * @param args {@link ApplicationArguments} program arguments.
     *
     * @return output file path to save combined records.
     */
    private Path extractOutputPath(final ApplicationArguments args) {
        if (args.containsOption(OUTPUT_FILE_PATH_ARG)) {
            if (args.getOptionValues(OUTPUT_FILE_PATH_ARG).isEmpty()) {
                throw argumentMissing(OUTPUT_FILE_PATH_ARG);
            }
            return Path.of(args.getOptionValues(OUTPUT_FILE_PATH_ARG).get(0));
        }
        final String randomCombinedFileName = "combined-json-file-" + UUID.randomUUID() + ".json";
        return Path.of(args.getOptionValues(SOURCE_PATH_ARG).get(0)).resolve(randomCombinedFileName);
    }

    public Path getSourceDirPath() {
        return sourcePath.toAbsolutePath();
    }

    public Path getOutputFilePath() {
        return outputFilePath.toAbsolutePath();
    }
}
