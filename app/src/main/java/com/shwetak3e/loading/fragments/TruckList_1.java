package com.shwetak3e.loading.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.shwetak3e.loading.MainActivity;
import com.shwetak3e.loading.R;
import com.shwetak3e.loading.adapter.TruckAdapter;


public class TruckList_1 extends Fragment {


    private static final String TAG =TruckList_1.class.getSimpleName() ;
    RecyclerView truck_list;
    TruckAdapter truckAdapter;


    LinearLayout add_truck;


    public static TruckList_1 newInstance() {
        TruckList_1 fragment = new TruckList_1();
        AddNewTruck_1.current_truck=null;
        TruckDetails_1.current_item=null;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_truck_list, container, false);
        MainActivity.editTruck=false;
        add_truck=(LinearLayout)view.findViewById(R.id.add_truck);
        add_truck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), MainActivity.class);

                startActivity(intent);
            }
        });

        truck_list=(RecyclerView)view.findViewById(R.id.truckList);
        truckAdapter=new TruckAdapter(getActivity(), new TruckAdapter.OnMyItemClickListener() {
            @Override
            public void onClick( String truck_ID) {
                AddNewTruck_1.current_truck=MainActivity.trucks_1.get(truck_ID);
                Intent intent=new Intent(getActivity(), MainActivity.class);
                intent.putExtra("Activity","TRUCK_DETAILS_1");
                intent.putExtra("SHOW_ISSUE",true);
                startActivity(intent);
            }
        });
        truck_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        truck_list.setAdapter(truckAdapter);
        return view;
    }



}
