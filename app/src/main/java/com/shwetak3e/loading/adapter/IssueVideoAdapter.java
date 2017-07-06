package com.shwetak3e.loading.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shwetak3e.loading.R;
import com.shwetak3e.loading.model.Issues;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by shwetakumar on 7/5/17.
 */

public class IssueVideoAdapter extends RecyclerView.Adapter<IssueVideoAdapter.ViewHolder>{

    List<Issues> issues =new LinkedList<>();
    Context context;
    OnItemClickListener onItemClickListener;

    public  IssueVideoAdapter(Context context, List<Issues> issues, OnItemClickListener onItemClickListener){
        this.context=context;
        this.issues = issues;
        this.onItemClickListener=onItemClickListener;
    }


    @Override
    public IssueVideoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.issue_video_list_grid,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IssueVideoAdapter.ViewHolder holder, final int position) {

        holder.damageText.setText(issues.get(position).getIssueDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(issues.get(position).getUri());
            }
        });

    }

    @Override
    public int getItemCount() {
        return issues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView damageText;

        public ViewHolder(View itemView) {
            super(itemView);
            damageText=(TextView)itemView.findViewById(R.id.damage_desc);

        }
    }

    public  interface  OnItemClickListener{
        void onClick(String videoUri);
    }
}
