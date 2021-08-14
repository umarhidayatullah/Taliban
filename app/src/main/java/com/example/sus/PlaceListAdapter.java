package com.example.sus;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.places.PlaceBuffer;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.PlaceViewHolder> {
    private Context context;
    private PlaceBuffer place;

    public PlaceListAdapter(Context context, PlaceBuffer places) {
        this.context = context;
        this.place = places;
    }

    @NonNull
    @Override
    public PlaceListAdapter.PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceListAdapter.PlaceViewHolder holder, int position) {
        String placeName = place.get(position).getName().toString();
        String placeAddress = place.get(position).getAddress().toString();
        holder.tvName.setText(placeName);
        holder.tvAddress.setText(placeAddress);
    }

    public void swapPlaces(PlaceBuffer newPlaces) {
        place = newPlaces;
        if(place != null) {
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        if(place == null) return 0;
        return place.getCount();
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress;


        public PlaceViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
        }
    }
}
