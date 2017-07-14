package com.shwetak3e.loading.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
    private TextView info;
    private  Button reenter_truck_id;
    LinearLayout truck_details;
    private RecyclerView stops_list;
    ImageButton see_more_details;

    boolean truck_details_visible=false;
    TextView title;
    private RecyclerView shipment_list;
    ShipmentAdapter shipmentAdapter;

    FloatingActionButton show_issue;
    boolean show_issue_value;

    public static ShipmentItem current_item;
    public static int shipment_pos;

    private Dialog itemDialog;



    public static TruckDetails_1 newInstance() {
        TruckDetails_1 fragment = new TruckDetails_1();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_truck_details_1, container,false);


        show_issue_value=getActivity().getIntent().getBooleanExtra("SHOW_ISSUE",false);

        //truck_img=(ImageView)view.findViewById(R.id.truck_img);
        truck_no=(TextView)view.findViewById(R.id.truck_no);
        info=(TextView)view.findViewById(R.id.info);

        reenter_truck_id=(Button)view.findViewById(R.id.reenter_truck_id);
        reenter_truck_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), MainActivity.class);
                startActivity(i);
            }
        });

        if(show_issue_value){
            if(MainActivity.context.contains("L")) {
                info.setText(" This Truck Has Been Loaded.");
            } else {
                info.setText(" This Truck Has Been UnLoaded.");
            }
            reenter_truck_id.setVisibility(View.GONE);
        }else{
            info.setText(" This is Not the Correct ID.");
            reenter_truck_id.setVisibility(View.VISIBLE);
        }
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

        title=(TextView)view.findViewById(R.id.title);
        if(MainActivity.context.contains("L")){
            title.setText("ITEMS TO LOAD");
        }else{
            title.setText("ITEMS TO UNLOAD");
        }
        shipment_list=(RecyclerView)view.findViewById(R.id.shipmemt_list);

        truck_no.setText(AddNewTruck_1.current_truck.getId().toString().trim());
        //driver_name.setText("Ram Lal");
        stops_list.setLayoutManager(new GridLayoutManager(getActivity(),3));
        stops_list.setAdapter(new StopsAdapter(getActivity(),AddNewTruck_1.current_truck.getStops()));

        shipment_list.setLayoutManager(new GridLayoutManager(getActivity(),1));
        shipmentAdapter=new ShipmentAdapter(getActivity(),AddNewTruck_1.current_truck.getShipmentItems(), new ShipmentAdapter.OnItemClickListener() {
            @Override
            public void onClick(String shipment_id, int position) {
                current_item=AddNewTruck_1.current_truck.getShipmentItems().get(position);
                shipment_pos=position;

                 /*Intent intent=new Intent(getActivity(), MainActivity.class);
                 intent.putExtra("Activity","LOAD_THIS_ITEM");
                 startActivity(intent);*/

                openShipment(current_item);
            }
        });
        shipment_list.setAdapter(shipmentAdapter);

        if(current_item!=null && !show_issue_value){
            openShipment(current_item);
        }


        show_issue=(FloatingActionButton)view.findViewById(R.id.show_issues);
        if(!show_issue_value){
            show_issue.setVisibility(View.INVISIBLE);
        }
        show_issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_item=AddNewTruck_1.current_truck.getShipmentItems().get(0);
                Intent i=new Intent(getActivity(), MainActivity.class);
                i.putExtra("Activity","ISSUES");
                i.putExtra("Shipment_ID",AddNewTruck_1.current_truck.getShipmentItems().get(0).getId());
                startActivity(i);
            }
        });

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
        holder.shipment_ID.setText(item.getId());
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
        if(MainActivity.context.contains("U")){
            holder.stop_layout.setVisibility(View.GONE);
        }
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

                Intent intent=new Intent(getActivity(),MainActivity.class);
                intent.putExtra("Activity", "ISSUES");
                intent.putExtra("Shipment_ID",item.getId());
                intent.putExtra("From","item");
                startActivity(intent);

            }
        });



        LinkedList<Issues> list1=(LinkedList)item.getDamaged_list();
        LinkedList<Issues> list2=((LinkedList)item.getWeight_list());
        int count=0;
        if(list1!=null) {
           count+=list1.size();


        }
        if(list2!=null){
            count+=list2.size();


        }
        count+=item.getMissing_count();
        Log.i("COUNT123 TRU","1 "+item.getDamaged_list().size());


            holder.issue_nos.setText(String.valueOf(count));/**/


        holder.skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemDialog.dismiss();
            }
        });

        holder.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinkedList<ShipmentItem> item_list=AddNewTruck_1.current_truck.getShipmentItems();
                if(item_list!=null){
                    item_list.remove(current_item);
                    item.setStatus(0);
                    item_list.add(item);
                    AddNewTruck_1.current_truck.setShipmentItems(item_list);
                }


                itemDialog.dismiss();
                shipmentAdapter.notifyDataSetChanged();
                current_item=AddNewTruck_1.current_truck.getShipmentItems().get(shipment_pos);
                if(current_item.getStatus()==0){
                    if(shipment_pos!=0){
                        shipment_pos=0;
                        current_item=AddNewTruck_1.current_truck.getShipmentItems().get(shipment_pos);
                        openShipment(current_item);
                    }else {
                        complete();
                    }
                }else {
                    openShipment(current_item);
                }
            }
        });


        holder.booking_desc.setText(item.getBookedItem().getDescription());
        holder.commodity_length.setText(item.getBookedItem().getLength().toString());
        holder.commodity_width.setText(item.getBookedItem().getWidth().toString());
        holder.commodity_height.setText(item.getBookedItem().getHeight().toString());
        holder.commodity_weight.setText(item.getBookedItem().getActualWeight().toString());











    }

    class ShipmentItemDialogHolder{


        ImageView commodity_img;
        ImageButton see_more_details;
        TextView  shipment_ID;
        TextView  commodity_name;
        TextView  booking_desc_short;


        ImageButton inc_load_count;
        ImageButton dec_load_count;
        EditText loading_count;
        TextView  total_count;


        LinearLayout stop_layout;
        TextView origin;
        TextView drop_loc;
        ImageButton nxt_dest;


        LinearLayout raise_issue_layout;
        TextView issue_nos;

        Button skip;
        Button next;


        TextView booking_desc;
        TextView  commodity_length;
        TextView  commodity_width;
        TextView  commodity_height;
        TextView  commodity_weight;
        LinearLayout other_details;




        ShipmentItemDialogHolder(Dialog dialog){
            commodity_img=(ImageView)dialog.findViewById(R.id.commodity_img);
            shipment_ID=(TextView)dialog.findViewById(R.id.shipment_id);
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


            stop_layout=(LinearLayout)dialog.findViewById(R.id.stop_layout);
            origin=(TextView)dialog.findViewById(R.id.origin);
            drop_loc=(TextView)dialog.findViewById(R.id.drop_loc);
            nxt_dest=(ImageButton)dialog.findViewById(R.id.next_dest);


            issue_nos=(TextView)dialog.findViewById(R.id.issue_nos) ;

            skip=(Button)dialog.findViewById(R.id.skip);
            next=(Button)dialog.findViewById(R.id.next);


            other_details=(LinearLayout) dialog.findViewById(R.id.other_details);

            booking_desc=(TextView)dialog.findViewById(R.id.booking_desc);

            commodity_length=(TextView)dialog.findViewById(R.id.commodity_length);

            commodity_width=(TextView)dialog.findViewById(R.id.commodity_width);

            commodity_height=(TextView)dialog.findViewById(R.id.commodity_height);

            commodity_weight=(TextView)dialog.findViewById(R.id.commodity_weight);




            raise_issue_layout=(LinearLayout)dialog.findViewById(R.id.raise_issue_layout);
        }
    }

    Dialog complete_dialog;
    void complete(){
        complete_dialog=new Dialog(getActivity(), R.style.MyDialogTheme);
        complete_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        complete_dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        complete_dialog.setContentView(R.layout.save_truck_dialog);
        complete_dialog.show();
        complete_dialog.setCancelable(true);

        final  CompleteDialogHolder holder=new CompleteDialogHolder(complete_dialog);
        holder.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.trucks.add(AddNewTruck_1.current_truck);
                complete_dialog.dismiss();
                Intent i=new Intent(getActivity(), MainActivity.class);
                i.putExtra("Activity","TRUCK_LIST");
                startActivity(i);
            }
        });

    }

    class CompleteDialogHolder{
        Button ok;
        CompleteDialogHolder(Dialog dialog){

            ok=(Button)dialog.findViewById(R.id.save);
        }
    }

}
