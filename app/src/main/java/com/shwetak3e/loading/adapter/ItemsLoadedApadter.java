package com.shwetak3e.loading.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shwetak3e.loading.R;
import com.shwetak3e.loading.model.Booking;
import com.shwetak3e.loading.model.ShipmentItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.shwetak3e.loading.MainActivity.bookings;


public class ItemsLoadedApadter extends RecyclerView.Adapter<ItemsLoadedApadter.ViewHolder> {


    private List<ShipmentItem>shipmentItemS=new ArrayList<>();


    private Context context;



    public ItemsLoadedApadter(Context context) {
        this.context=context;
        for(Map.Entry<Integer,Booking> entry :bookings.entrySet()) {
            int key = entry.getKey();
            Booking booking = bookings.get(key);
            List<ShipmentItem> shipmentItems = booking.getItems();
            for (ShipmentItem shipmentItem:shipmentItems){
                Log.i("ABCD",shipmentItem.getBookedItem().getCommodityName()+":::::::"+shipmentItem.getShippedItemCount()+"------- "+shipmentItem.getUnloadedCount());
                if(shipmentItem.getShippedItemCount()!=shipmentItem.getUnloadedCount()){
                    Log.i("ABCD",shipmentItem.getBookedItem().getCommodityName()+":::::::"+shipmentItem.getShippedItemCount()+"------- "+shipmentItem.getUnloadedCount());
                    shipmentItemS.add(shipmentItem);
                }
            }

        }


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loaded,parent,false);
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
        final String booking_id=shipmentItemS.get(position).getId().toString().split("_")[0];
        final int shipped_count=shipmentItemS.get(position).getShippedItemCount();
        final int loaded_count=shipmentItemS.get(position).getLoadedCount();
        final int damaged_count=shipmentItemS.get(position).getDamaged_count();
        final int missinh_count=shipmentItemS.get(position).getMissing_count();

        Glide.with(context).load(shipmentItemS.get(position).getImageUri()).into(holder.commodity_icon);
        holder.commodity_name.setText(shipmentItemS.get(position).getBookedItem().getCommodityName());
        holder.unloaded_count.setText(String.valueOf(shipped_count));
        holder.desc.setText(shipmentItemS.get(position).getBookedItem().getDescription());
        holder.damaged_count.setText(String.valueOf(damaged_count));
        holder.missing_count.setText(String.valueOf(missinh_count));
        holder.loaded_count.setText(String.valueOf(loaded_count));
        holder.booking_id.setText(booking_id);

        String complete_URI=new String();
        if(shipmentItemS.get(position).getStatus()==0){
           complete_URI= Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_complete).toString();
        }else if(shipmentItemS.get(position).getStatus()==1){
            complete_URI= Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_incomplete).toString();
        }
        Glide.with(context).load(complete_URI).into(holder.complete_status);

        String damage_URI=new String();
        if(shipmentItemS.get(position).getDamagedStatus()){
            damage_URI= Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_error).toString();
        }else {
            damage_URI= Uri.parse("").toString();
        }
        Glide.with(context).load(damage_URI).into(holder.error_status);


        String same_truck_URI=new String();
        if(shipmentItemS.get(position).getSame_truck_status()){
            same_truck_URI= Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_truck).toString();
        }else {
            same_truck_URI= Uri.parse("").toString();
        }
        Glide.with(context).load(same_truck_URI).into(holder.same_truck_status);









    }

    @Override
    public int getItemCount() {
        return shipmentItemS.size();
    }




    static abstract class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView booking_id;
        ImageView commodity_icon;
        TextView commodity_name;
        TextView unloaded_count;
        TextView loaded_count;
        TextView damaged_count;
        TextView missing_count;
        TextView desc;
        ImageView error_status;
        ImageView complete_status;
        ImageView same_truck_status;



        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            booking_id=(TextView)itemView.findViewById(R.id.booking_id_loaded);
            commodity_icon=(ImageView)itemView.findViewById(R.id.commodity_img);
            commodity_name=(TextView)itemView.findViewById(R.id.commodity_name);
            unloaded_count=(TextView)itemView.findViewById(R.id.shipped_count);
            desc=(TextView)itemView.findViewById(R.id.booking_desc);
            error_status=(ImageView) itemView.findViewById(R.id.damaged_status);
            loaded_count=(TextView) itemView.findViewById(R.id.count_loaded);
            complete_status=(ImageView)itemView.findViewById(R.id.status);
            damaged_count=(TextView)itemView.findViewById(R.id.damaged_count);
            missing_count=(TextView)itemView.findViewById(R.id.missing_count);
            same_truck_status=(ImageView)itemView.findViewById(R.id.same_truck_status);
        }



    }




}
