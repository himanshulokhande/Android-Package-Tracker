package com.example.fedexpackagetracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;

public class TrackingListAdapter extends RecyclerView.Adapter<TrackingListAdapter.ViewHolder> {


    ArrayList<PackageShipModel> ls;
    Context context;

    public TrackingListAdapter(Context context, ArrayList<PackageShipModel> list){
        this.context = context;
        this.ls = list;
    }

    @NonNull
    @Override
    public TrackingListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.track_list_structure, parent, false);
        ViewHolder viewHolder = new TrackingListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrackingListAdapter.ViewHolder holder, int position) {
        PackageShipModel ps = ls.get(position);
        holder.center.setText(ps.getCenterName());
        PrettyTime p = new PrettyTime();

        //holder.arrival.setText(ps.getArrivalTime().toString());
        holder.arrival.setText(p.format(ps.getArrivalTime()));
        holder.departure.setText(p.format(ps.getDepartureTime()));
        //holder.departure.setText(ps.getDepartureTime().toString());
    }

    @Override
    public int getItemCount() {
        return ls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView center, arrival, departure;

        ViewHolder(View itemView){
            super(itemView);
            center = itemView.findViewById(R.id.centerNameLabel);
            arrival = itemView.findViewById(R.id.arrivalTimeLabel);
            departure = itemView.findViewById(R.id.departureTimeLabel);
        }
    }
}
