package com.shwetak3e.loading.adapter;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shwetak3e.loading.R;
import com.shwetak3e.loading.model.Issues;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by shwetakumar on 7/5/17.
 */

public class IssuesAdapter extends RecyclerView.Adapter<IssuesAdapter.ViewHolder>{

    List<Issues> issues =new LinkedList<>();
    Context context;
    OnItemClickListener onItemClickListener;

    public IssuesAdapter(Context context, List<Issues> issues, OnItemClickListener onItemClickListener){
        this.context=context;
        this.issues = issues;
        this.onItemClickListener=onItemClickListener;
    }


    @Override
    public IssuesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.issue_list_grid,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IssuesAdapter.ViewHolder holder, final int position) {


        holder.issue_desc.setText(issues.get(position).getIssueDescription());


        if(issues.get(position).getIssueDescriptionType()==0){
            Glide.with(context).load(Uri.parse("android.resource://com.shwetak3e.loading/" +R.drawable.ic_video)).into(holder.issue_desc_type);
        }else if(issues.get(position).getIssueDescriptionType()==1){
            Glide.with(context).load(Uri.parse("android.resource://com.shwetak3e.loading/" +R.drawable.ic_photo)).into(holder.issue_desc_type);
        }else if(issues.get(position).getIssueDescriptionType()==2){
            Glide.with(context).load(Uri.parse("android.resource://com.shwetak3e.loading/" +R.drawable.ic_text)).into(holder.issue_desc_type);
        }

        if(issues.get(position).getIssueType()==0){
            Glide.with(context).load(Uri.parse("android.resource://com.shwetak3e.loading/" +R.drawable.ic_error)).into(holder.issue_type);
        }else if(issues.get(position).getIssueType()==1){
            Glide.with(context).load(Uri.parse("android.resource://com.shwetak3e.loading/" +R.drawable.ic_missing)).into(holder.issue_type);
        }else if(issues.get(position).getIssueType()==2){
            Glide.with(context).load(Uri.parse("android.resource://com.shwetak3e.loading/" +R.drawable.ic_load)).into(holder.issue_type);
        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(position);
            }
        });



    }

    @Override
    public int getItemCount() {
        return issues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView issue_type;
        ImageView issue_desc_type;
        TextView issue_desc;
        ImageButton see_issue;

        public ViewHolder(View itemView) {
            super(itemView);
           issue_type=(ImageView)itemView.findViewById(R.id.issue_type);
           issue_desc_type=(ImageView)itemView.findViewById(R.id.issue_desc_type);
           issue_desc=(TextView)itemView.findViewById(R.id.issue_desc);
           see_issue=(ImageButton)itemView.findViewById(R.id.see_issue);

        }
    }

    public  interface  OnItemClickListener{
        void onClick(int pos);
    }
}
