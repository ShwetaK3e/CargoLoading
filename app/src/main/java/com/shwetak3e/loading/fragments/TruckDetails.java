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
import com.shwetak3e.loading.model.ShipmentItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.shwetak3e.loading.R.id.damage_layout;
import static com.shwetak3e.loading.R.id.drop_loc;


public class TruckDetails extends Fragment {


    private static final String TAG =TruckDetails.class.getSimpleName() ;
    private LinearLayout enter_booking_id;
    private LinearLayout truck_details;


    public static TruckDetails newInstance() {
        TruckDetails fragment = new TruckDetails();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_truck_details, container, false);


        enter_booking_id=(LinearLayout)view.findViewById(R.id.enter_booking_id);
        enter_booking_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                i.putExtra("Activity","Enter_booking_ID");
                startActivity(i);
            }
        });
        truck_details=(LinearLayout)view.findViewById(R.id.truck_details);
        truck_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                i.putExtra("Activity","Truck_Details");
                startActivity(i);
            }
        });


        return view;
    }

















    @Override
    public void onStop() {
        super.onStop();
    }

}
