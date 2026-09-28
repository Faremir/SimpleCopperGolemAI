package io.github.faremir.smartergolems.config;

import com.google.gson.*;

import net.fabricmc.loader.api.FabricLoader;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.*;

public final class SmarterGolemsConfigManager {

    private static final Logger LOGGER = LoggerFactory.getLogger("Smarter Golems");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("smartergolems.json");
    private static final Path TEMP_CONFIG_PATH = CONFIG_PATH.resolveSibling(CONFIG_PATH.getFileName() + ".tmp");
    private static SmarterGolemsConfig config;

    private SmarterGolemsConfigManager() {
    }

    /**
     * Returns the current in-memory configuration.
     */
    public static SmarterGolemsConfig get() {
        return config;
    }

    /**
     * Initializes and loads the configuration.
     *
     * @throws ConfigLoadException if the configuration file cannot be created, read or is invalid
     */
    public static void initialize() {
        try {
            config = initFile();
            if (config == null) {
                config = loadFile();
            }
        } catch (IOException | JsonParseException | IllegalStateException exception) {
            throw new ConfigLoadException("Failed to load configuration from " + CONFIG_PATH, exception);
        }

    }

    /**
     * Reloads the configuration from disk.
     * <p>
     * The current in-memory configuration is replaced only when the file can be successfully read and parsed.
     *
     * @return {@code true} when the configuration was successfully reloaded
     */
    public static IOResult reload() {
        try {
            config = loadFile();
            return IOResult.SUCCESS;
        } catch (JsonSyntaxException exception) {
            LOGGER.error("[SmarterGolems] Configuration file {} contains invalid JSON syntax or malformed format.", CONFIG_PATH, exception);
            return IOResult.MALFORMED_JSON;
        } catch (JsonIOException exception) {
            LOGGER.error("[SmarterGolems] Configuration file {} could not be read by the JSON parser stream.", CONFIG_PATH, exception);
            return IOResult.IO_ERROR;
        } catch (JsonParseException exception) {
            LOGGER.error("[SmarterGolems] Configuration file {} encountered a general JSON processing error.", CONFIG_PATH, exception);
            return IOResult.MALFORMED_JSON;
        } catch (NoSuchFileException exception) {
            LOGGER.error("[SmarterGolems] Configuration file {} does not exist.", CONFIG_PATH, exception);
            return IOResult.FILE_NOT_FOUND;
        } catch (AccessDeniedException exception) {
            LOGGER.error("[SmarterGolems] Permission denied while reading configuration file {}.", CONFIG_PATH, exception);
            return IOResult.ACCESS_DENIED;
        } catch (IOException exception) {
            LOGGER.error("[SmarterGolems] Failed to read configuration file from disk {}.", CONFIG_PATH, exception);
            return IOResult.IO_ERROR;
        } catch (IllegalStateException exception) {
            LOGGER.error("[SmarterGolems] Configuration file {} is empty.", CONFIG_PATH, exception);
            return IOResult.EMPTY_FILE;
        }
    }

    /**
     * Saves the current in-memory configuration to disk.
     */
    public static void save() {
        if (config == null) {
            return;
        }

        try {
            writeFile(config);
        } catch (IOException exception) {
            LOGGER.error("[SmarterGolems] Failed to save configuration to {}.", CONFIG_PATH, exception);
        }
    }

    /**
     * Creates the default configuration file when it does not exist.
     *
     * @return the newly created default configuration, or {@code null} when the file already exists
     * @throws IOException if the configuration file cannot be created
     */
    @Nullable
    private static SmarterGolemsConfig initFile() throws IOException {
        if (Files.exists(CONFIG_PATH)) {
            return null;
        }

        SmarterGolemsConfig defaultConfig = new SmarterGolemsConfig();
        writeFile(defaultConfig);
        return defaultConfig;
    }


    /**
     * Loads the configuration from disk.
     *
     * @return the loaded configuration, or {@code null} when loading fails
     * @throws IOException           if the configuration cannot be read
     * @throws JsonParseException    if the configuration contains invalid JSON
     * @throws IllegalStateException if the configuration file is empty
     */
    private static SmarterGolemsConfig loadFile() throws IOException, JsonParseException {
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            SmarterGolemsConfig loadedConfig = GSON.fromJson(reader, SmarterGolemsConfig.class);

            if (loadedConfig == null) {
                throw new IllegalStateException("Configuration file is empty.");
            }

            return loadedConfig;
        }
    }

    /**
     * Writes a configuration to disk.
     */
    private static void writeFile(SmarterGolemsConfig config) throws IOException {
        Files.createDirectories(CONFIG_PATH.getParent());

        try (Writer writer = Files.newBufferedWriter(TEMP_CONFIG_PATH)) {
            GSON.toJson(config, writer);
        }

        Files.move(TEMP_CONFIG_PATH, CONFIG_PATH, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    public enum IOResult {
        SUCCESS, FILE_NOT_FOUND, MALFORMED_JSON, EMPTY_FILE, ACCESS_DENIED, IO_ERROR
    }

    public static class ConfigLoadException extends RuntimeException {
        public ConfigLoadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}