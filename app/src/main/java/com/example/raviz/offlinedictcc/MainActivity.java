package com.example.raviz.offlinedictcc;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private Button button = null;
    private ListView listView = null;
    private EditText editText = null;
    private DictService mBoundService;
    private TreeMap<String, String> results = null;
    private Menu mainMenu = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // start service
        Intent intent = new Intent(this, DictService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

        listView = (ListView) findViewById(R.id.listView);
        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.editText);
                String searchKey = editText.getText().toString();
                if (searchKey.length() > 1) {
                    Snackbar.make(listView, "Searching...", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    searchResults(searchKey);
                } else {
                    Snackbar.make(listView, "Word too short", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        setupToolbar();
    }

    private void searchResults(String sk) {
        final String searchKey = sk;
        EditText editText = (EditText) findViewById(R.id.editText);
        editText.setText(sk);
        new Thread() {
            @Override
            public void run() {
                Snackbar.make(listView, "Searching...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if (mBoundService != null) {
                    results = mBoundService.searchAndGetResults(searchKey);
                    try {
                        // code runs in a thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateListView(results);
                            }
                        });
                    } catch (final Exception ex) {
                        Log.i("---", "Exception in thread");
                    }
                }
            }
        }.start();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
//        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
//        ab.setDisplayHomeAsUpEnabled(true);
    }

    public void updateListView(TreeMap<String, String> results) {
        String searchKey = mBoundService.getSearchKey();

        listView = (ListView) findViewById(R.id.listView);
        button = (Button) findViewById(R.id.button);
//
//        editText.setText(searchKey);
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
            Snackbar.make(listView, "Found " + results.size() + " entries", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        button.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (mBoundService != null) {
            String searchKey = mBoundService.getSearchKey();
            searchResults(searchKey);
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBoundService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DictService.LocalBinder myBinder = (DictService.LocalBinder) service;
            mBoundService = myBinder.getService();
            mBoundService.setDirection("de-en");
        }
    };

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d(TAG, "OnPostResume");
    }

    public String cleanUUID(String key) {
        return key.substring(36);
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        stopService(new Intent(this, DictService.class));
        Log.d(TAG, "destroyed");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.mainMenu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.de_en) {
            mBoundService.setDirection("de-en");
        }
        if (id == R.id.en_de) {
            mBoundService.setDirection("en-de");
        }
        if (id == R.id.es_de) {
            mBoundService.setDirection("es-de");
        }
        if (id == R.id.de_es) {
            mBoundService.setDirection("de-es");
        }
        if (id == R.id.en_es) {
            mBoundService.setDirection("en-es");
        }
        for (int i = 0; i < this.mainMenu.size(); i++) {
            this.mainMenu.getItem(i).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onOptionsItemSelected(item);
    }

}
