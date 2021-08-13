package com.trust.tournamentdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trust.tournamentdemo.R;
import com.trust.tournamentdemo.activiy.MainActivity;
import com.trust.tournamentdemo.model.TournamentsModel;
import com.trust.tournamentdemo.model.WinnerModel;

import java.util.ArrayList;

public class WinnerAdapter extends RecyclerView.Adapter<WinnerAdapter.ViewHolder> {
    Context context;
    ArrayList<WinnerModel> winnerModelArrayList;

    public WinnerAdapter(Context context, ArrayList<WinnerModel> winnerModelArrayList) {
        this.context = context;
        this.winnerModelArrayList = winnerModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.winner_layout, parent, false);
        return new WinnerAdapter.ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int posRevers = winnerModelArrayList.size() - (position + 1);
        holder.tournamentName.setText(winnerModelArrayList.get(posRevers).getTournamentName()+" "+winnerModelArrayList.get(posRevers).getTournamentNo());
        holder.winnerName.setText(winnerModelArrayList.get(posRevers).getWinnerName());
    }

    @Override
    public int getItemCount() {
        return winnerModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tournamentName,winnerName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tournamentName=itemView.findViewById(R.id.tournamentName);
            winnerName=itemView.findViewById(R.id.winnerName);
        }
    }

}
