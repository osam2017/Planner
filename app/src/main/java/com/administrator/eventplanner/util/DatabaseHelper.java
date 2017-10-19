package com.administrator.eventplanner.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.administrator.eventplanner.db.PlanDb;
import com.administrator.eventplanner.db.SubPlanDb;

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "plandata";
    private static final int DATABASE_VERSION = 3;

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PlanDb.CREATE_SQL);
        db.execSQL(SubPlanDb.CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if( oldVersion == 1 && newVersion == 2 ){
            db.execSQL(PlanDb.CREATE_SQL);
        }
        if( oldVersion == 2 && newVersion == 3) {
            //db.execSQL("alter table " + ProjectDao.TABLE_NAME + " add " + ProjectDao.COLUMN_imagePath + " TEXT");
            db.execSQL(PlanDb.CREATE_SQL);
        }
    }
}
