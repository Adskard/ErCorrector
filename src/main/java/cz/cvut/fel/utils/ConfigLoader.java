package cz.cvut.fel.utils;

import lombok.Getter;
import lombok.extern.java.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Log
@Getter
public class ConfigLoader {
    private final Properties properties = new Properties();

    public void load(String path) throws IOException {
        InputStream input = new FileInputStream(path);
        properties.load(input);
        input.close();
    }
}
