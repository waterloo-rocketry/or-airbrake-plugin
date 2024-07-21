package com.waterloorocketry.airbrakeplugin.jni;

import java.io.*;
import java.net.URL;

public class LibraryLoader {
    /**
     * @return The appropriate dynamic library file extension for the operating system
     */
    private static String getExt() {
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows")) {
            return "dll";
        }
        return switch (osName) {
            case "Linux" -> "so";
            case "Mac OS X" -> "dylib";
            default -> throw new UnsupportedOperationException("Unknown operating system");
        };
    }

    /**
     * Loads a JNI dynamic library
     * @param cls Class to load native methods
     * @param name Name of the library
     */
    static void load(Class<?> cls, String name) {
        String path = "/lib" + name + "." + getExt();
        URL url = cls.getResource(path);
        if (url == null) {
            throw new RuntimeException("Library " + name + " not found");
        }
        try {
            final File libfile = File.createTempFile(name, ".lib");
            libfile.deleteOnExit();

            try (InputStream in = url.openStream()) {
                try (FileOutputStream out = new FileOutputStream(libfile)) {
                    in.transferTo(out);
                }
            }

            System.load(libfile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}