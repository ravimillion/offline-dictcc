package com.example.raviz.offlinedictcc;

import android.graphics.Path;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by raviz on 15.05.16.
 */
public class DictFileMapping {
    public static HashMap<String, ArrayList<String>> FileMapping = new HashMap<>();
    public static void MapInit() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList = new ArrayList<>();
        arrayList.add("de-en1.txt");
        arrayList.add("de-en2.txt");
        FileMapping.put("de-en", arrayList); // deutsch to english

        arrayList = new ArrayList<>();
        arrayList.add("de-es1.txt");
        FileMapping.put("de-es", arrayList); // deutsch to spanish

        arrayList = new ArrayList<>();
        arrayList.add("en-de1.txt");
        FileMapping.put("en-de", arrayList); // english to deutsch

        arrayList = new ArrayList<>();
        arrayList.add("en-es1.txt");
        FileMapping.put("en-es", arrayList); // english to spanish

        arrayList = new ArrayList<>();
        arrayList.add("es-de1.txt");
        FileMapping.put("es-de", arrayList); // spanish to deutsch
    }
}
