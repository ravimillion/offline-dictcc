package com.example.appsideview;

import android.app.Activity;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeMap;

public class DictionaryService extends Service implements ClipboardManager.OnPrimaryClipChangedListener {
    public String TAG = DictionaryService.class.toString();
    public String lastSearchKey = new String("dummy");
    public String searchKey = null;
    public String dictPath = null;
    public Dictionary dictionary = null;
    public ClipboardManager clipBoard = null;
    private IBinder mBinder = new LocalBinder();
    private String dictDir = null;
    private int MAX_SRC = 10;

    public void onCreate() {
        Toast.makeText(getApplicationContext(), "OfflineDict Started", Toast.LENGTH_SHORT).show();
        dictPath = Environment.getExternalStorageDirectory() + File.separator + "Dictionary" + File.separator;
        dictionary = new Dictionary();
        clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipBoard.addPrimaryClipChangedListener(this);
    }

    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "OfflineDict Stopped", Toast.LENGTH_SHORT).show();
        clipBoard.removePrimaryClipChangedListener(this);
    }

    public void activateActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public String getSearchKey() {
        return this.searchKey;
    }

    public TreeMap<String, String> searchAndGetResults(String searchKey) throws FileNotFoundException {
        this.lastSearchKey = searchKey;
        TreeMap<String, String> rawResults = new TreeMap<>();
        TreeMap<String, String> tempResults = null;
        ArrayList<File> dictFiles = new ArrayList<>();
        for (int i = 1; i < MAX_SRC; i++) {
            String filePath = dictPath + this.dictDir + i + ".txt";
            try {
                dictFiles.add(Utils.getDictSourceFile(filePath));
            } catch(FileNotFoundException fe) {
                Log.d(TAG, "File not found: " + filePath);
                continue;
            }
        }

        if (dictFiles.size() == 0) throw new FileNotFoundException();

        for (int i = 0; i < dictFiles.size(); i++) {
            tempResults= dictionary.getTranslation(searchKey, dictFiles.get(i));
            Set<String> keys = tempResults.keySet();
            for (String k: keys) {
                rawResults.put(k, tempResults.get(k));
            }
        }

        return sortResultsByKeyLength(rawResults);
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

    public void setDirection(String dir) {
        this.dictDir = dir;
    }
    public void onPrimaryClipChanged() {
        ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
        CharSequence copiedText = item.getText();

        if (lastSearchKey.equals(copiedText) || copiedText == null) return;

        this.searchKey = copiedText.toString();
        activateActivity();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Dict Path: " + dictPath);
        Log.d(TAG, "Service bound");
        return mBinder;
    }

    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "Service Disconnected");
        return false;
    }

    public class LocalBinder extends Binder {
        DictionaryService getService() {
            return DictionaryService.this;
        }
    }

}
