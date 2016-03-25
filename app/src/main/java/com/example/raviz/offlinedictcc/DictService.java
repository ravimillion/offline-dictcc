package com.example.raviz.offlinedictcc;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;
import java.util.TreeMap;

public class DictService extends Service implements ClipboardManager.OnPrimaryClipChangedListener {
    public String TAG = DictService.class.toString();
    public String lastSearchKey = new String("dummy");
    public String dictPath = null;
    public Dictionary dictionary = null;
    public ClipboardManager clipBoard = null;
    private IBinder mBinder = new LocalBinder();
    private Activity context = null;
    private TreeMap<String, String> results = null;

    public DictService() {
    }

    public void setContext(Activity ctx) {
        Log.d(TAG, "Setting context " + ctx.toString());
        this.context = ctx;

    }

    public void onCreate() {
        Toast.makeText(getApplicationContext(), "OfflineDick Started", Toast.LENGTH_SHORT).show();
        dictPath = Environment.getExternalStorageDirectory() + File.separator + "Dictionary" + File.separator + "dict.txt";
        dictionary = new Dictionary(dictPath);
        clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipBoard.addPrimaryClipChangedListener(this);
    }

    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "OfflineDick Stopped", Toast.LENGTH_SHORT).show();
        clipBoard.removePrimaryClipChangedListener(this);
    }

    public void activateActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public TreeMap<String, String> getResults() {
        Log.d(TAG, "Results returned");
        return this.results;
    }

    public void onPrimaryClipChanged() {
        Toast.makeText(getApplicationContext(), "Searching...", Toast.LENGTH_SHORT).show();

        ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
        CharSequence copiedText = item.getText();

        if (lastSearchKey.equals(copiedText)) {
            Log.d(TAG, "Repeating search term");
            return;
        }

        Log.d(TAG, "Searching for... " + copiedText.toString());
        lastSearchKey = copiedText.toString();

        this.results = dictionary.getTranslation(copiedText.toString());
        if (results.keySet().size() == 0) {
            Toast.makeText(getApplicationContext(), "Nothing found", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Nothing found");
            return;
        } else {
            Log.d(TAG, "Total results: " + results.size());
        }

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
        DictService getService() {
            return DictService.this;
        }
    }

}
