/*
 *
 * Copyright 2025 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.arm.cli.mergejsonfiles.cli;

import com.arm.cli.mergejsonfiles.config.JsonFilesMergeConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.Files.copy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {JsonFilesMergeConfig.class})
@ExtendWith(SpringExtension.class)
public class CliExecutorTest {
    private static final String BOARDS_1_JSON = "boards-1.json";
    private static final String BOARDS_2_JSON = "boards-2.json";
    private static final String JSON_MISMATCHED_MESSAGE = "The merged JSON does not match the expected output";

    @Autowired
    private CliExecutor cliExecutor;

    @DisplayName("When valid arguments passed generates combined json file as per requirements")
    @Test
    void executeTest_GeneratesCombinedFile(@TempDir Path tmpDir) throws IOException {
        final String TEST_SUITE_1 = "classpath:test-suite-1-example-files";
        final String expectedResultFile = "combined-board-file-expected-result.json";
        final File folder = ResourceUtils.getFile(TEST_SUITE_1);

        final Path boards_1_test_suite_2 = tmpDir.resolve(BOARDS_1_JSON);
        final Path boards_2_test_suite_2 = tmpDir.resolve(BOARDS_2_JSON);
        final Path boards_combined_file = tmpDir.resolve(expectedResultFile);

        // Copy json files from test resources directory to tmp directory
        copy(folder.toPath().resolve(BOARDS_1_JSON), boards_1_test_suite_2);
        copy(folder.toPath().resolve(BOARDS_2_JSON), tmpDir.resolve(boards_2_test_suite_2));
        copy(folder.toPath().resolve(expectedResultFile), tmpDir.resolve(boards_combined_file));

        final String SOURCE_PATH_ARG = "source-path";
        final String OUTPUT_FILE_PATH_ARG = "output-file-path";

        final ApplicationArguments args = mock(ApplicationArguments.class);

        // Define behavior for mocked methods
        when(args.containsOption(eq(SOURCE_PATH_ARG))).thenReturn(true);
        when(args.getOptionValues(eq(SOURCE_PATH_ARG))).thenReturn(List.of(tmpDir.toString()));
        when(args.containsOption(eq(OUTPUT_FILE_PATH_ARG))).thenReturn(false);

        // Method to test
        cliExecutor.execute(new OptionParser(args));

        final Optional<Path> outputPath = getOutputFile(tmpDir);
        assertFalse(outputPath.isEmpty(), "Output file not found!");

        final Path outputFile = outputPath.get();
        // Read the files using ObjectMapper
        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode mergedJson = objectMapper.readTree(outputFile.toFile());
        final JsonNode expectedJson = objectMapper.readTree(boards_combined_file.toFile());

        // Compare the JSON contents
        assertEquals(expectedJson, mergedJson, JSON_MISMATCHED_MESSAGE);
    }

    private Optional<Path> getOutputFile(final Path sourceDir) throws IOException {
        try (final Stream<Path> files = Files.walk(sourceDir)) {
            return files.filter(Files::isRegularFile) // Only regular files
                    .filter(path -> path.getFileName().toString().startsWith("combined-json-file-")) // Match prefix
                    .collect(Collectors.toSet())
                    .stream()
                    .findFirst();
        }
    }
}
