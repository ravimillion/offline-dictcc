package com.example.appsideview.dictionary;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.appsideview.main.MainActivity;

import java.io.FileNotFoundException;
import java.util.TreeMap;

public class DictionaryService extends Service implements ClipboardManager.OnPrimaryClipChangedListener {
    public String TAG = DictionaryService.class.toString();
    public String lastSearchKey = new String("dummy");
    public String searchKey = null;
    public Dictionary dictionary = null;
    public ClipboardManager clipBoard = null;
    private IBinder mBinder = new LocalBinder();

    public void onCreate() {
        Toast.makeText(getApplicationContext(), "OfflineDict Started", Toast.LENGTH_SHORT).show();
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

    public TreeMap<String, String> getResults(String searchKey) throws FileNotFoundException {
        TreeMap<String, String> sortedResults = dictionary.getSearchResults(searchKey);
        return sortedResults;
    }



    public void setLanguage(String dir) {
        this.dictionary.setLanguage(dir);
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
        Log.d(TAG, "Service bound");
        return mBinder;
    }

    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "Service Disconnected");
        return false;
    }

    public class LocalBinder extends Binder {
        public DictionaryService getService() {
            return DictionaryService.this;
        }
    }

}
