package com.example.appsideview.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBManager {
    private Context context = null;
    private String TAG = "DBManager";
    private static DBManager dbManager = new DBManager();
    private SQLiteDatabase wDB = null;
    private SQLiteDatabase rDB = null;

    public DBManager() {
    }

    public void setParams(Context context) {
        dbManager.context = context;
        DBCore dbCore = new DBCore(context);
        wDB = dbCore.getWritableDatabase();
        rDB = dbCore.getReadableDatabase();
    }

    public static DBManager getDBManager() {
        return dbManager;
    }

    public void printTable() {
        Cursor cursor = rDB.query(DBCore.Dictionary.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Log.d(TAG, cursor.getString(cursor.getColumnIndexOrThrow(DBCore.Dictionary.COLUMN_NAME_KEY)) + " | " +
                        cursor.getString(cursor.getColumnIndexOrThrow(DBCore.Dictionary.COLUMN_NAME_LANGUAGE_FROM)) + " | " +
                        cursor.getString(cursor.getColumnIndexOrThrow(DBCore.Dictionary.COLUMN_NAME_LANGUAGE_TO)) + " | " +
                        cursor.getInt(cursor.getColumnIndexOrThrow(DBCore.Dictionary.COLUMN_NAME_REVISIONS)));
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    public void isDuplicate() {
        Cursor cursor = rDB.query(DBCore.Dictionary.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Log.d(TAG, cursor.getString(cursor.getColumnIndexOrThrow(DBCore.Dictionary.COLUMN_NAME_KEY)) + " | " +
                        cursor.getString(cursor.getColumnIndexOrThrow(DBCore.Dictionary.COLUMN_NAME_LANGUAGE_FROM)) + " | " +
                        cursor.getString(cursor.getColumnIndexOrThrow(DBCore.Dictionary.COLUMN_NAME_LANGUAGE_TO)));
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    public String getNextWordSortedByRevision(String langFrom, String langTo) {
        String word = null;
        int revisionCount = 0;
        Cursor cursor = rDB.rawQuery("SELECT * FROM " + DBCore.Dictionary.TABLE_NAME + " WHERE " +
                DBCore.Dictionary.COLUMN_NAME_LANGUAGE_FROM + " = \"" + langFrom + "\" AND " +
                DBCore.Dictionary.COLUMN_NAME_LANGUAGE_TO + " = \"" + langTo + "\"" + " ORDER BY " + DBCore.Dictionary.COLUMN_NAME_REVISIONS + " ASC", null);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                    word = cursor.getString(cursor.getColumnIndexOrThrow(DBCore.Dictionary.COLUMN_NAME_KEY));
                    revisionCount = cursor.getInt(cursor.getColumnIndexOrThrow(DBCore.Dictionary.COLUMN_NAME_REVISIONS));
                    Log.d(TAG, "Returning: " + word);
            }

            String query = "UPDATE " + DBCore.Dictionary.TABLE_NAME + " SET "
                    + DBCore.Dictionary.COLUMN_NAME_REVISIONS + " = " + (revisionCount + 1) + " WHERE " + DBCore.Dictionary.COLUMN_NAME_KEY + " = \"" + word + "\"";
            Log.d(TAG, query);
            wDB.execSQL(query);

            return word;
        }

        printTable();

        return word;
    }

    public void saveInHistory(String word, String langFrom, String langTo) {
        Cursor cursor = rDB.rawQuery("SELECT * FROM " + DBCore.Dictionary.TABLE_NAME + " WHERE " +
                DBCore.Dictionary.COLUMN_NAME_KEY + " = \"" + word + "\" AND " +
                DBCore.Dictionary.COLUMN_NAME_LANGUAGE_FROM + " = \"" + langFrom + "\" AND " +
                DBCore.Dictionary.COLUMN_NAME_LANGUAGE_TO + " = \"" + langTo + "\"", null);

        if (cursor.getCount() > 0) {
            Log.d(TAG, "Duplicate entry: " + word);
            printTable();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(DBCore.Dictionary.COLUMN_NAME_KEY, word);
        values.put(DBCore.Dictionary.COLUMN_NAME_REVISIONS, 0);
        values.put(DBCore.Dictionary.COLUMN_NAME_LANGUAGE_FROM, langFrom);
        values.put(DBCore.Dictionary.COLUMN_NAME_LANGUAGE_TO, langTo);
        wDB.insert(DBCore.Dictionary.TABLE_NAME, null, values);
    }

//    public void addWord(Circle circle) {
//        ContentValues values = new ContentValues();
//        values.put(DBCore.Dancer.COLUMN_NAME_PROJECT_ID, circle.getProjectId());
//        values.put(DBCore.Dancer.COLUMN_NAME_DANCER_ID, circle.getCid());
//        values.put(DBCore.Dancer.COLUMN_NAME_COLOR, circle.getBasePaint().getColor());
//        values.put(DBCore.Dancer.COLUMN_NAME_BASE_LOC_X, circle.getBaseCenterX());
//        values.put(DBCore.Dancer.COLUMN_NAME_BASE_LOC_Y, circle.getBaseCenterY());
//        values.put(DBCore.Dancer.COLUMN_NAME_DANCER_NAME, circle.getDancerName());
//        values.put(DBCore.Dancer.COLUMN_NAME_RADIUS, circle.getRadius());
//
//        wDB.insert(DBCore.Dancer.TABLE_NAME, null, values);
//    }

    //    public void saveDancers(HashSet<Circle> dancers) {
//        ContentValues values = new ContentValues();
//        wDB.execSQL("DELETE FROM " + DBCore.Dancer.TABLE_NAME + " WHERE " +
//                DBCore.Dancer.COLUMN_NAME_PROJECT_ID + " = \"" + CircleManager.CURRENT_PROJECT.getpId() + "\"");
//
//        for (Circle c : dancers) {
//            Log.d(TAG, "Adding to db: " + c.toString());
//            addCircle(c);
//        }
//    }


//    public void updateProject(Project project) {
//        String query = "UPDATE " + DBCore.Project.TABLE_NAME + " SET " + DBCore.Project.COLUMN_NAME_CIRCLE_COUNT + " = \"" + project.getCircleCount() + "\" WHERE " + DBCore.Project.COLUMN_NAME_TITLE + "=\"" + project.getpId() + "\"";
//        Log.d(TAG, query);
//        wDB.execSQL(query);
//    }
//
//    public void updateDancerName(String dancerId, String name) {
//        String query = "UPDATE " + DBCore.Dancer.TABLE_NAME + " SET " + DBCore.Dancer.COLUMN_NAME_DANCER_NAME + " = \"" + name + "\" WHERE " + DBCore.Dancer.COLUMN_NAME_DANCER_ID + "=\"" + dancerId + "\"";
//        Log.d(TAG, query);
//        wDB.execSQL(query);
//    }
//
//    public void removeDancer(Circle dancer){
//        String queryDeleteDancers = "DELETE FROM " + DBCore.Dancer.TABLE_NAME + " WHERE " + DBCore.Dancer.COLUMN_NAME_DANCER_ID + " = \"" +dancer.getCid() +"\"" + " AND " + DBCore.Dancer.COLUMN_NAME_PROJECT_ID
//                + " = \"" + dancer.getProjectId() + "\"";
//        String queryDeleteTansitions = "DELETE FROM " + DBCore.Transitions.TABLE_NAME + " WHERE " + DBCore.Transitions.COLUMN_NAME_DANCER_ID + " = \"" +dancer.getCid() +"\"";
//        Log.d(TAG, queryDeleteDancers);
//        Log.d(TAG, queryDeleteTansitions);
//        wDB.execSQL(queryDeleteDancers);
//        wDB.execSQL(queryDeleteTansitions);
//    }
//
//    public void removeTransition(Record record){
//        String queryDeleteTansitions = "DELETE FROM " + DBCore.Transitions.TABLE_NAME + " WHERE " + DBCore.Dancer.COLUMN_NAME_DANCER_ID + " = \"" +record.getCid() +"\"" + " AND " + DBCore.Transitions.COLUMN_NAME_TRANS_ID
//                + " = \"" + record.getTransId() + "\"";
//        Log.d(TAG, queryDeleteTansitions);
//        wDB.execSQL(queryDeleteTansitions);
//    }
//
//    public Project loadProject(String projectId) {
//        Cursor cursor = rDB.query(DBCore.Project.TABLE_NAME, null, DBCore.Project.COLUMN_NAME_TITLE + "=?", new String[]{projectId}, null, null, null);
//        Project project = new Project();
//        if (cursor.moveToFirst()) {
//            do {
//                project.setpId(cursor.getString(cursor.getColumnIndexOrThrow(DBCore.Project.COLUMN_NAME_TITLE)));
//                project.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DBCore.Project.COLUMN_NAME_TITLE)));
//                project.setCircleCount(cursor.getInt(cursor.getColumnIndexOrThrow(DBCore.Project.COLUMN_NAME_CIRCLE_COUNT)));
//            } while (cursor.moveToNext());
//        }
//        if (cursor != null && !cursor.isClosed()) {
//            cursor.close();
//        }
//
//        return project;
//    }
//
//    public ArrayList<Project> loadProjects() {
//        ArrayList<Project> projects = new ArrayList<>();
//        Cursor cursor = rDB.query(DBCore.Project.TABLE_NAME, null, null, null, null, null, null);
//
//        if (cursor.moveToFirst()) {
//            do {
//                Project project = new Project();
//                project.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DBCore.Project.COLUMN_NAME_TITLE)));
//                project.setCircleCount(cursor.getInt(cursor.getColumnIndexOrThrow(DBCore.Project.COLUMN_NAME_CIRCLE_COUNT)));
//                projects.add(project);
//            } while (cursor.moveToNext());
//        }
//        if (cursor != null && !cursor.isClosed()) {
//            cursor.close();
//        }
//
//        return projects;
//    }
//
//    public void updateSettings(int refreshRate){
//        String query = "UPDATE " + DBCore.Settings.TABLE_NAME + " SET "
//                + DBCore.Settings.COLUMN_NAME_REFRESH_RATE + " = " + refreshRate;
//        Log.d(TAG, query);
//        wDB.execSQL(query);
//    }
//
//
//    public ArrayList<Circle> getDancersForProject(Project project) {
//        ArrayList<Circle> circles = new ArrayList<>();
//        Log.d(TAG, "Querying for: " + project.getTitle() + " " + DBCore.Dancer.COLUMN_NAME_PROJECT_ID + "=?");
//        Cursor cursor = rDB.query(DBCore.Dancer.TABLE_NAME, null, DBCore.Dancer.COLUMN_NAME_PROJECT_ID + "=?", new String[]{project.getTitle()}, null, null, null);
//
//        if (cursor.moveToFirst()) {
//            do {
//                Circle circle = new Circle(
//                        cursor.getFloat(cursor.getColumnIndexOrThrow(DBCore.Dancer.COLUMN_NAME_BASE_LOC_X)),
//                        cursor.getFloat(cursor.getColumnIndexOrThrow(DBCore.Dancer.COLUMN_NAME_BASE_LOC_Y)),
//                        cursor.getFloat(cursor.getColumnIndexOrThrow(DBCore.Dancer.COLUMN_NAME_RADIUS)),
//                        cursor.getString(cursor.getColumnIndexOrThrow(DBCore.Dancer.COLUMN_NAME_PROJECT_ID)),
//                        cursor.getString(cursor.getColumnIndexOrThrow(DBCore.Dancer.COLUMN_NAME_DANCER_ID)),
//                        cursor.getInt(cursor.getColumnIndexOrThrow(DBCore.Dancer.COLUMN_NAME_COLOR)),
//                        cursor.getString(cursor.getColumnIndexOrThrow(DBCore.Dancer.COLUMN_NAME_DANCER_NAME))
//                );
//
//                Log.d(TAG, "Loading Circle: " + circle.toString());
//                circles.add(circle);
//
//            } while (cursor.moveToNext());
//        }
//        if (cursor != null && !cursor.isClosed()) {
//            cursor.close();
//        }
//
//        for(Circle c: circles){
//            cursor = rDB.query(DBCore.Transitions.TABLE_NAME, null, DBCore.Transitions.COLUMN_NAME_DANCER_ID + "=?", new String[]{c.getCid()}, null, null, null);
//            ArrayList<Record> recordsList = new ArrayList<>();
//            if (cursor.moveToFirst()) {
//                do {
//                    Interval interval = new Interval();
//                    Position position = new Position();
//
//                    interval.setStartTS(cursor.getInt(cursor.getColumnIndexOrThrow(DBCore.Transitions.COLUMN_NAME_START_TS)));
//                    interval.setEndTS(cursor.getInt(cursor.getColumnIndexOrThrow(DBCore.Transitions.COLUMN_NAME_END_TS)));
//
//                    position.setStartX(cursor.getInt(cursor.getColumnIndexOrThrow(DBCore.Transitions.COLUMN_NAME_START_X)));
//                    position.setStartY(cursor.getInt(cursor.getColumnIndexOrThrow(DBCore.Transitions.COLUMN_NAME_START_Y)));
//                    position.setEndX(cursor.getInt(cursor.getColumnIndexOrThrow(DBCore.Transitions.COLUMN_NAME_END_X)));
//                    position.setEndY(cursor.getInt(cursor.getColumnIndexOrThrow(DBCore.Transitions.COLUMN_NAME_END_Y)));
//
//                    Record record = new Record(interval, position,
//                            cursor.getString(cursor.getColumnIndexOrThrow(DBCore.Transitions.COLUMN_NAME_MOVE_NAME)),
//                            cursor.getString(cursor.getColumnIndexOrThrow(DBCore.Transitions.COLUMN_NAME_DANCER_ID)),
//                            cursor.getString(cursor.getColumnIndexOrThrow(DBCore.Transitions.COLUMN_NAME_TRANS_ID))
//                    );
//                    recordsList.add(record);
//                } while (cursor.moveToNext());
//            }
//            c.setRecordsArray(recordsList);
//            if (cursor != null && !cursor.isClosed()) {
//                cursor.close();
//            }
//        }
//        return circles;
//    }
//
//    public void saveDancers(HashSet<Circle> dancers) {
//        ContentValues values = new ContentValues();
//        wDB.execSQL("DELETE FROM " + DBCore.Dancer.TABLE_NAME + " WHERE " +
//                DBCore.Dancer.COLUMN_NAME_PROJECT_ID + " = \"" + CircleManager.CURRENT_PROJECT.getpId() + "\"");
//
//        for (Circle c : dancers) {
//            Log.d(TAG, "Adding to db: " + c.toString());
//            addCircle(c);
//        }
//    }
//
//    public void saveTransitions(HashSet<Circle> dancers) {
//        ContentValues values = new ContentValues();
//        Iterator<Circle> iter = dancers.iterator();
//
//        while (iter.hasNext()) {
//            Circle circle = iter.next();
//            // Delete all transitions for this circle
//            wDB.execSQL("DELETE FROM " + DBCore.Transitions.TABLE_NAME + " WHERE " +
//                    DBCore.Transitions.COLUMN_NAME_DANCER_ID + " = \"" + circle.getCid() + "\"");
//
//            ArrayList<Record> records = circle.getRecordsArray();
//
//            for (int i = 0; i < records.size(); i++) {
//                Interval interval = records.get(i).getInterval();
//                Position position = records.get(i).getPosition();
//
//                values.put(DBCore.Transitions.COLUMN_NAME_DANCER_ID, circle.getCid());
//                values.put(DBCore.Transitions.COLUMN_NAME_TRANS_ID, records.get(i).getTransId());
//                values.put(DBCore.Transitions.COLUMN_NAME_MOVE_NAME, records.get(i).getText());
//                values.put(DBCore.Transitions.COLUMN_NAME_START_X, position.getStartX());
//                values.put(DBCore.Transitions.COLUMN_NAME_START_Y, position.getStartY());
//                values.put(DBCore.Transitions.COLUMN_NAME_END_X, position.getTargetX());
//                values.put(DBCore.Transitions.COLUMN_NAME_END_Y, position.getTargetY());
//                values.put(DBCore.Transitions.COLUMN_NAME_START_TS, interval.getStartTS());
//                values.put(DBCore.Transitions.COLUMN_NAME_END_TS, interval.getEndTS());
//                Log.d(TAG, "Adding transition: " + values.toString());
//                wDB.insert(DBCore.Transitions.TABLE_NAME, null, values);
//            }
//        }
//    }
//
//    public void addProject(Project project) {
//        Log.d(TAG, "Saving : " +  project.getCircleCount());
//        ContentValues values = new ContentValues();
//        values.put(DBCore.Project.COLUMN_NAME_TITLE, project.getTitle());
//        values.put(DBCore.Project.COLUMN_NAME_CIRCLE_COUNT, project.getCircleCount());
//        wDB.insert(DBCore.Project.TABLE_NAME, null, values);
//    }
//
//    public void addCircle(Circle circle) {
//        ContentValues values = new ContentValues();
//        values.put(DBCore.Dancer.COLUMN_NAME_PROJECT_ID, circle.getProjectId());
//        values.put(DBCore.Dancer.COLUMN_NAME_DANCER_ID, circle.getCid());
//        values.put(DBCore.Dancer.COLUMN_NAME_COLOR, circle.getBasePaint().getColor());
//        values.put(DBCore.Dancer.COLUMN_NAME_BASE_LOC_X, circle.getBaseCenterX());
//        values.put(DBCore.Dancer.COLUMN_NAME_BASE_LOC_Y, circle.getBaseCenterY());
//        values.put(DBCore.Dancer.COLUMN_NAME_DANCER_NAME, circle.getDancerName());
//        values.put(DBCore.Dancer.COLUMN_NAME_RADIUS, circle.getRadius());
//
//        wDB.insert(DBCore.Dancer.TABLE_NAME, null, values);
//    }
}

