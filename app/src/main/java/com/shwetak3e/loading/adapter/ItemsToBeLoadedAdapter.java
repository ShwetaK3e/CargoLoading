package com.shwetak3e.loading.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shwetak3e.loading.R;
import com.shwetak3e.loading.model.ShipmentItem;

import java.util.ArrayList;
import java.util.List;

import static com.shwetak3e.loading.fragments.LoadItems.booked_shipmentItems;


public class ItemsToBeLoadedAdapter extends RecyclerView.Adapter<ItemsToBeLoadedAdapter.ViewHolder> {

    private List<ShipmentItem> shipmentItems=new ArrayList<>();


    private Context context;
    OnMyItemClickListener onMyItemClickListener;


    public ItemsToBeLoadedAdapter(Context context, OnMyItemClickListener onMyItemClickListener) {
        this.context=context;
        this.shipmentItems=booked_shipmentItems;
        this.onMyItemClickListener=onMyItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_to_load,parent,false);
        final ViewHolder holder= new ViewHolder(itemView) {
            @Override
            public void onClick(View v) {

            }
        };
        return holder;
    }


    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final int shipped_nos= shipmentItems.get(position).getShippedItemCount();
        final int unloaded_count=shipmentItems.get(position).getUnloadedCount();
        final int loaded_count=shipmentItems.get(position).getLoadedCount();
        final int damaged_count=shipmentItems.get(position).getDamaged_count();
        final int missinh_count=shipmentItems.get(position).getMissing_count();

        Glide.with(context).load(shipmentItems.get(position).getImageUri()).into(holder.commodity_icon);
        holder.commodity_name.setText(shipmentItems.get(position).getBookedItem().getCommodityName());
        holder.unloaded_count.setText(String.valueOf(unloaded_count));
        holder.desc.setText(shipmentItems.get(position).getBookedItem().getDescription());
        holder.damaged_count.setText(String.valueOf(damaged_count));
        holder.missing_count.setText(String.valueOf(missinh_count));


        String complete_URI=new String();
        if(shipmentItems.get(position).getStatus()==0){
           complete_URI= Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_complete).toString();
        }else if(shipmentItems.get(position).getStatus()==1){
            complete_URI= Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_incomplete).toString();
        }
        Glide.with(context).load(complete_URI).into(holder.status);

        String damage_URI=new String();
        if(shipmentItems.get(position).getDamagedStatus()){
            damage_URI= Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_error).toString();
        }else {
            damage_URI= Uri.parse("").toString();
        }
        Glide.with(context).load(damage_URI).into(holder.error_status);


        String same_truck_URI=new String();
        if(shipmentItems.get(position).getSame_truck_status()){
            same_truck_URI= Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_truck).toString();
        }else {
            same_truck_URI= Uri.parse("").toString();
        }
        Glide.with(context).load(same_truck_URI).into(holder.same_truck_status);


        holder.decrease_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (loaded_count > 0 ){
                    holder.loaded_count.setText(String.valueOf(loaded_count - 1));
                    onMyItemClickListener.onClick(position, "desc","loading");
                 }
            }
        });

        holder.increase_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(unloaded_count>0 && loaded_count+damaged_count+missinh_count+unloaded_count==shipped_nos) {
                    holder.loaded_count.setText(String.valueOf(loaded_count+1));
                    onMyItemClickListener.onClick(position, "inc","loading");
                }
                if(unloaded_count==0){
                    onMyItemClickListener.onClick(position, "inc","loading");
                }
            }
        });


        holder.decrease_damage_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (damaged_count> 0){
                    Log.i("ABCD", "damage1");

                    holder.damaged_count.setText(String.valueOf(damaged_count-1));
                    onMyItemClickListener.onClick(position, "desc","damage");
                }
            }
        });

        holder.increase_damage_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(unloaded_count>0 && loaded_count+damaged_count+missinh_count+unloaded_count==shipped_nos) {
                    Log.i("ABCD", "damage2");
                    holder.damaged_count.setText(String.valueOf(damaged_count+1));
                    onMyItemClickListener.onClick(position, "inc", "damage");
                }

            }
        });


        holder.decrease_missing_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (missinh_count> 0){
                    Log.i("ABCD", "missing1");
                    holder.missing_count.setText(String.valueOf(missinh_count-1));
                    onMyItemClickListener.onClick(position, "desc","missing");
                }
            }
        });

        holder.increase_missing_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ABCD", "missing2");

                if(unloaded_count>0 && loaded_count+damaged_count+missinh_count+unloaded_count==shipped_nos) {
                    holder.missing_count.setText(String.valueOf(missinh_count+1));
                    onMyItemClickListener.onClick(position, "inc", "missing");
                }

            }
        });




    }

    @Override
    public int getItemCount() {
        return shipmentItems.size();
    }




    static abstract class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView commodity_icon;
        TextView commodity_name;
        TextView unloaded_count;
        TextView loaded_count;
        TextView damaged_count;
        TextView missing_count;
        TextView desc;
        ImageButton decrease_load;
        ImageButton increase_load;
        ImageButton decrease_damage_count;
        ImageButton increase_damage_count;
        ImageButton decrease_missing_count;
        ImageButton increase_missing_count;
        ImageView error_status;
        ImageView status;
        ImageView same_truck_status;



        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            commodity_icon=(ImageView)itemView.findViewById(R.id.commodity_img);
            commodity_name=(TextView)itemView.findViewById(R.id.commodity_name);
            unloaded_count=(TextView)itemView.findViewById(R.id.shipped_count);
            desc=(TextView)itemView.findViewById(R.id.booking_desc);
            decrease_load=(ImageButton) itemView.findViewById(R.id.decrease_count);
            increase_load=(ImageButton) itemView.findViewById(R.id.increase_count);
            error_status=(ImageView) itemView.findViewById(R.id.damaged_status);
            loaded_count=(TextView) itemView.findViewById(R.id.loaded_count);
            status=(ImageView)itemView.findViewById(R.id.status);
            decrease_damage_count=(ImageButton) itemView.findViewById(R.id.decrease_damage_count);
            increase_damage_count=(ImageButton) itemView.findViewById(R.id.increase_damaged_count);
            decrease_missing_count=(ImageButton) itemView.findViewById(R.id.decrease_missing_count);
            increase_missing_count=(ImageButton) itemView.findViewById(R.id.increase_missing_count);
            damaged_count=(TextView)itemView.findViewById(R.id.damaged_count);
            missing_count=(TextView)itemView.findViewById(R.id.missing_count);
            same_truck_status=(ImageView)itemView.findViewById(R.id.same_truck_status);
        }



    }

    public interface OnMyItemClickListener{
        void onClick(int pos, String order, String type);
    }


}
