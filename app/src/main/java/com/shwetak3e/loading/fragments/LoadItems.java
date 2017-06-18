package com.shwetak3e.loading.fragments;

import android.app.Dialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shwetak3e.loading.IssueVideoActivity;
import com.shwetak3e.loading.R;
import com.shwetak3e.loading.adapter.ItemsToBeLoadedAdapter;
import com.shwetak3e.loading.model.Booking;
import com.shwetak3e.loading.model.ShipmentItem;

import java.util.ArrayList;
import java.util.List;


import static com.shwetak3e.loading.MainActivity.bookings;


public class LoadItems extends Fragment {


    RecyclerView item_to_be_loaded_list;
    public static List<ShipmentItem> booked_shipmentItems=new ArrayList<>();
    public static List<Integer> keyList=new ArrayList<>();
    ItemsToBeLoadedAdapter adapter;


    TextView booking_id;
    LinearLayout new_bookiing_id;


    public static LoadItems newInstance() {
        LoadItems fragment = new LoadItems();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.load_items_fragment, container, false);

        booking_id=(TextView)view.findViewById(R.id.booking_id);
        new_bookiing_id=(LinearLayout)view.findViewById(R.id.new_booking_id);
        new_bookiing_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterBookingIdDialog();
            }
        });


        item_to_be_loaded_list = (RecyclerView) view.findViewById(R.id.items_loaded);
        item_to_be_loaded_list.setLayoutManager(new GridLayoutManager(getActivity(),1));


        enterBookingIdDialog();

        return view;
    }


    public void enterBookingIdDialog() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.booking_success_dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        final EditText enter_booking_id=(EditText)dialog.findViewById(R.id.enter_booking_id);
        Button find_booking = (Button) dialog.findViewById(R.id.confirm_booking);

        find_booking.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                final int bookingID=Integer.parseInt(enter_booking_id.getText().toString().trim());

                booked_shipmentItems=bookings.get(bookingID).getItems();
                booking_id.setText(String.valueOf(bookingID));
                adapter=new ItemsToBeLoadedAdapter(getActivity(), new ItemsToBeLoadedAdapter.OnMyItemClickListener() {
                    @Override
                    public void onClick(int pos, String order, String type) {
                        ShipmentItem shipmentItem = booked_shipmentItems.get(pos);
                        booked_shipmentItems.remove(shipmentItem);
                        if ("loading".equalsIgnoreCase(type)) {
                            int loaded_count = shipmentItem.getLoadedCount();
                            int umloaded_count = shipmentItem.getUnloadedCount();
                            if ("desc".equalsIgnoreCase(order)) {
                                if (loaded_count > 0) {
                                    loaded_count--;
                                    umloaded_count++;
                                    if (umloaded_count > 0) {
                                        shipmentItem.setStatus(1);
                                        shipmentItem.setSame_truck_status(false);
                                        shipmentItem.setSame_truck_status(false);
                                    }
                                    shipmentItem.setUnloadedCount(umloaded_count);
                                    shipmentItem.setLoadedCount(loaded_count);
                                }
                            } else if ("inc".equalsIgnoreCase(order)) {
                                if (umloaded_count >= 0) {
                                    loaded_count++;
                                    umloaded_count--;
                                    if (umloaded_count == 0) {
                                        shipmentItem.setStatus(0);
                                        shipmentItem.setSame_truck_status(true);
                                    }
                                    shipmentItem.setUnloadedCount(umloaded_count);
                                    shipmentItem.setLoadedCount(loaded_count);

                                }

                            }
                        }else if("damage".equalsIgnoreCase(type)){
                            int damaged_count = shipmentItem.getDamaged_count();
                            int missing_count=shipmentItem.getMissing_count();
                            int umloaded_count = shipmentItem.getUnloadedCount();
                            if ("desc".equalsIgnoreCase(order)) {
                                if (damaged_count > 0) {
                                    damaged_count--;
                                    umloaded_count++;
                                    if (umloaded_count > 0) {
                                        shipmentItem.setStatus(1);
                                        shipmentItem.setSame_truck_status(false);
                                    }
                                    if(damaged_count==0 && missing_count==0){
                                        shipmentItem.setDamagedStatus(false);
                                    }
                                    shipmentItem.setUnloadedCount(umloaded_count);
                                    shipmentItem.setDamaged_count(damaged_count);

                                }
                            } else if ("inc".equalsIgnoreCase(order)) {
                                if (umloaded_count >= 0) {
                                    damaged_count++;
                                    umloaded_count--;
                                    if (umloaded_count == 0) {
                                        shipmentItem.setStatus(0);
                                        shipmentItem.setSame_truck_status(true);
                                    }
                                    if(damaged_count>0){
                                        shipmentItem.setDamagedStatus(true);
                                    }
                                    shipmentItem.setUnloadedCount(umloaded_count);
                                    shipmentItem.setDamaged_count(damaged_count);

                                }
                              startActivity(new Intent(getActivity(), IssueVideoActivity.class));
                            }

                        }else if("missing".equalsIgnoreCase(type)){
                            int missing_count =shipmentItem.getMissing_count();
                            int umloaded_count = shipmentItem.getUnloadedCount();
                            int damaged_count = shipmentItem.getDamaged_count();
                            if ("desc".equalsIgnoreCase(order)) {
                                if (missing_count > 0) {
                                    missing_count--;
                                    umloaded_count++;
                                    if (umloaded_count > 0) {
                                        shipmentItem.setStatus(1);
                                        shipmentItem.setSame_truck_status(false);
                                    }
                                    if(missing_count==0 && damaged_count==0){

                                            shipmentItem.setDamagedStatus(false);

                                    }
                                    shipmentItem.setUnloadedCount(umloaded_count);
                                    shipmentItem.setMissing_count(missing_count);
                                }
                            } else if ("inc".equalsIgnoreCase(order)) {
                                if (umloaded_count >= 0) {
                                    missing_count++;
                                    umloaded_count--;
                                    if (umloaded_count == 0) {
                                        shipmentItem.setStatus(0);
                                        shipmentItem.setSame_truck_status(true);
                                    }
                                    if(missing_count>0){

                                        shipmentItem.setDamagedStatus(true);

                                    }
                                    shipmentItem.setUnloadedCount(umloaded_count);
                                    shipmentItem.setMissing_count(missing_count);

                                }

                            }
                        }


                        booked_shipmentItems.add(pos, shipmentItem);
                        Booking booking = bookings.get(bookingID);
                        bookings.remove(bookingID);
                        booking.setItems(booked_shipmentItems);
                        bookings.put(bookingID, booking);
                        adapter.notifyDataSetChanged();
                    }

                });
                item_to_be_loaded_list.setAdapter(adapter);

            }
        });
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
    }




}
