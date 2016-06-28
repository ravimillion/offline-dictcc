package com.example.appsideview.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.appsideview.db.DBCore;
import com.example.appsideview.flashcard.FlashCardActivity;
import com.example.appsideview.R;
import com.example.appsideview.db.DBManager;
import com.example.appsideview.dictionary.DictEntryAdapter;
import com.example.appsideview.dictionary.DictionaryService;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String TAG = "MainActivity";
    private Button button = null;
    private ListView listView = null;
    private DictionaryService mBoundService;
    private TreeMap<String, String> results = null;
    private Menu mainMenu = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, DictionaryService.class);
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
                    searchResults(searchKey);
                } else {
                    Snackbar.make(listView, "Word too short", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initDataBase();
    }

    public void initDataBase() {
//        getApplicationContext().deleteDatabase(DBCore.DB_NAME);
        DBManager dbManager = DBManager.getDBManager();
        dbManager.setParams(getApplicationContext());
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
            mBoundService.setLanguage("de-en");
            Log.d(TAG, "Service bound");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        stopService(new Intent(this, DictionaryService.class));
        Log.d(TAG, "destroyed");
    }

    private void searchResults(String sk) {
        final String searchKey = sk;
        if (sk == null) return;
        EditText editText = (EditText) findViewById(R.id.editText);
        editText.setText(sk);
        new Thread() {
            @Override
            public void run() {
                Snackbar.make(listView, "Searching...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if (mBoundService != null) {
                    try {
                        results = mBoundService.getResults(searchKey);
                        if (results.size() > 0) {
                            DBManager.getDBManager().saveInHistory(searchKey, "de", "en");
                        }
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

    public void updateListView(TreeMap<String, String> results) {
        listView = (ListView) findViewById(R.id.listView);
        button = (Button) findViewById(R.id.button);

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
            if (results.size() > 0) {
                Snackbar.make(listView, "Found " + results.size() + " entries", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else {
                Snackbar.make(listView, "Nothing found ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        } else {
//            Snackbar.make(listView, "No source file", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
        }

        button.setVisibility(View.VISIBLE);
    }

    public String cleanUUID(String key) {
        return key.substring(36);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            this.moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.mainMenu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mBoundService != null) {
            String searchKey = mBoundService.getSearchKey();
            searchResults(searchKey);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setVisibility(View.INVISIBLE);
        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        if (id == R.id.de_en) {
//            mBoundService.setLanguage("de-en");
//        }
//        if (id == R.id.en_de) {
//            mBoundService.setLanguage("en-de");
//        }
//        if (id == R.id.es_de) {
//            mBoundService.setLanguage("es-de");
//        }
//        if (id == R.id.de_es) {
//            mBoundService.setLanguage("de-es");
//        }
//        if (id == R.id.en_es) {
//            mBoundService.setLanguage("en-es");
//        }
////        if (id == R.id.action_flashcard) {
////            fab.setVisibility(View.VISIBLE);
////        }
//        for (int i = 0; i < this.mainMenu.size(); i++) {
//            this.mainMenu.getItem(i).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
//        }
//        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.de_en) {
            mBoundService.setLanguage("de-en");
        }
        if (id == R.id.en_de) {
            mBoundService.setLanguage("en-de");
        }
        if (id == R.id.es_de) {
            mBoundService.setLanguage("es-de");
        }
        if (id == R.id.de_es) {
            mBoundService.setLanguage("de-es");
        }
        if (id == R.id.en_es) {
            mBoundService.setLanguage("en-es");
        }
        if (id == R.id.flashcards) {
            Intent myIntent = new Intent(MainActivity.this, FlashCardActivity.class);
//            myIntent.putExtra("key", value); //Optional parameters
            MainActivity.this.startActivity(myIntent);
        }


//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//        }
        for (int i = 0; i < this.mainMenu.size(); i++) {
            this.mainMenu.getItem(i).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
