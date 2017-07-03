package com.shwetak3e.loading.adapter;

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

    public StopsAdapter(List<String> stops) {
        this.stops=stops;
    }


    @Override
    public StopsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.drop_loc_grid,parent);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StopsAdapter.ViewHolder holder, int position) {

        holder.stop.setText(stops.get(position));

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
