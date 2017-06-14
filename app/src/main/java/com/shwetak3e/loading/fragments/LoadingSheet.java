package com.shwetak3e.loading.fragments;

import android.app.Dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shwetak3e.loading.R;
import com.shwetak3e.loading.adapter.ItemsLoadedApadter;
import com.shwetak3e.loading.model.Booking;
import com.shwetak3e.loading.model.ShipmentItem;

import java.util.ArrayList;
import java.util.List;


import static com.shwetak3e.loading.MainActivity.bookings;


public class LoadingSheet extends Fragment {


    RecyclerView item_loaded;
    public static List<ShipmentItem> booked_shipmentItems=new ArrayList<>();
    ItemsLoadedApadter adapter;


    public static LoadingSheet newInstance() {
        Log.i("ABCD","new Instance");
        LoadingSheet fragment = new LoadingSheet();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_loading_sheet, container, false);

        Log.i("ABCD","new Instance");
        item_loaded = (RecyclerView) view.findViewById(R.id.items_loaded);
        item_loaded.setLayoutManager(new GridLayoutManager(getActivity(),1));
        adapter=new ItemsLoadedApadter(getActivity());
        item_loaded.setAdapter(adapter);


        return view;
    }


}
