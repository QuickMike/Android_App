package khodkov.michael.chipin;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
    private static final int SCHEMA = 1;
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FIO = "fio";
    public static final String COLUMN_DEPO = "depo";
    public static final String COLUMN_CHECKBOX = "checkbox";

    public DatabaseHelper(Context context) {
        super(context, Message.SQL_NAME_DB, null, SCHEMA);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Message.SQL_NAME_USERS_TABLE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_FIO + " TEXT NOT NULL, " +
                COLUMN_DEPO +" TEXT NOT NULL DEFAULT '0.00', " +
                COLUMN_CHECKBOX +" INTEGER NOT NULL DEFAULT 0);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Message.SQL_NAME_USERS_TABLE + ";");
        onCreate(db);
    }

    public void delSQLTable(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + Message.SQL_NAME_USERS_TABLE + ";");
        onCreate(db);
    }
}
