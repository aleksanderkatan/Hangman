package com.example.projekt.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekt.R;
import com.example.projekt.database.GameEntry;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class GameEntryListAdapter extends RecyclerView.Adapter<GameEntryListAdapter.MyViewHolder> {
    private final Context context;
    private List<GameEntry> gameEntryList;

    public GameEntryListAdapter(Context context) {
        this.context = context;
    }

    public void setGameEntryList(List<GameEntry> gameEntryList) {
        this.gameEntryList = gameEntryList;
    }

    @NonNull
    @Override
    public GameEntryListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_row_game_entry, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull GameEntryListAdapter.MyViewHolder holder, int position) {
        position = getItemCount()-position-1;
        GameEntry entry = this.gameEntryList.get(position);
        StringBuilder text = new StringBuilder(entry.player1);
        text.append(" VS ");
        text.append(entry.player2);
        text.append("\n");
        text.append(entry.score1);
        text.append(" : ");
        text.append(entry.score2);
        text.append("\n");
        text.append(new Timestamp(entry.begin));
        text.append("\n");
        text.append(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(entry.length),
                TimeUnit.MILLISECONDS.toSeconds(entry.length) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(entry.length))
        ));
        text.append("\n");
        text.append("\n");

        holder.txtGameEntry.setText(text);
    }

    @Override
    public int getItemCount() {
        return this.gameEntryList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtGameEntry;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtGameEntry = itemView.findViewById(R.id.txtBluetoothDevice);
        }
    }
}
