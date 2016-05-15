package com.example.raviz.offlinedictcc;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

public class Dictionary {
    String TAG = "Dictionary";

    public Dictionary() {
    }


    public File getDictSourceFile(String dictPath) {
        File file = new File(dictPath);
        if (!file.exists()) {
            Log.d(TAG, "File not found : " + file.getAbsolutePath());
            return null;
        }

        return file;
    }

    private TreeMap<String, String> getResults(String searchKey, File file) {
        TreeMap<String, String> results = new TreeMap<>();
        String line = null;

        try {
            String command = "grep " + searchKey + " " + file.getAbsolutePath();
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

            while ((line = br.readLine()) != null) {
                String l = line.toString();
                int tabIndex = l.indexOf('\t');

                if (tabIndex > 0) {
                    String k = l.substring(0, tabIndex);
                    String v = l.substring(tabIndex + 1);
                    results.put(UUID.randomUUID().toString() + k, v);
                } else {
                  Log.d(TAG, " Error: Tab index seperator not found");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    public String processSearchKey(String searchKey) {
        searchKey = searchKey.replaceAll("ae", "ä");
        searchKey = searchKey.replaceAll("AE", "Ä");

        searchKey = searchKey.replaceAll("ue", "ü");
        searchKey = searchKey.replaceAll("UE", "Ü");

        searchKey = searchKey.replaceAll("oe", "ö");
        searchKey = searchKey.replaceAll("OE", "Ö");

        System.out.println("New search key: " + searchKey);
        return searchKey;
    }

    public TreeMap<String, String> getTranslation(String searchKey, String dictPath) {
        File file = getDictSourceFile(dictPath);
        if (file == null) return null;

        searchKey = processSearchKey(searchKey);
        TreeMap<String, String> rawResults = getResults(searchKey, file);
        return rawResults;
    }
}
