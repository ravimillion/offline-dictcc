package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Dictionary {
    public static void main(String args[]) {
        System.out.println("Initializing");
        String filePath = "/home/raviz/Documents/dict.txt";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
        } catch (Exception e) {
        }

        String searchKey = "eingeplant";
        String line = null;
        for (int i = 0; i < 1094057; i++) {
            try {
                line = br.readLine();
                if (line != null) {
                    String l = line.toString();
                    int tabIndex = l.indexOf('\t');
                    if (tabIndex > 0) {
                        String key = l.substring(0, tabIndex);
                        String value = l.substring(tabIndex + 1);
                        if (key.indexOf(searchKey) > -1) {
                            System.out.println("Word: " + key + " ===== Meaning: " + value);
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
