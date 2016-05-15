package com.example.raviz.offlinedictcc;

import java.io.File;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Created by raviz on 15.05.16.
 */
public class Utils {
    public static boolean ifExists(String path) {
        File file = new File(path);
        return file.exists();
    }
}
