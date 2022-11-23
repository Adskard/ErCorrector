package cz.cvut.fel.utils;

import lombok.Getter;
import lombok.extern.java.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Class ConfigLoader is utility class that is used for
 * loading Config from a file into a Properties Object.
 *
 * @author Adam Skarda
 */
@Log
@Getter
public class ConfigLoader {
    private final Properties properties = new Properties();

    /**
     * Loads configuration from given file path into a Properties Object.
     *
     * @param path path to configuration file
     * @throws IOException when there is exception reading a file from given path
     */
    public void load(String path) throws IOException {
        InputStream input = new FileInputStream(path);
        properties.load(input);
        input.close();
    }
}
