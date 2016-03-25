package com.example.raviz.offlinedictcc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private DictService mBoundService;
    private boolean mServiceBound = false;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "###################Starting main activity########################");
        startService(new Intent(this, DictService.class));
        Log.d(TAG, "On create Counter value: " + counter++);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
//        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
//        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, DictService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

        if (mBoundService != null) {
            TreeMap<String, String> results = mBoundService.getResults();


            ListView listView = (ListView) findViewById(R.id.listView);

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


        } else {
//        ListView listView = (ListView) findViewById(R.id.listView);
//        String[] keys = {"Empty"};
//        String[] values = {"Results"};
//        DictEntryAdapter dictEntryAdapter = new DictEntryAdapter(this, keys, values);
//        listView.setAdapter(dictEntryAdapter);
        }

    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DictService.LocalBinder myBinder = (DictService.LocalBinder) service;
            mBoundService = myBinder.getService();
            mServiceBound = true;
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

        return super.onOptionsItemSelected(item);
    }

}
