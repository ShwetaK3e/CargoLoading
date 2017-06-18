package com.shwetak3e.loading.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.shwetak3e.loading.IssueVideoActivity;
import com.shwetak3e.loading.MainActivity;
import com.shwetak3e.loading.R;
import com.shwetak3e.loading.adapter.ItemsToBeLoadedAdapter;
import com.shwetak3e.loading.adapter.TruckAdapter;
import com.shwetak3e.loading.model.ShipmentItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class TruckList extends Fragment {


    private static final String TAG =TruckList.class.getSimpleName() ;
    RecyclerView truck_list;
    TruckAdapter truckAdapter;


    LinearLayout add_truck;


    public static TruckList newInstance() {
        TruckList fragment = new TruckList();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_truck_list, container, false);
        add_truck=(LinearLayout)view.findViewById(R.id.add_truck);
        add_truck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), MainActivity.class);
                intent.putExtra("Activity","Add_New_Truck");
                startActivity(intent);
            }
        });

        truck_list=(RecyclerView)view.findViewById(R.id.truckList);
        truckAdapter=new TruckAdapter(getActivity(), new TruckAdapter.OnMyItemClickListener() {
            @Override
            public void onClick() {
                Intent intent=new Intent(getActivity(), MainActivity.class);
                intent.putExtra("Activity","Enter_booking_ID");
                startActivity(intent);
            }
        });
        truck_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        truck_list.setAdapter(truckAdapter);
        return view;
    }



}
