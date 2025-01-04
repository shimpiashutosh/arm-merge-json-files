package com.arm.cli.mergejsonfiles.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.Files.copy;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultMergeFilesServiceTest {
    private static final String BOARDS_1_JSON = "boards-1.json";
    private static final String BOARDS_2_JSON = "boards-2.json";
    private static final String COMBINED_RESULT_JSON_FILE = "combined-json-file.json";
    private static final String JSON_MISMATCHED_MESSAGE = "The merged JSON does not match the expected output";

    @DisplayName("When valid path to json files provided then generates combined json file as per requirements")
    @Test
    public void defaultMergeFilesServiceTest_GeneratesCombinedFile(@TempDir Path tmpDir) throws IOException {
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

        final Path combinedJsonResultFile = tmpDir.resolve(COMBINED_RESULT_JSON_FILE);
        final DefaultMergeFilesService defaultMergeFilesService = new DefaultMergeFilesService();

        //Method to test
        defaultMergeFilesService.merge(tmpDir, combinedJsonResultFile);

        // Read the files using ObjectMapper
        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode mergedJson = objectMapper.readTree(combinedJsonResultFile.toFile());
        final JsonNode expectedJson = objectMapper.readTree(boards_combined_file.toFile());

        // Compare the JSON contents
        assertEquals(expectedJson, mergedJson, JSON_MISMATCHED_MESSAGE);
    }

    @DisplayName("When valid path to json files provided where some files have wrongly spelled field names then generates partial combined json file as per requirements")
    @Test
    public void defaultMergeFilesServiceTest_GeneratesPartialCombinedFile_IgnoresInvalidData(@TempDir Path tmpDir) throws IOException {
        final String TEST_SUITE_2 = "classpath:test-suite-2-partially-misspelled-fields";
        final String expectedResultFile = "partial-combined-board-file-expected-result.json";
        final File folder = ResourceUtils.getFile(TEST_SUITE_2);
        final Path boards_1_test_suite_2 = tmpDir.resolve(BOARDS_1_JSON);
        final Path boards_2_test_suite_2 = tmpDir.resolve(BOARDS_2_JSON);
        final Path boards_combined_file = tmpDir.resolve(expectedResultFile);

        // Copy json files from test resources directory to tmp directory
        copy(folder.toPath().resolve(BOARDS_1_JSON), boards_1_test_suite_2);
        copy(folder.toPath().resolve(BOARDS_2_JSON), tmpDir.resolve(boards_2_test_suite_2));
        copy(folder.toPath().resolve(expectedResultFile), tmpDir.resolve(boards_combined_file));

        final Path combinedJsonResultFile = tmpDir.resolve(COMBINED_RESULT_JSON_FILE);
        final DefaultMergeFilesService defaultMergeFilesService = new DefaultMergeFilesService();

        //Method to test
        defaultMergeFilesService.merge(tmpDir, combinedJsonResultFile);

        // Read the files using ObjectMapper
        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode mergedJson = objectMapper.readTree(combinedJsonResultFile.toFile());
        final JsonNode expectedJson = objectMapper.readTree(boards_combined_file.toFile());

        // Compare the JSON contents
        assertEquals(expectedJson, mergedJson, JSON_MISMATCHED_MESSAGE);
    }

    @DisplayName("When valid path to json files provided where all files have wrongly spelled field names then generates empty combined json file as per requirements")
    @Test
    public void defaultMergeFilesServiceTest_GeneratesEmptyCombinedFile_IgnoresInvalidData(@TempDir Path tmpDir) throws IOException {
        final String TEST_SUITE_3 = "classpath:test-suite-3-all-fields-misspelled-empty-result";
        final String expectedResultFile = "empty-combined-board-file-expected-result.json";
        final String recursiveDir = "recursive-dir-test";
        final File folder = ResourceUtils.getFile(TEST_SUITE_3);
        final Path boards_1_test_suite_2 = tmpDir.resolve(recursiveDir).resolve(BOARDS_1_JSON);
        final Path boards_2_test_suite_2 = tmpDir.resolve(BOARDS_2_JSON);
        final Path boards_combined_file = tmpDir.resolve(expectedResultFile);

        // Copy json files from test resources directory to tmp directory
        Files.createDirectory(tmpDir.resolve(recursiveDir));
        copy(folder.toPath().resolve(recursiveDir).resolve(BOARDS_1_JSON), boards_1_test_suite_2);
        copy(folder.toPath().resolve(BOARDS_2_JSON), tmpDir.resolve(boards_2_test_suite_2));
        copy(folder.toPath().resolve(expectedResultFile), tmpDir.resolve(boards_combined_file));

        final Path combinedJsonResultFile = tmpDir.resolve(COMBINED_RESULT_JSON_FILE);
        final DefaultMergeFilesService defaultMergeFilesService = new DefaultMergeFilesService();

        //Method to test
        defaultMergeFilesService.merge(tmpDir, combinedJsonResultFile);

        // Read the files using ObjectMapper
        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode mergedJson = objectMapper.readTree(combinedJsonResultFile.toFile());
        final JsonNode expectedJson = objectMapper.readTree(boards_combined_file.toFile());

        // Compare the JSON contents
        assertEquals(expectedJson, mergedJson, JSON_MISMATCHED_MESSAGE);
    }

    @DisplayName("When valid path to large json files provided then generates combined json file as per requirements")
    @Test
    public void defaultMergeFilesServiceTest_GeneratesLargeCombinedFile(@TempDir Path tmpDir) throws IOException {
        final String TEST_SUITE_3 = "classpath:test-suite-4-large-records";
        final String expectedResultFile = "combined-board-file-expected-result.json";
        final File folder = ResourceUtils.getFile(TEST_SUITE_3);
        final Path boards_1_test_suite_2 = tmpDir.resolve(BOARDS_1_JSON);
        final Path boards_2_test_suite_2 = tmpDir.resolve(BOARDS_2_JSON);
        final Path boards_combined_file = tmpDir.resolve(expectedResultFile);

        // Copy json files from test resources directory to tmp directory
        copy(folder.toPath().resolve(BOARDS_1_JSON), boards_1_test_suite_2);
        copy(folder.toPath().resolve(BOARDS_2_JSON), tmpDir.resolve(boards_2_test_suite_2));
        copy(folder.toPath().resolve(expectedResultFile), tmpDir.resolve(boards_combined_file));

        final Path combinedJsonResultFile = tmpDir.resolve(COMBINED_RESULT_JSON_FILE);
        final DefaultMergeFilesService defaultMergeFilesService = new DefaultMergeFilesService();

        //Method to test
        defaultMergeFilesService.merge(tmpDir, combinedJsonResultFile);

        // Read the files using ObjectMapper
        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode mergedJson = objectMapper.readTree(combinedJsonResultFile.toFile());
        final JsonNode expectedJson = objectMapper.readTree(boards_combined_file.toFile());

        // Compare the JSON contents
        assertEquals(expectedJson, mergedJson, JSON_MISMATCHED_MESSAGE);
    }

    @DisplayName("When valid path to json files provided then recursively search for json files & then generates combined json file as per requirements")
    @Test
    public void defaultMergeFilesServiceTest_GeneratesCombinedFile_SearchRecursively(@TempDir Path tmpDir) throws IOException {
        final String TEST_SUITE_5 = "classpath:test-suite-5-example-files-recursive-search";
        final String expectedResultFile = "combined-board-file-expected-result.json";
        final String recursiveDir = "recursive-child-dir";
        final File folder = ResourceUtils.getFile(TEST_SUITE_5);
        final Path boards_1_test_suite_2 = tmpDir.resolve(recursiveDir).resolve(BOARDS_1_JSON);
        final Path boards_2_test_suite_2 = tmpDir.resolve(BOARDS_2_JSON);
        final Path boards_combined_file = tmpDir.resolve(expectedResultFile);

        // Copy json files from test resources directory to tmp directory
        Files.createDirectory(tmpDir.resolve(recursiveDir));
        copy(folder.toPath().resolve(recursiveDir).resolve(BOARDS_1_JSON), boards_1_test_suite_2);
        copy(folder.toPath().resolve(BOARDS_2_JSON), tmpDir.resolve(boards_2_test_suite_2));
        copy(folder.toPath().resolve(expectedResultFile), tmpDir.resolve(boards_combined_file));

        final Path combinedJsonResultFile = tmpDir.resolve(COMBINED_RESULT_JSON_FILE);
        final DefaultMergeFilesService defaultMergeFilesService = new DefaultMergeFilesService();

        //Method to test
        defaultMergeFilesService.merge(tmpDir, combinedJsonResultFile);

        // Read the files using ObjectMapper
        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode mergedJson = objectMapper.readTree(combinedJsonResultFile.toFile());
        final JsonNode expectedJson = objectMapper.readTree(boards_combined_file.toFile());

        // Compare the JSON contents
        assertEquals(expectedJson, mergedJson, JSON_MISMATCHED_MESSAGE);
    }
}
