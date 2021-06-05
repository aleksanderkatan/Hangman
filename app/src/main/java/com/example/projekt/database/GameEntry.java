package com.example.projekt.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Timestamp;

@Entity
public class GameEntry {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "player_1")
    public String player1;

    @ColumnInfo(name = "score_1")
    public int score1;

    @ColumnInfo(name = "player_2")
    public String player2;

    @ColumnInfo(name = "score_2")
    public int score2;

    @ColumnInfo(name = "begin_timestamp")
    public long begin;

    @ColumnInfo(name = "length_millis")
    public long length;
}
