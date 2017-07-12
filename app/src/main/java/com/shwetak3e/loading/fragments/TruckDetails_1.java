package com.shwetak3e.loading.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.shwetak3e.loading.CustomWidgets.SlidingLinearLayout;
import com.shwetak3e.loading.IssueVideoActivity;
import com.shwetak3e.loading.MainActivity;
import com.shwetak3e.loading.R;
import com.shwetak3e.loading.adapter.ShipmentAdapter;
import com.shwetak3e.loading.adapter.StopsAdapter;
import com.shwetak3e.loading.model.Issues;
import com.shwetak3e.loading.model.ShipmentItem;
import com.shwetak3e.loading.model.Truck_1;

import org.w3c.dom.Text;

import java.util.LinkedList;
import java.util.List;


public class TruckDetails_1 extends Fragment {


    private static final String TAG =TruckDetails_1.class.getSimpleName() ;
    private ImageView truck_img;
    private TextView truck_no;
    private TextView driver_name;
    private  Button reenter_truck_id;
    LinearLayout truck_details;
    private RecyclerView stops_list;
    ImageButton see_more_details;

    boolean truck_details_visible=false;
    private RecyclerView shipment_list;
    ShipmentAdapter shipmentAdapter;

    public static ShipmentItem current_item;
    public static int shipment_pos;

    private Dialog itemDialog;



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
        reenter_truck_id=(Button)view.findViewById(R.id.reenter_truck_id);
        reenter_truck_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), MainActivity.class);
                startActivity(i);
            }
        });
        //driver_name=(TextView)view.findViewById(R.id.driver_name);

        truck_details=(LinearLayout)view.findViewById(R.id.truck_details);
        truck_details.setVisibility(View.GONE);
        stops_list=(RecyclerView)view.findViewById(R.id.stops_list);
        see_more_details=(ImageButton)view.findViewById(R.id.see_more_details) ;
        see_more_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!truck_details_visible){
                    truck_details.setVisibility(View.VISIBLE);
                    truck_details_visible=true;
                    see_more_details.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_up_arrow));
                }else{
                    truck_details.setVisibility(View.GONE);
                    truck_details_visible=false;
                    see_more_details.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_drop_arrow_1));
                }

            }
        });
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
        stops_list.setAdapter(new StopsAdapter(getActivity(),AddNewTruck_1.current_truck.getStops()));

        shipment_list.setLayoutManager(new GridLayoutManager(getActivity(),1));
        shipmentAdapter=new ShipmentAdapter(getActivity(),AddNewTruck_1.current_truck.getShipmentItems(), new ShipmentAdapter.OnItemClickListener() {
            @Override
            public void onClick(String shipment_id, int position) {
                current_item=MainActivity.shipments_1.get(shipment_id);
                shipment_pos=position;

                 /*Intent intent=new Intent(getActivity(), MainActivity.class);
                 intent.putExtra("Activity","LOAD_THIS_ITEM");
                 startActivity(intent);*/

                openShipment(MainActivity.shipments_1.get(shipment_id));
            }
        });
        shipment_list.setAdapter(shipmentAdapter);

        if(current_item!=null){
            openShipment(current_item);
        }

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
    }


     boolean other_details_visibility=false;
     int next_loc=1;
     boolean first_pressed=true;
    void openShipment(final ShipmentItem item){
        itemDialog=new Dialog(getActivity(), R.style.MyDialogTheme);
        itemDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        itemDialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        itemDialog.setContentView(R.layout.item_dialog);
        itemDialog.show();
        itemDialog.setCancelable(true);

        final ShipmentItemDialogHolder holder=new ShipmentItemDialogHolder(itemDialog);

        //holder.other_details.setVisibility(View.GONE);
        holder.other_details.animate().alpha(0.0f).translationY(holder.other_details.getHeight());




        Glide.with(getContext()).load(item.getImageUri()).into(holder.commodity_img);
        holder.commodity_name.setText(item.getBookedItem().getCommodityName());
        holder.booking_desc_short.setText(item.getBookedItem().getDescription());
        holder.see_more_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!other_details_visibility){
                    //holder.other_details.setVisibility(View.VISIBLE);
                    holder.other_details.animate().alpha(1.0f).translationY(0).setDuration(300);
                    other_details_visibility=true;
                    holder.see_more_details.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_up_arrow));
                }else{
                    other_details_visibility=false;
                    holder.other_details.animate().alpha(0.0f).translationY(holder.other_details.getHeight()).setDuration(300);
                    holder.see_more_details.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_drop_arrow_1));
                   // holder.other_details.setVisibility(View.GONE);
                }
            }
        });




        holder.inc_load_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setLoadedCount(item.getLoadedCount()+1);
                holder.loading_count.setSelection(holder.loading_count.getText().toString().length());
                holder.loading_count.setText(item.getLoadedCount().toString());

            }
        });
        holder.dec_load_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setLoadedCount(item.getLoadedCount()-1);
                holder.loading_count.setSelection(holder.loading_count.getText().toString().length());
                holder.loading_count.setText(item.getLoadedCount().toString());

            }
        });

        holder.loading_count.setText(item.getLoadedCount().toString());
        if(item.getLoadedCount()+item.getMissing_count()==item.getShippedItemCount()){
            holder.next.setEnabled(true);
            holder.next.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            holder.inc_load_count.setEnabled(false);
            holder.inc_load_count.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_circle_white));
        }else{
            holder.next.setEnabled(false);
            holder.next.setTextColor(getContext().getResources().getColor(R.color.light_grey));
            if(item.getLoadedCount()==0){
                holder.dec_load_count.setEnabled(false);
                holder.dec_load_count.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_circle_white));
            }
        }
        holder.loading_count.setSelection(holder.loading_count.getText().toString().length());
        holder.loading_count.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                 // oldCount=s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int newCount;
                if(s.length()!=0) {
                    newCount=Integer.parseInt(s.toString());
                        if(newCount<0 || newCount +item.getMissing_count()> item.getShippedItemCount()){
                        holder.loading_count.setText(String.valueOf(item.getLoadedCount()));
                        holder.loading_count.setSelection(holder.loading_count.getText().toString().length());
                    }else if (newCount==0){
                        holder.dec_load_count.setEnabled(false);
                        holder.dec_load_count.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_circle_white));
                    }else {
                        holder.loading_count.setSelection(holder.loading_count.getText().toString().length());
                        item.setLoadedCount(newCount);
                        holder.dec_load_count.setEnabled(true);
                        holder.dec_load_count.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_circle_maroon));
                        if(item.getLoadedCount()+item.getMissing_count()==item.getShippedItemCount()){
                            holder.inc_load_count.setEnabled(false);
                            holder.inc_load_count.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_circle_white));
                            holder.next.setEnabled(true);
                            holder.next.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                        }else if(newCount+item.getMissing_count()<item.getShippedItemCount()) {
                            holder.next.setEnabled(true);
                            holder.next.setTextColor(getActivity().getResources().getColor(R.color.light_grey));
                            holder.inc_load_count.setEnabled(true);
                            holder.inc_load_count.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_circle_green));
                        }
                    }
                }else{
                    item.setLoadedCount(0);
                    if(item.getLoadedCount()==0){
                        holder.dec_load_count.setEnabled(false);
                        holder.dec_load_count.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_circle_white));
                    }
                }


            }
        });
        holder.total_count.setText(item.getShippedItemCount().toString());


        final LinkedList<String> item_stops=item.getStops();
        holder.origin.setText(item.getOrigin());
        holder.drop_loc.setText(item.getOrigin());
        if(item_stops.size()!=0 && item.isStopSet() ){
            holder.drop_loc.setText(item_stops.get(item_stops.size()-1));
        }
        holder.nxt_dest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stop=AddNewTruck_1.current_truck.getStops().get(next_loc++);
                holder.drop_loc.setText(stop);
                if(item.isStopSet() && item_stops.size()!=0){
                    item_stops.remove(item_stops.size()-1);
                }
                item.setStopSet(true);
                item_stops.add(stop);
                item.setStops(item_stops);
                if(next_loc >= AddNewTruck_1.current_truck.getStops().size()) {
                   next_loc=1;
                }

            }
        });





        holder.raise_issue_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinkedList<ShipmentItem> item_list=AddNewTruck_1.current_truck.getShipmentItems();
                if(!item_list.isEmpty()) {
                    item_list.remove(shipment_pos);
                    item_list.add(shipment_pos, item);
                    AddNewTruck_1.current_truck.setShipmentItems(item_list);
                }
                shipmentAdapter.notifyDataSetChanged();
                itemDialog.dismiss();
                Intent intent=new Intent(getActivity(), IssueVideoActivity.class);
                intent.putExtra("Shipment_ID",item.getId());
                startActivity(intent);

            }
        });

        holder.go_to_issues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),MainActivity.class);
                intent.putExtra("Activity", "ISSUES");
                intent.putExtra("Shipment_ID",item.getId());
                startActivity(intent);
            }
        });

        LinkedList<Issues> issue_list=(LinkedList)item.getIssues();
        String count;
        if(issue_list!=null) {
           count=String.valueOf(issue_list.size());
        }else{
            count="0";
        }

            holder.issue_nos.setText(count);


        holder.skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinkedList<ShipmentItem> item_list=AddNewTruck_1.current_truck.getShipmentItems();
                if(!item_list.isEmpty()) {
                    item_list.remove(shipment_pos);
                    item_list.add(shipment_pos, item);
                    AddNewTruck_1.current_truck.setShipmentItems(item_list);
                }
                shipmentAdapter.notifyDataSetChanged();
                itemDialog.dismiss();
            }
        });

        holder.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinkedList<ShipmentItem> item_list=AddNewTruck_1.current_truck.getShipmentItems();
                item.setStatus(0);
                int count=item_list.size();
                if(!item_list.isEmpty()) {
                    item_list.remove(shipment_pos);
                    item_list.add(count-1,item);
                    AddNewTruck_1.current_truck.setShipmentItems(item_list);
                }
                itemDialog.dismiss();
                shipmentAdapter.notifyDataSetChanged();
                current_item=AddNewTruck_1.current_truck.getShipmentItems().get(++shipment_pos);
                openShipment(current_item);
            }
        });











    }

    class ShipmentItemDialogHolder{

        ImageView commodity_img;
        ImageButton see_more_details;
        TextView  commodity_name;
        TextView  booking_desc_short;


        ImageButton inc_load_count;
        ImageButton dec_load_count;
        EditText loading_count;
        TextView  total_count;


        TextView origin;
        TextView drop_loc;
        ImageButton nxt_dest;


        LinearLayout raise_issue_layout;
        RelativeLayout go_to_issues;
        TextView issue_nos;

        Button skip;
        Button next;


        TextView booking_desc;
        TextView  commodity_length;
        TextView  commodity_width;
        TextView  commodity_height;
        TextView  commodity_weight;
        SlidingLinearLayout other_details;




        ShipmentItemDialogHolder(Dialog dialog){
            commodity_img=(ImageView)dialog.findViewById(R.id.commodity_img);
            commodity_name=(TextView)dialog.findViewById(R.id.commodity_name);
            booking_desc_short=(TextView)dialog.findViewById(R.id.booking_desc_short);
            see_more_details=(ImageButton)dialog.findViewById(R.id.see_more_details);


            inc_load_count=(ImageButton)dialog.findViewById(R.id.inc_load_count);


            dec_load_count=(ImageButton)dialog.findViewById(R.id.dec_load_count);
            dec_load_count.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TruckDetails_1.current_item.setLoadedCount(TruckDetails_1.current_item.getLoadedCount()-1);
                    loading_count.setText(TruckDetails_1.current_item.getLoadedCount().toString());
                }
            });

            loading_count=(EditText) dialog.findViewById(R.id.loading_count);
            total_count=(TextView)dialog.findViewById(R.id.total_count);


            origin=(TextView)dialog.findViewById(R.id.origin);
            drop_loc=(TextView)dialog.findViewById(R.id.drop_loc);
            nxt_dest=(ImageButton)dialog.findViewById(R.id.next_dest);


            go_to_issues=(RelativeLayout)dialog.findViewById(R.id.go_to_issues);
            issue_nos=(TextView)dialog.findViewById(R.id.issue_nos) ;

            skip=(Button)dialog.findViewById(R.id.skip);
            next=(Button)dialog.findViewById(R.id.next);


            other_details=(SlidingLinearLayout) dialog.findViewById(R.id.other_details);
            //other_details=new SlidingLinearLayout(getContext(),R.id.other_details);
            booking_desc=(TextView)dialog.findViewById(R.id.booking_desc);
            //commodity_name.setText(TruckDetails_1.current_item.getBookedItem().getCommodityName());
            commodity_length=(TextView)dialog.findViewById(R.id.commodity_length);
            //commodity_length.setText(TruckDetails_1.current_item.getBookedItem().getLength().toString());
            commodity_width=(TextView)dialog.findViewById(R.id.commodity_width);
            //commodity_width.setText(TruckDetails_1.current_item.getBookedItem().getWidth().toString());
            commodity_height=(TextView)dialog.findViewById(R.id.commodity_height);
            //commodity_height.setText(TruckDetails_1.current_item.getBookedItem().getHeight().toString());
            commodity_weight=(TextView)dialog.findViewById(R.id.commodity_weight);
            //commodity_weight.setText(TruckDetails_1.current_item.getBookedItem().getActualWeight().toString());


            //booking_desc.setText(TruckDetails_1.current_item.getBookedItem().getDescription().toString());

            //loading_count.setText(TruckDetails_1.current_item.getLoadedCount().toString());

            raise_issue_layout=(LinearLayout)dialog.findViewById(R.id.raise_issue_layout);
        }
    }

}
