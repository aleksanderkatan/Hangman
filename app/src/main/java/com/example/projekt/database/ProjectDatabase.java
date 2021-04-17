package com.example.projekt.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {GameEntry.class}, version = 1)
public abstract class ProjectDatabase extends RoomDatabase {

    public abstract GameEntryDao gameEntryDao();

    private static ProjectDatabase INSTANCE;

    public static ProjectDatabase getDatabaseInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ProjectDatabase.class, "PROJECT_DATABASE")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
}
