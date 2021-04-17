package com.example.projekt;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekt.adapters.GameEntryListAdapter;
import com.example.projekt.database.GameEntry;
import com.example.projekt.database.ProjectDatabase;

import java.sql.Timestamp;
import java.util.List;
import java.util.Random;

public class GamesLogActivity extends AppCompatActivity {
    Button btAdd;
    Button btClear;
    RecyclerView rv;
    GameEntryListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games_log);

        btAdd = findViewById(R.id.btAdd);
        btClear = findViewById(R.id.btClear);
        rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rv.addItemDecoration(dividerItemDecoration);
        adapter = new GameEntryListAdapter(this);
        rv.setAdapter(adapter);

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random(System.currentTimeMillis());
                GameEntry entry = new GameEntry();

                entry.player1 = "Worthless imp " + random.nextInt(10000);
                entry.player2 = "Worthless imp " + random.nextInt(10000);
                entry.score1 = random.nextInt(10);
                entry.score1 = random.nextInt(10);
                entry.endedPrematurely = random.nextBoolean();
                entry.begin =System.currentTimeMillis();
                entry.length = random.nextInt(1000)+100;

                saveNewGameEntry(entry);
                loadGameEntryList();
            }
        });

        btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetDatabase();
                loadGameEntryList();
            }
        });


        loadGameEntryList();
    }

    private void resetDatabase() {
        ProjectDatabase db = ProjectDatabase.getDatabaseInstance(this.getApplicationContext());
        db.clearAllTables();
    }

    private void saveNewGameEntry(GameEntry entry) {
        ProjectDatabase db = ProjectDatabase.getDatabaseInstance(this.getApplicationContext());
        db.gameEntryDao().insertGameEntry(entry);
    }

    private void loadGameEntryList() {
        ProjectDatabase db = ProjectDatabase.getDatabaseInstance(this.getApplicationContext());
        List<GameEntry> gameEntryList = db.gameEntryDao().getAllEntries();
        adapter.setGameEntryList(gameEntryList);
        rv.setAdapter(adapter);
    }
}