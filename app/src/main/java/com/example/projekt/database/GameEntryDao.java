package com.example.projekt.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Index;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GameEntryDao {

   @Query("SELECT * FROM gameentry")
   List<GameEntry> getAllEntries();

   @Insert
   void insertGameEntry(GameEntry... entries);

   @Delete
   void delete (GameEntry entry);
}
