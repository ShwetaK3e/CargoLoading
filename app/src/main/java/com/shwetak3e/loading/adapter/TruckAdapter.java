package com.shwetak3e.loading.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shwetak3e.loading.MainActivity;
import com.shwetak3e.loading.R;
import com.shwetak3e.loading.model.Booking;
import com.shwetak3e.loading.model.ShipmentItem;
import com.shwetak3e.loading.model.Truck;
import com.shwetak3e.loading.model.Truck_1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.shwetak3e.loading.MainActivity.bookings;
import static com.shwetak3e.loading.MainActivity.current_truck;
import static com.shwetak3e.loading.MainActivity.trucks;


public class TruckAdapter extends RecyclerView.Adapter<TruckAdapter.ViewHolder> {


    private Context context;
    private OnMyItemClickListener onMyItemClickListener;


    public TruckAdapter(Context context, OnMyItemClickListener onMyItemClickListener) {
        this.context=context;
        this.onMyItemClickListener=onMyItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.truck_list_grid,parent,false);
        final ViewHolder holder= new ViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Id=holder.truck_id.getText().toString().trim();
                onMyItemClickListener.onClick(Id);
            }
        });
        return holder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.truck_id.setText(MainActivity.trucks.get(position).getId());
       // holder.driver_name.setText(MainActivity.trucks.get(position).getDriver_name());

        holder.origin.setText(MainActivity.trucks.get(position).getOrigin());
        holder.dest.setText(MainActivity.trucks.get(position).getDestination());

    }

    @Override
    public int getItemCount() {
        return MainActivity.trucks.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder  {

        TextView truck_id;
        TextView driver_name;
        TextView origin;
        TextView dest;





        public ViewHolder(View itemView) {
            super(itemView);
            truck_id=(TextView)itemView.findViewById(R.id.truck_id);
           // driver_name=(TextView)itemView.findViewById(R.id.driver_name);
            origin=(TextView)itemView.findViewById(R.id.origin);
            dest=(TextView)itemView.findViewById(R.id.dest);

        }


    }

    public interface OnMyItemClickListener{
        void onClick(String truck_ID);
    }


}
