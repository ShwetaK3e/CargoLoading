package com.shwetak3e.loading.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shwetak3e.loading.MainActivity;
import com.shwetak3e.loading.R;
import com.shwetak3e.loading.adapter.ShipmentAdapter;
import com.shwetak3e.loading.adapter.StopsAdapter;
import com.shwetak3e.loading.model.Truck_1;

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
        Log.i(TAG,"shipment list");
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_truck_details_1, container,false);


        Log.i(TAG,"shipment_list");

        //truck_img=(ImageView)view.findViewById(R.id.truck_img);
        truck_no=(TextView)view.findViewById(R.id.truck_no);
        //driver_name=(TextView)view.findViewById(R.id.driver_name);
        stops_list=(RecyclerView)view.findViewById(R.id.stops_list);
        shipment_list=(RecyclerView)view.findViewById(R.id.shipmemt_list);

         if(AddNewTruck_1.current_truck==null){
             Log.i("ABCD","Its null");
         }else{
             Truck_1 trck=AddNewTruck_1.current_truck;
             Log.i("ABCD","not null"+trck.getId());
         }
        truck_no.setText(AddNewTruck_1.current_truck.getId().toString().trim());
        //driver_name.setText("Ram Lal");
        stops_list.setLayoutManager(new GridLayoutManager(getActivity(),3));
        stops_list.setAdapter(new StopsAdapter(AddNewTruck_1.current_truck.getStops()));

        shipment_list.setLayoutManager(new GridLayoutManager(getActivity(),1));
        shipment_list.setAdapter(new ShipmentAdapter(getActivity(),AddNewTruck_1.current_truck.getShipmentItems(), new ShipmentAdapter.OnItemClickListener() {
            @Override
            public void onClick(String shipment_id) {

            }
        }));

        return view;
    }

















    @Override
    public void onStop() {
        super.onStop();
    }

}
