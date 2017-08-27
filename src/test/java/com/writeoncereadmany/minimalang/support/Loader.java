package com.writeoncereadmany.minimalang.support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public interface Loader {

    static String loadSource(String name)  {
        try {
            return String.join("\n", Files.readAllLines(Paths.get("src/test/resources", name)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
