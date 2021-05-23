package com.example.projekt.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekt.R;
import com.example.projekt.database.GameEntry;

import java.util.List;


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

    @Override
    public void onBindViewHolder(@NonNull GameEntryListAdapter.MyViewHolder holder, int position) {
        GameEntry entry = this.gameEntryList.get(position);
        String text = entry.player1 + " vs " + entry.player2;
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
