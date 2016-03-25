package com.example.raviz.offlinedictcc;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
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

    public HashMap<String, String> getResults(String searchKey, File file) {
        HashMap<String, String> results = new HashMap<>();
        String line = null;
        boolean limitFlag = false;
        int totalResults = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            while ((line = br.readLine()) != null) {
                String l = line.toString();
                int tabIndex = l.indexOf('\t');
                if (tabIndex > 0) {
                    String k = l.substring(0, tabIndex);
                    String v = l.substring(tabIndex + 1);

                    if (k.indexOf(searchKey) > -1) {
                        if (k.length() < (searchKey.length() + TYPE_LENGTH)) {
                            limitFlag = true;
                        }
                        results.put(UUID.randomUUID().toString() + k, v);
                        totalResults++;

                        if (limitFlag && totalResults > 50) {
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }


    public TreeMap<String, String> sortResultsByKeyLength(HashMap<String, String> results) {

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

    public TreeMap<String, String> getTranslation(String searchKey) {
        File file = getDictSourceFile();
        if (file == null) return null;

        HashMap<String, String> rawResults = getResults(searchKey, file);

        // check for past tense verbs
        if (rawResults.size() < 10) {
            if (searchKey.endsWith("ten")) {
                searchKey = searchKey.substring(0, searchKey.lastIndexOf("ten")) + "n"; // arbeiteten -> arbeiten
                Log.d(TAG, "Intelligent Search ten->n: " + searchKey);
                HashMap<String, String> results = getResults(searchKey, file);
                rawResults.putAll(results);
            }
        }
        if (rawResults.size() < 10) {
            if (searchKey.endsWith("test")) {
                searchKey = searchKey.substring(0, searchKey.lastIndexOf("test")) + "en"; // arbeiteten -> arbeiten
                Log.d(TAG, "Intelligent Search test->en: " + searchKey);
                HashMap<String, String> results = getResults(searchKey, file);
                rawResults.putAll(results);
            }
        }
        if (rawResults.size() < 10) {
            if (searchKey.endsWith("test")) {
                searchKey = searchKey.substring(0, searchKey.lastIndexOf("tet")) + "en"; // arbeiteten -> arbeiten
                Log.d(TAG, "Intelligent Search tet->en: " + searchKey);
                HashMap<String, String> results = getResults(searchKey, file);
                rawResults.putAll(results);
            }
        }
        if (rawResults.size() < 10) {
            if (searchKey.startsWith("ge") && searchKey.endsWith("t")) {
                searchKey = searchKey.replaceFirst("ge", "");
                searchKey = searchKey.substring(0, searchKey.lastIndexOf("t")) + "en";
                Log.d(TAG, "Intelligent Search preteritum: " + searchKey);
                HashMap<String, String> results = getResults(searchKey, file);
                rawResults.putAll(results);
            }
        }


        TreeMap<String, String> results = sortResultsByKeyLength(rawResults);
        return results;
    }
}
