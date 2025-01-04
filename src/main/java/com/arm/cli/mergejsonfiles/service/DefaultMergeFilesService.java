package com.arm.cli.mergejsonfiles.service;

import com.arm.cli.mergejsonfiles.model.BoardData;
import com.arm.cli.mergejsonfiles.model.BoardDataSlice;
import com.arm.cli.mergejsonfiles.model.BoardWrapperData;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Stream;

import static com.fasterxml.jackson.core.JsonEncoding.UTF8;
import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
import static java.nio.file.Files.walk;

/**
 * Default merge file service.
 */
public class DefaultMergeFilesService implements IMergeFilesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMergeFilesService.class);
    private final ObjectMapper objectMapper;

    public DefaultMergeFilesService() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void merge(final Path sourceDirPath,
                      final Path outputFilePath) throws IOException {
        final JsonFactory jsonFactory = new JsonFactory();
        final TreeMap<String, TreeMap<String, BoardDataSlice>> parsedJsonData = new TreeMap<>();
        listFiles(sourceDirPath)
                .filter(Files::isRegularFile)
                .sorted(Comparator.comparing(Path::getFileName))
                .filter(path -> path.toString().endsWith(".json"))
                .map(path -> parseJsonFile(jsonFactory, path.toFile()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(boardWrapperRecord -> buildTreeMapData(boardWrapperRecord, parsedJsonData));
        writeCombinedJsonFileData(parsedJsonData, outputFilePath);
    }

    /**
     * Returns stream of regular file paths, performs recursive search.
     *
     * @param sourceDirPath the path to the directory containing the source files
     *         to be merged. Must not be {@code null}.
     *
     * @return {@link Stream} of paths.
     * @throws IOException if an I/O error occurs during listing files.
     */
    protected Stream<Path> listFiles(final Path sourceDirPath) throws IOException {
        return walk(sourceDirPath, FOLLOW_LINKS);
    }

    /**
     * Parse the JSON file, builds {@link BoardWrapperData} object.
     *
     * @param jsonFactory {@link JsonFactory} instance to process JSON file.
     * @param jsonFile {@link File} instance of JSON file to process.
     *
     * @return Optional {@link BoardWrapperData}, returns empty if there is an error while processing file.
     */
    protected Optional<BoardWrapperData> parseJsonFile(final JsonFactory jsonFactory,
                                                       final File jsonFile) {
        final List<BoardData> boardDataList = new ArrayList<>();
        try (final JsonParser parser = jsonFactory.createParser(jsonFile)) {
            boolean boardsFound = false;
            // Start parsing the file
            while (!parser.isClosed()) {
                final JsonToken token = parser.nextToken();

                // Check for the start of the array
                if (JsonToken.FIELD_NAME.equals(token) && "boards".equals(parser.currentName())) {
                    boardsFound = true;
                    parser.nextToken(); // Move to START_ARRAY

                    while (parser.nextToken() != JsonToken.END_ARRAY) {
                        // Deserialize each object in the array
                        try {
                            final BoardData boardData = objectMapper.readValue(parser, BoardData.class);
                            validateBoardData(boardData);
                            boardDataList.add(boardData);
                        } catch (NullPointerException nullPointerException) {
                            LOGGER.error("Json field {} is null", nullPointerException.getMessage());
                        } catch (IOException exception) {
                            LOGGER.error("Error reading json object: {}", exception.getMessage());
                        }
                    }
                }
            }
            if (!boardsFound) {
                LOGGER.error("Error: Required field 'boards' is missing or misspelled in file: {}. Skipping the file.", jsonFile.getName());
                return Optional.empty();
            }
            return Optional.of(new BoardWrapperData(boardDataList));
        } catch (IOException ioException) {
            return Optional.empty();
        }
    }

    /**
     * Validates {@link BoardData} board data retrieved from JSON file.
     *
     * @param boardData same {@link BoardData} instance provided as an argument.
     */
    protected void validateBoardData(final BoardData boardData) {
        Objects.requireNonNull(boardData.getName(), "\"name\"");
        Objects.requireNonNull(boardData.getName(), "\"vendor\"");
        Objects.requireNonNull(boardData.getName(), "\"core\"");
        Objects.requireNonNull(boardData.getName(), "\"has_wifi\"");
    }

    /**
     * Builds sorted map from parsed JSON file stored as models.
     *
     * @param boardWrapperData {@link BoardWrapperData} wrapper class.
     * @param parsedJsonData {@link TreeMap} to store records in sorted order. e.g. vendor and name.
     */
    protected void buildTreeMapData(final BoardWrapperData boardWrapperData,
                                    final TreeMap<String, TreeMap<String, BoardDataSlice>> parsedJsonData) {
        boardWrapperData
                .boards()
                .forEach(boardData ->
                        parsedJsonData.compute(boardData.getVendor(), (vendorAsKey, boardNameMap) -> {
                            if (boardNameMap == null) {
                                boardNameMap = new TreeMap<>();
                            }
                            boardNameMap.computeIfAbsent(boardData.getName(), (nameAsKey) -> new BoardDataSlice(boardData.getCore(), boardData.isHasWifi()));
                            return boardNameMap;
                        }));
    }

    /**
     * Writes combined data to a single JSON file.
     *
     * @param parsedJsonData {@link TreeMap} stores records in a sorted order. e.g. vendor and name.
     * @param outputFilePath combined JSON output file.
     */
    protected void writeCombinedJsonFileData(final TreeMap<String, TreeMap<String, BoardDataSlice>> parsedJsonData,
                                             final Path outputFilePath) {
        LOGGER.info("Output file is being generated - {}", outputFilePath);
        try (final JsonGenerator jsonGenerator = new JsonFactory()
                .createGenerator(outputFilePath.toFile(), UTF8)) {
            jsonGenerator.useDefaultPrettyPrinter();
            jsonGenerator.writeStartObject(); // Start of the root object
            jsonGenerator.writeFieldName("boards"); // Field name for the array
            jsonGenerator.writeStartArray(); // Start of the array

            int noOfVendors = 0;
            int noOfBoards = 0;

            for (final Map.Entry<String, TreeMap<String, BoardDataSlice>> mapEntry : parsedJsonData.entrySet()) {
                noOfVendors++;
                for (final Map.Entry<String, BoardDataSlice> boardDataSliceMapEntry : mapEntry.getValue().entrySet()) {
                    try {
                        writeBoardData(jsonGenerator, boardDataSliceMapEntry.getKey(), mapEntry.getKey(), boardDataSliceMapEntry.getValue());
                        noOfBoards++;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            jsonGenerator.writeEndArray(); // End of the array
            writeMetaData(jsonGenerator, noOfVendors, noOfBoards);
            jsonGenerator.writeEndObject(); // End of the root object
            LOGGER.info("Output file has been generated - {}", outputFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes individual board data JSON structure.
     *
     * @param jsonGenerator {@link JsonGenerator} instance to write JSON data.
     * @param boardName board name.
     * @param vendorName vendor name.
     * @param boardDataSlice {@link BoardDataSlice} instance, keeps partial info.
     *
     * @throws IOException if an I/O error occurs during writing process.
     */
    protected void writeBoardData(final JsonGenerator jsonGenerator,
                                  final String boardName,
                                  final String vendorName,
                                  final BoardDataSlice boardDataSlice) throws IOException {
        jsonGenerator.writeStartObject(); // Start of an individual record
        jsonGenerator.writeStringField("name", boardName);
        jsonGenerator.writeStringField("vendor", vendorName);
        jsonGenerator.writeStringField("core", boardDataSlice.getCore());
        jsonGenerator.writeBooleanField("has_wifi", boardDataSlice.isHasWifi());
        jsonGenerator.writeEndObject(); // End of an individual record
    }

    /**
     * Writes metadata.
     *
     * @param jsonGenerator {@link JsonGenerator} instance to write JSON data.
     * @param totalVendors no. of total vendors.
     * @param totalBoards no. of total boards.
     *
     * @throws IOException if an I/O error occurs during writing process.
     */
    protected void writeMetaData(final JsonGenerator jsonGenerator,
                                 final int totalVendors,
                                 final int totalBoards) throws IOException {
        jsonGenerator.writeFieldName("_metadata");
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("total_vendors", totalVendors);
        jsonGenerator.writeNumberField("total_boards", totalBoards);
        jsonGenerator.writeEndObject(); // End of an individual record
    }
}
