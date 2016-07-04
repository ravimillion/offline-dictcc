package com.example.appsideview.flashcard;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.appsideview.R;
import com.example.appsideview.db.DBCore;
import com.example.appsideview.db.DBManager;
import com.example.appsideview.dictionary.DictEntryAdapter;
import com.example.appsideview.dictionary.DictionaryService;
import com.example.appsideview.touch.OnSwipeTouchListener;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeMap;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FlashCardActivity extends FragmentActivity {
    private String TAG = "FlashCardActivity";
    private ListView listView = null;
    private TextView textView = null;
    private DictionaryService mBoundService = null;
    private TreeMap<String, String> results = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_card);

        // start service
        Intent intent = new Intent(this, DictionaryService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

        textView = (TextView) findViewById(R.id.textView2);
        textView.setVisibility(View.VISIBLE);

        listView = (ListView) findViewById(R.id.listView2);
        listView.setVisibility(View.INVISIBLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                findViewById(R.id.textView2).setVisibility(View.VISIBLE);
                findViewById(R.id.listView2).setVisibility(View.INVISIBLE);
            }
        });

        findViewById(R.id.flashCardFrameLayout).setOnTouchListener(new OnSwipeTouchListener(FlashCardActivity.this) {
            public void onSwipeTop() {
                findViewById(R.id.textView2).setVisibility(View.INVISIBLE);
                findViewById(R.id.listView2).setVisibility(View.VISIBLE);
            }

            public void onSwipeRight() {
                String word = DBManager.getDBManager().getNextWordSortedByRevision(mBoundService.getLanguageFrom(), mBoundService.getLanguageTo());
                if (word == null) {
                    textView.setText("No word found.");
                    return;
                }

                searchResults(word);
                TextView textView = (TextView) findViewById(R.id.textView2);
                textView.setText(word);
            }

            public void onSwipeLeft() {
                String word = DBManager.getDBManager().getNextWordSortedByRevision(mBoundService.getLanguageFrom(), mBoundService.getLanguageTo());
                if (word == null) {
                    textView.setText("No word found.");
                    return;
                }

                searchResults(word);
                TextView textView = (TextView) findViewById(R.id.textView2);
                textView.setText(word);
            }

            public void onSwipeBottom() {
//                Log.d(TAG, "down");
            }
        });
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBoundService = null;
            Log.d(TAG, "Service disconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DictionaryService.LocalBinder myBinder = (DictionaryService.LocalBinder) service;
            mBoundService = myBinder.getService();
            Log.d(TAG, "Current language: " + mBoundService.getLanguageFrom());
            Log.d(TAG, "Service bound");
        }
    };

    private void showListView() {
        findViewById(R.id.textView2).setVisibility(View.INVISIBLE);
        findViewById(R.id.listView2).setVisibility(View.VISIBLE);
    }

    private void searchResults(String sk) {
        final String searchKey = sk;
        if (sk == null) return;
        new Thread() {
            @Override
            public void run() {
                if (mBoundService != null) {
                    try {
                        results = mBoundService.getResults(searchKey);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Snackbar.make(listView, "No source file", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateListView(results);
                        }
                    });
                } else {
                    Snackbar.make(listView, "Service not bound", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        }.start();
    }

    private void updateListView(TreeMap<String, String> results) {
        if (results != null) {
            Set<String> keySet = results.keySet();
            String[] keysArray = Arrays.copyOf(keySet.toArray(), keySet.toArray().length, String[].class);
            String[] valuesArray = new String[keysArray.length];

            for (int i = 0; i < keysArray.length; i++) {
                valuesArray[i] = results.get(keysArray[i]).toString();
                keysArray[i] = cleanUUID(keysArray[i]);
            }

            DictEntryAdapter dictEntryAdapter = new DictEntryAdapter(this, keysArray, valuesArray);
            listView.setAdapter(dictEntryAdapter);

        }
    }

    private String cleanUUID(String key) {
        return key.substring(36);
    }

    public void setDictionaryService(DictionaryService dictService) {
        this.mBoundService = dictService;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        Log.d(TAG, "destroyed");
    }
}
