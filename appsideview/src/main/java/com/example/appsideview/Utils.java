package com.example.appsideview;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by raviz on 15.05.16.
 */
public class Utils {
    public static boolean ifExists(String path) {
        File file = new File(path);
        return file.exists();
    }


    public static File getDictSourceFile(String dictPath) throws FileNotFoundException {
        File file = new File(dictPath);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        return file;
    }
}
