package com.myapp.donotsmoke;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import static com.myapp.donotsmoke.MainActivity.LOG_TAG;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATE_TIME_OF_LAST_CIGARETTE = "date_time_of_last_cigarette";
    public static final String CIGARETTES_PER_DAY = "cigarettes_per_day";
    public static final String PACK_AVERAGE_COST = "pack_average_cost";

    public DBHelper(@Nullable Context context) {
        super(context, "doNotSmokeDB", null, 1);
    }

    // Метод создания БД
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "--- onCreate database ---");

        //создаем таблицу с полями
        db.execSQL("create table inputDataTable ("
                + "id integer primary key autoincrement,"
                + "date_time_of_last_cigarette integer," //в ms. В SQLite integer включает long. Подбирает размер автоматически
                + "cigarettes_per_day integer,"
                + "pack_average_cost real"
                + ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    void insertInputData(int cigarettesPerDay, float packAverageCost, long dateTimeOfLastCigarette) {
        //выполняет подключение к базе данных и возвращает нам объект SQLiteDatabase для работы с ней
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DATE_TIME_OF_LAST_CIGARETTE, dateTimeOfLastCigarette);
        cv.put(CIGARETTES_PER_DAY, cigarettesPerDay);
        cv.put(PACK_AVERAGE_COST, packAverageCost);

        long rowID = db.insert("inputDataTable", null, cv);
        Log.d(LOG_TAG, "row with ID = " + rowID + " inserted in inputDataTable");
    }

    // чтение последней строчки таблицы
    InputTableRow readLastRowOfInputDataTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query("inputDataTable", null, null, null, null, null, null);
        InputTableRow itr;
        if (c.moveToLast()) {
            itr = new InputTableRow(
                    c.getLong(c.getColumnIndex(DATE_TIME_OF_LAST_CIGARETTE)),
                    c.getInt(c.getColumnIndex(CIGARETTES_PER_DAY)),
                    c.getFloat(c.getColumnIndex(PACK_AVERAGE_COST))
            );
        }
        else itr = new InputTableRow(0, 0, 0);
        c.close();
        Log.d(LOG_TAG, itr.toString()); //перегруженный toString()
        return itr;
    }
}