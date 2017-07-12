package com.shwetak3e.loading.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shwetak3e.loading.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pervacio on 7/3/2017.
 */

public class StopsAdapter extends RecyclerView.Adapter<StopsAdapter.ViewHolder> {

    List<String> stops=new ArrayList<>();
    Context context;

    public StopsAdapter(Context context, List<String> stops) {
        this.context=context;
        this.stops=stops;
    }



    @Override
    public StopsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.drop_loc_grid,null);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StopsAdapter.ViewHolder holder, int position) {

        if(position==0){
            holder.stop.setTextColor(context.getResources().getColor(R.color.white));
            holder.stop.setBackground(context.getResources().getDrawable(R.drawable.bg_rounded_square_holder_green));
            holder.stop.setPadding(20,20,20,20);
        }else if(position==stops.size()-1){

            holder.stop.setBackground(context.getResources().getDrawable(R.drawable.bg_selected_category_holder_grey));
            holder.stop.setPadding(20,20,20,20);  // set paddng after bg setting
        }

        holder.stop.setText(stops.get(position)+ addForwardArrow(position));

    }

    private String addForwardArrow(int pos){
        if(pos==stops.size()-1){
            return "";
        }
        return " >";
    }

    @Override
    public int getItemCount() {
        return stops.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView stop;
        public ViewHolder(View itemView) {
            super(itemView);
            stop=(TextView)itemView.findViewById(R.id.stop);

        }
    }
}
