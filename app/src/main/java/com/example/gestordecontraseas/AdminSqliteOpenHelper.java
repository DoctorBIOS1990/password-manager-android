package com.example.gestordecontraseas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSqliteOpenHelper extends SQLiteOpenHelper {

    public AdminSqliteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS user(id INTEGER PRIMARY KEY, password TEXT)");
        db.execSQL("INSERT OR IGNORE INTO user (id, password) VALUES(1, '')");
        db.execSQL("CREATE TABLE IF NOT EXISTS passwords_list(id INTEGER PRIMARY KEY AUTOINCREMENT, descripcion TEXT, password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
