package com.example.appsideview.db;

/**
 * Created by ravi on 31.05.16.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;


/**
 * Created by ravi on 19.09.15.
 */
public class DBCore extends SQLiteOpenHelper{

    private String TAG = "DBManager";
    public static String DB_NAME = "offline-dict.db";
    public static int DB_VERSION = 1;
    private DBCore dbManager = null;
    public DBCore(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(TAG, "OnCreateDatabase");
            db.execSQL(DBCore.Dictionary.CREATE_TABLE_HISTORY);
//            db.execSQL(DBCore.Dancer.CREATE_TABLE_DANCER);
//            db.execSQL(DBCore.Transitions.CREATE_TABLE_TRANSITIONS);
//            db.execSQL(DBCore.Settings.CREATE_TABLE_SETTINGS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int oldVersion, int newVersion) {

    }

    // data type definition
    private static final String TEXT_TYPE = " TEXT";
    private static final String LONG_TYPE = " LONG";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    // COLUMN definition
    public static abstract class Dictionary implements BaseColumns {
        public static final String TABLE_NAME = "dictionary";
        public static final String COLUMN_NAME_KEY = "key";
        public static final String COLUMN_NAME_REVISIONS = "revisions";
        public static final String COLUMN_NAME_LANGUAGE_FROM = "lang_from";
        public static final String COLUMN_NAME_LANGUAGE_TO = "lang_to";
        public static final String COLUMN_NAME_INDEX = "idx";

        private static final String CREATE_TABLE_HISTORY = "CREATE TABLE " +
                Dictionary.TABLE_NAME + " (" +
                Dictionary._ID + " INTEGER PRIMARY KEY," + Dictionary.COLUMN_NAME_KEY + TEXT_TYPE + COMMA_SEP +
                Dictionary.COLUMN_NAME_REVISIONS + INTEGER_TYPE + COMMA_SEP +
                Dictionary.COLUMN_NAME_LANGUAGE_FROM + TEXT_TYPE + COMMA_SEP +
                Dictionary.COLUMN_NAME_LANGUAGE_TO + TEXT_TYPE + COMMA_SEP +
                Dictionary.COLUMN_NAME_INDEX + INTEGER_TYPE +
                " )";
    }
















//
//
//    /* Inner class that defines the table contents */
//    public static abstract class Project implements BaseColumns {
//        public static final String TABLE_NAME = "projects";
//        public static final String COLUMN_NAME_TITLE = "title";
//        public static final String COLUMN_NAME_CIRCLE_COUNT = "circle_count";
//
//        private static final String CREATE_TABLE_PROJECT =
//                "CREATE TABLE " + Project.TABLE_NAME + " (" +
//                        Project._ID + " INTEGER PRIMARY KEY," +
//                        Project.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
//                        Project.COLUMN_NAME_CIRCLE_COUNT + INTEGER_TYPE +
//                        " )";
//    }
//
//    public static abstract class Dancer implements BaseColumns {
//        public static final String TABLE_NAME = "dancer";
//        public static final String COLUMN_NAME_DANCER_NAME = "name";
//        public static final String COLUMN_NAME_PROJECT_ID = "project_id";
//        public static final String COLUMN_NAME_DANCER_ID = "dancer_id";
//        public static final String COLUMN_NAME_COLOR = "color";
//        public static final String COLUMN_NAME_BASE_LOC_X = "base_x";
//        public static final String COLUMN_NAME_BASE_LOC_Y = "base_y";
//        public static final String COLUMN_NAME_RADIUS = "radius";
//
//        private static final String CREATE_TABLE_DANCER =
//                "CREATE TABLE " + Dancer.TABLE_NAME + " (" +
//                        Dancer._ID + " INTEGER PRIMARY KEY," +
//                        Dancer.COLUMN_NAME_PROJECT_ID + TEXT_TYPE + COMMA_SEP +
//                        Dancer.COLUMN_NAME_DANCER_NAME + TEXT_TYPE + COMMA_SEP +
//                        Dancer.COLUMN_NAME_DANCER_ID + INTEGER_TYPE + COMMA_SEP +
//                        Dancer.COLUMN_NAME_COLOR + TEXT_TYPE + COMMA_SEP +
//                        Dancer.COLUMN_NAME_BASE_LOC_X + INTEGER_TYPE + COMMA_SEP +
//                        Dancer.COLUMN_NAME_BASE_LOC_Y + INTEGER_TYPE + COMMA_SEP +
//                        Dancer.COLUMN_NAME_RADIUS + INTEGER_TYPE +
//                        " )";
//    }
//
//    public static abstract class Transitions implements BaseColumns {
//        public static final String TABLE_NAME = "transitions";
//        public static final String COLUMN_NAME_TRANS_ID = "trans_id";
//        public static final String COLUMN_NAME_DANCER_ID = "dancer_id";
//        public static final String COLUMN_NAME_MOVE_NAME = "move_name";
//        public static final String COLUMN_NAME_START_X = "start_x";
//        public static final String COLUMN_NAME_START_Y = "start_y";
//        public static final String COLUMN_NAME_END_X = "end_x";
//        public static final String COLUMN_NAME_END_Y = "end_y";
//        public static final String COLUMN_NAME_START_TS = "start_ts";
//        public static final String COLUMN_NAME_END_TS = "end_ts";
//
//        private static final String CREATE_TABLE_TRANSITIONS =
//                "CREATE TABLE " + Transitions.TABLE_NAME + " (" +
//                        Transitions._ID + " INTEGER PRIMARY KEY," +
//                        Transitions.COLUMN_NAME_TRANS_ID + TEXT_TYPE + COMMA_SEP +
//                        Transitions.COLUMN_NAME_DANCER_ID + TEXT_TYPE + COMMA_SEP +
//                        Transitions.COLUMN_NAME_MOVE_NAME + TEXT_TYPE + COMMA_SEP +
//                        Transitions.COLUMN_NAME_START_TS + LONG_TYPE + COMMA_SEP +
//                        Transitions.COLUMN_NAME_END_TS + LONG_TYPE + COMMA_SEP +
//                        Transitions.COLUMN_NAME_START_X + INTEGER_TYPE + COMMA_SEP +
//                        Transitions.COLUMN_NAME_START_Y + INTEGER_TYPE + COMMA_SEP +
//                        Transitions.COLUMN_NAME_END_X + INTEGER_TYPE + COMMA_SEP +
//                        Transitions.COLUMN_NAME_END_Y + INTEGER_TYPE +
//                        " )";
//    }
//
//    public static abstract class Settings implements BaseColumns {
//        public static final String TABLE_NAME = "settings";
//        public static final String COLUMN_NAME_REFRESH_RATE = "refresh_rate";
//        private static final String CREATE_TABLE_SETTINGS =
//                "CREATE TABLE " + Settings.TABLE_NAME + " (" +
//                        Settings._ID + " INTEGER PRIMARY KEY," +
//                        Settings.COLUMN_NAME_REFRESH_RATE + INTEGER_TYPE +
//                        " )";
//    }
}
