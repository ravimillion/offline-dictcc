package com.example.appsideview.dictionary;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.UUID;

import android.os.Environment;
import android.util.Log;

import com.example.appsideview.main.Utils;

import java.util.TreeMap;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Dictionary {
    private String TAG = "Dictionary";
    private String language = "de_en";
    private int MAX_DICTIONARY = 10;
    private String dictPath = Environment.getExternalStorageDirectory() + File.separator + "Dictionary" + File.separator;

    private String processSearchKey(String searchKey) {
        searchKey = searchKey.replaceAll("ae", "ä");
        searchKey = searchKey.replaceAll("AE", "Ä");

        searchKey = searchKey.replaceAll("ue", "ü");
        searchKey = searchKey.replaceAll("UE", "Ü");

        searchKey = searchKey.replaceAll("oe", "ö");
        searchKey = searchKey.replaceAll("OE", "Ö");

        return searchKey;
    }

    private String prepareCommand(String searchKey, ArrayList<File> dictFiles) {
        StringBuffer command = new StringBuffer("grep -ihF " + searchKey + " ");
        for (int i = 0; i < dictFiles.size(); i++) {
            command.append(dictFiles.get(i).getAbsolutePath() + " "); // -h without filename
        }
        Log.d(TAG, "Command: " + command.toString());
        return command.toString();
    }

    private TreeMap<String, String> getResults(String searchKey, ArrayList<File> dictFiles) {
        TreeMap<String, String> results = new TreeMap<>();
        String line = null;
        String l = null;
        String k = null;
        String v = null;
        String searchKeyLowerCase = searchKey.toLowerCase();
        String command = prepareCommand(searchKey, dictFiles);
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = br.readLine()) != null) {
                l = line.toString();
                int tabIndex = l.indexOf('\t');
                String keyString = l.substring(0, tabIndex).toLowerCase();
                if (tabIndex > 0 && keyString.contains(searchKeyLowerCase)) {
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

    private TreeMap<String, String> sortResultsByKeyLength(TreeMap<String, String> results) {
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
    private TreeMap<String, String> searchDictionaryFiles(String searchKey, ArrayList<File> dictFiles){
        searchKey = processSearchKey(searchKey);
        TreeMap<String, String> rawResults = getResults(searchKey, dictFiles);
        return rawResults;
    }



    public TreeMap<String, String> getSearchResults(String searchKey) throws FileNotFoundException {
        TreeMap<String, String> rawResults;
        ArrayList<File> dictFiles = new ArrayList<>();

        for (int i = 1; i < MAX_DICTIONARY; i++) {
            String filePath = dictPath + this.language + i + ".txt";
            try {
                dictFiles.add(Utils.getDictSourceFile(filePath));
            } catch(FileNotFoundException fe) {
                Log.d(TAG, "File not found: " + filePath);
                continue;
            }
        }
        Log.d(TAG, "Total searchable files: " + dictFiles.size());

        if (dictFiles.size() == 0) throw new FileNotFoundException();

        rawResults = searchDictionaryFiles(searchKey, dictFiles);
        return sortResultsByKeyLength(rawResults);
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
