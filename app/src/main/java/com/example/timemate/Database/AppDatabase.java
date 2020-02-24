package com.example.timemate.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {OneDay.class, TotalData.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract OneDayDao oneDayDao();
    public abstract TotalDataDao totalDataDao();

    private static AppDatabase INSTANCE;

    // 디비 객체 생성
    public static AppDatabase getAppDatabase(Context context) {
        if(INSTANCE==null) {
            INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "db-TimeMate").allowMainThreadQueries().build();
        }
        return INSTANCE;
    }
    // 디비 객체제거
    public static void destroyINSTANCE() {
        INSTANCE = null;
    }
}
