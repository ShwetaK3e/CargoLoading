package com.shwetak3e.loading.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Pervacio on 7/3/2017.
 */

public class ShipmentAdapter extends RecyclerView.Adapter<ShipmentAdapter.ViewHolder> {

    List<ShipmentItem> shipmentItems=new LinkedList<>();
    Context context;
    OnItemClickListener onItemClickListener;

    public ShipmentAdapter(Context context,List<ShipmentItem> shipmentItems,OnItemClickListener onItemclickListener){
        this.shipmentItems=shipmentItems;
        this.context=context;
        this.onItemClickListener=onItemclickListener;

    }

    @Override
    public ShipmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.shipment_list_grid,null);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ShipmentAdapter.ViewHolder holder, final int position) {
        if(shipmentItems.get(position).getStatus()==0){
            holder.shipment_id.setTextColor(context.getResources().getColor(R.color.light_grey));
            holder.loading_status.setImageResource(R.drawable.ic_complete);
            holder.itemView.setEnabled(false);
        }
        if(shipmentItems.get(position).getDamagedStatus()){
            holder.damage_status.setVisibility(View.VISIBLE);
        }else{
            holder.damage_status.setVisibility(View.INVISIBLE);
        }
        Glide.with(context).load(shipmentItems.get(position).getImageUri()).into(holder.commodity_img);
        holder.shipment_id.setText(shipmentItems.get(position).getId());
        holder.total_count.setText(shipmentItems.get(position).getShippedItemCount().toString());
        holder.loaded_count.setText(shipmentItems.get(position).getLoadedCount().toString());
        holder.missing_count.setText(shipmentItems.get(position).getMissing_count().toString());
        holder.destination.setText(shipmentItems.get(position).getDestination());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(shipmentItems.get(position).getId(), position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return shipmentItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView commodity_img;
        ImageView loading_status;
        ImageView damage_status;
        TextView shipment_id;
        TextView total_count;
        TextView loaded_count;
        TextView missing_count;
        TextView destination;
        ImageButton load_shipment;


        public ViewHolder(View itemView) {
            super(itemView);
            commodity_img=(ImageView)itemView.findViewById(R.id.commodity_pic);
            loading_status=(ImageView)itemView.findViewById(R.id.loading_status);
            damage_status=(ImageView)itemView.findViewById(R.id.damaged_status);
            shipment_id=(TextView)itemView.findViewById(R.id.shipment_id);
            total_count=(TextView)itemView.findViewById(R.id.total_count);
            loaded_count=(TextView)itemView.findViewById(R.id.loaded_count);
            missing_count=(TextView)itemView.findViewById(R.id.missing_count);
            destination=(TextView)itemView.findViewById(R.id.destination);
            load_shipment=(ImageButton) itemView.findViewById(R.id.load_shipment);
        }
    }

    public interface OnItemClickListener{
        void onClick(String shipment_id, int pos);
    }
}
