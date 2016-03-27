package com.example.raviz.offlinedictcc;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.Set;
import java.util.TreeMap;

/**
 * Created by raviz on 26.03.16.
 */
public class OfflineDictTest extends TestCase {
    @Test
    public void testSearch() {
        Dictionary dictionary = new Dictionary("/home/raviz/Documents/test.txt");
        TreeMap<String, String> results = dictionary.getTranslation("Einfuehrung");
        Set<String> keys = results.keySet();
        for(String k: keys) {
//            System.out.println(k.toString().substring(36) + " -- " + results.get(k.toString()));
        }
    }
}
