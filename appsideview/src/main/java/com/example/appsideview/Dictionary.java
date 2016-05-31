package com.example.appsideview;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.UUID;
import android.util.Log;
import java.util.TreeMap;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Dictionary {
    String TAG = "Dictionary";

    private TreeMap<String, String> getResults(String searchKey, File file) {
        TreeMap<String, String> results = new TreeMap<>();
        String line = null;
        String l = null;
        String k = null;
        String v = null;
        try {
            String command = "grep -ihF " + searchKey + " " + file.getAbsolutePath(); // -h without filename
            Log.d(TAG, "Command: " + command);
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = br.readLine()) != null) {
                l = line.toString();
                int tabIndex = l.indexOf('\t');

                if (tabIndex > 0 && l.substring(0, tabIndex).toLowerCase().contains(searchKey)) {
                    k = l.substring(0, tabIndex);
                    v = l.substring(tabIndex + 1);
                    results.put(UUID.randomUUID().toString() + k, v);
                } else {
                    Log.d(TAG, l);
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

        return searchKey;
    }

    public TreeMap<String, String> getTranslation(String searchKey, File file){
        searchKey = processSearchKey(searchKey);
        TreeMap<String, String> rawResults = getResults(searchKey, file);
        return rawResults;
    }
}
