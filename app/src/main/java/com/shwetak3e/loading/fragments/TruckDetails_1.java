package com.shwetak3e.loading.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shwetak3e.loading.MainActivity;
import com.shwetak3e.loading.R;

import org.w3c.dom.Text;


public class TruckDetails_1 extends Fragment {


    private static final String TAG =TruckDetails_1.class.getSimpleName() ;
    private ImageView truck_img;
    private TextView truck_no;
    private TextView driver_name;
    private RecyclerView stops_list;
    private RecyclerView shipment_list;



    public static TruckDetails_1 newInstance() {
        TruckDetails_1 fragment = new TruckDetails_1();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_truck_details_1, container, false);

        truck_img=(ImageView)view.findViewById(R.id.truck_img);
        truck_no=(TextView)view.findViewById(R.id.truck_id);
        driver_name=(TextView)view.findViewById(R.id.driver_name);

        stops_list=(RecyclerView)view.findViewById(R.id.stops_list);


        shipment_list=(RecyclerView)view.findViewById(R.id.shipmemt_list);








        return view;
    }

















    @Override
    public void onStop() {
        super.onStop();
    }

}
