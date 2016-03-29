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
    public String dictPath = null;
    public int TYPE_LENGTH = 6; // {m} {f} or {pl}

    public Dictionary(String dictPath) {
        this.dictPath = dictPath;
    }

    public File getDictSourceFile() {
        File file = new File(dictPath);
        if (!file.exists()) {
            Log.d(TAG, "File not found : " + dictPath);
            return null;
        }

        return file;
    }

    private TreeMap<String, String> getResults(String searchKey, File file) {
        TreeMap<String, String> results = new TreeMap<>();
        String line = null;

        try {
            String command = "grep " + searchKey + " " + dictPath;
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

            while ((line = br.readLine()) != null) {
                String l = line.toString();
                int tabIndex = l.indexOf('\t');

                if (tabIndex > 0) {
                    String k = l.substring(0, tabIndex);
                    String v = l.substring(tabIndex + 1);
                    results.put(UUID.randomUUID().toString() + k, v);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }


    public TreeMap<String, String> sortResultsByKeyLength(TreeMap<String, String> results) {

        TreeMap<String, String> sortedResults = new TreeMap<String, String>(
                new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        if (s1.length() > s2.length()) {
                            return 1;
                        } else if (s1.length() < s2.length()) {
                            return -1;
                        } else {
                            return s1.compareTo(s2);
                        }
                    }
                });
        Set<String> keys = results.keySet();
        for (String k : keys) {
            sortedResults.put(k, results.get(k));
        }
        return sortedResults;
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

    public TreeMap<String, String> getTranslation(String searchKey) {
        File file = getDictSourceFile();
        if (file == null) return null;

        searchKey = processSearchKey(searchKey);
        TreeMap<String, String> rawResults = getResults(searchKey, file);
        TreeMap<String, String> results = sortResultsByKeyLength(rawResults);
        return results;

        // check for past tense verbs
//        if (rawResults.size() < 10) {
//            if (searchKey.endsWith("ten")) {
//                searchKey = searchKey.substring(0, searchKey.lastIndexOf("ten")) + "n"; // arbeiteten -> arbeiten
//                Log.d(TAG, "Intelligent Search ten->n: " + searchKey);
//                HashMap<String, String> results = getResults(searchKey, file);
//                rawResults.putAll(results);
//            }
//        }
//        if (rawResults.size() < 10) {
//            if (searchKey.endsWith("test")) {
//                searchKey = searchKey.substring(0, searchKey.lastIndexOf("test")) + "en"; // arbeiteten -> arbeiten
//                Log.d(TAG, "Intelligent Search test->en: " + searchKey);
//                HashMap<String, String> results = getResults(searchKey, file);
//                rawResults.putAll(results);
//            }
//        }
//        if (rawResults.size() < 10) {
//            if (searchKey.endsWith("test")) {
//                searchKey = searchKey.substring(0, searchKey.lastIndexOf("tet")) + "en"; // arbeiteten -> arbeiten
//                Log.d(TAG, "Intelligent Search tet->en: " + searchKey);
//                HashMap<String, String> results = getResults(searchKey, file);
//                rawResults.putAll(results);
//            }
//        }
//        if (rawResults.size() < 10) {
//            if (searchKey.startsWith("ge") && searchKey.endsWith("t")) {
//                searchKey = searchKey.replaceFirst("ge", "");
//                searchKey = searchKey.substring(0, searchKey.lastIndexOf("t")) + "en";
//                Log.d(TAG, "Intelligent Search preteritum: " + searchKey);
//                HashMap<String, String> results = getResults(searchKey, file);
//                rawResults.putAll(results);
//            }
//        }


//        TreeMap<String, String> results = sortResultsByKeyLength(rawResults);
//        return results;
    }
}
