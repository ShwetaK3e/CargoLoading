package com.shwetak3e.loading;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.shwetak3e.loading.fragments.AddNewTruck;
import com.shwetak3e.loading.fragments.AddNewTruck_1;
import com.shwetak3e.loading.fragments.DrawerFragment;
import com.shwetak3e.loading.fragments.IssueList;
import com.shwetak3e.loading.fragments.LoadItems;
import com.shwetak3e.loading.fragments.LoadItemsItemwise;
import com.shwetak3e.loading.fragments.LoadItemsItemwise_1;
import com.shwetak3e.loading.fragments.LoadingSheet;
import com.shwetak3e.loading.fragments.ReadBookingID;
import com.shwetak3e.loading.fragments.TruckDetails;
import com.shwetak3e.loading.fragments.TruckDetails_1;
import com.shwetak3e.loading.fragments.TruckList;
import com.shwetak3e.loading.fragments.TruckList_1;
import com.shwetak3e.loading.model.Booking;
import com.shwetak3e.loading.model.Issues;
import com.shwetak3e.loading.model.ShipmentItem;
import com.shwetak3e.loading.model.Truck;
import com.shwetak3e.loading.model.Truck_1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements DrawerFragment.FragmentDrawerListener {

    public static Map<String,Booking> bookings=new HashMap<>();
    public static String context="L";
    static int i=-1;


    Toolbar mToolbar;
    DrawerFragment drawerFragment;
    static public List<String> truck_drop_loc=new ArrayList<>();
    static public List<Truck_1> trucks=new LinkedList<>();
    static public Map<String,Truck_1> trucks_1=new HashMap<>();
    static public Truck_1 current_truck=new Truck_1();
    static public boolean express=false;
    static public boolean editTruck=false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent=getIntent();
        String activity=intent.getStringExtra("Activity");
        i++;

        if(i==0) {
            setData1();
            setData2();
            setData3();
            setData4();
            setData5();
            setData6();
            //setTruckList();
            setTruckList_1();
            //setTruckLoc();
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (DrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        /*if("TO_LOAD".equalsIgnoreCase(activity)) {
            displayView(2);
        }else if("Enter_booking_ID".equalsIgnoreCase(activity)) {
            displayView(3);
        }else if("Add_New_Truck".equalsIgnoreCase(activity)){
            displayView(6);
        } else if("Truck_Details".equalsIgnoreCase(activity)){
            displayView(5);
        }else{
            displayView(0);
        }*/

        if("TRUCK_DETAILS_1".equalsIgnoreCase(activity)){
            displayView(3);
        } else if("ISSUES".equalsIgnoreCase(activity)){
            displayView(1);
        } else if("TRUCK_LIST".equalsIgnoreCase(activity)){
            displayView(0);
        } else{
            displayView(2);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    IssueList.OnBackPressedListener onBackPressedListener;
    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = TruckList_1.newInstance();;
                title = getString(R.string.nav_truck_list)+context;
                break;

            case 1:
                onBackPressedListener=new IssueList.OnBackPressedListener() {
                    @Override
                    public void onBack() {
                      Intent i=new Intent(MainActivity.this, MainActivity.class);
                      i.putExtra("Activity","TRUCK_DETAILS_1");
                      i.putExtra("Shipment_ID", TruckDetails_1.current_item.getId());
                      i.putExtra("SKIP", true);
                      if(MainActivity.editTruck==false){
                       i.putExtra("SHOW_ISSUE",true);
                      }
                      startActivity(i);

                    }
                };
                fragment = IssueList.newInstance(onBackPressedListener);
                title = getString(R.string.nav_issue_list)+context;
                break;
            case 2:
                fragment = AddNewTruck_1.newInstance();
                title = getString(R.string.nav_add_new_truck)+context;
                break;
            case 3:
                fragment = TruckDetails_1.newInstance();
                title = getString(R.string.nav_truck_details)+context;
                break;

            /*case 2:
                fragment = LoadItemsItemwise.newInstance();
                title = getString(R.string.nav_item_to_load);
                break;
            case 3:
                fragment = ReadBookingID.newInstance();
                title = getString(R.string.nav_booking_id);
                break;
            case 4:
                fragment = LoadingSheet.newInstance();
                title = getString(R.string.nav_item_loaded);
                break;
            case 5:
                fragment = TruckDetails.newInstance();
                title = getString(R.string.nav_truck_details);
                break;
            case 6:
                fragment = AddNewTruck.newInstance();
                title = getString(R.string.nav_add_new_truck);
                break;*/
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }


    void setData1() {

        Booking booking=new Booking();
        booking.setBookingID("RTYUFG");

        List<ShipmentItem> shipmentItems=new ArrayList<>();

        ShipmentItem  shipmentItem=new ShipmentItem();
        shipmentItem.setId("RTYUFG_REF");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("Refrigerator");
        shipmentItem.getBookedItem().setDescription("Handle Top part with Care ");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_refrigerator).toString());
        shipmentItem.setShippedItemCount(20);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(20);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setWeight_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItem.getBookedItem().setLength(1045);
        shipmentItem.getBookedItem().setWidth(2066);
        shipmentItem.getBookedItem().setHeight(344);
        shipmentItem.getBookedItem().setActualWeight(345);
        shipmentItem.setOrigin("HYD");
        shipmentItem.setDestination("DEL");
        shipmentItems.add(shipmentItem);

        shipmentItem=new ShipmentItem();
        shipmentItem.setId("RTYUFG_CHR");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("Chair");
        shipmentItem.getBookedItem().setDescription("This contains Cushion ");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_chair).toString());
        shipmentItem.setShippedItemCount(5);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(5);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setWeight_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItem.getBookedItem().setLength(105);
        shipmentItem.getBookedItem().setWidth(5660);
        shipmentItem.getBookedItem().setHeight(357);
        shipmentItem.getBookedItem().setActualWeight(450);
        shipmentItem.setDestination("REW");
        shipmentItem.setOrigin("HYD");
        shipmentItems.add(shipmentItem);


        shipmentItem=new ShipmentItem();
        shipmentItem.setId("RTYUFG_BED");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("Bed");
        shipmentItem.getBookedItem().setDescription("Keep Beside Soft Items ");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_bed).toString());
        shipmentItem.setShippedItemCount(2);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(2);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setWeight_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItems.add(shipmentItem);
        shipmentItem.getBookedItem().setLength(144);
        shipmentItem.getBookedItem().setWidth(2056);
        shipmentItem.getBookedItem().setHeight(345);
        shipmentItem.setDestination("OMA");
        shipmentItem.setOrigin("HYD");
        shipmentItem.getBookedItem().setActualWeight(4550);

        booking.setItems(shipmentItems);
        bookings.put("RTYUFG",booking);


    }


    void setData2() {

        Booking booking=new Booking();
        booking.setBookingID("DJKFGH");

        List<ShipmentItem> shipmentItems=new ArrayList<>();

        ShipmentItem shipmentItem=new ShipmentItem();
        shipmentItem.setId("DJKFGH_DEO");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("AXE Deo");
        shipmentItem.getBookedItem().setDescription("Keep Liquid safe. ");
        shipmentItem.getBookedItem().setLength(10);
        shipmentItem.getBookedItem().setWidth(20);
        shipmentItem.getBookedItem().setHeight(30);
        shipmentItem.getBookedItem().setActualWeight(20);
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_body_spray).toString());
        shipmentItem.setShippedItemCount(500);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(500);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setWeight_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItem.setDestination("FGT");
        shipmentItem.setOrigin("HYD");
        shipmentItems.add(shipmentItem);


        shipmentItem=new ShipmentItem();
        shipmentItem.setId("DJKFGH_BLB");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("Bulbs");
        shipmentItem.getBookedItem().setDescription("Contains Glass Items ");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_bulb).toString());
        shipmentItem.setShippedItemCount(50);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(50);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setWeight_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItem.getBookedItem().setLength(45);
        shipmentItem.getBookedItem().setWidth(56);
        shipmentItem.getBookedItem().setHeight(45);
        shipmentItem.getBookedItem().setActualWeight(144);
        shipmentItem.setDestination("RTY");
        shipmentItem.setOrigin("HYD");
        shipmentItems.add(shipmentItem);

        shipmentItem=new ShipmentItem();
        shipmentItem.setId("DJKFGH_WALL");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("Wallpaper");
        shipmentItem.getBookedItem().setDescription("Contains Paper Items");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_wallpaper).toString());
        shipmentItem.setShippedItemCount(60);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(60);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setWeight_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItem.getBookedItem().setLength(106);
        shipmentItem.getBookedItem().setWidth(520);
        shipmentItem.getBookedItem().setHeight(45);
        shipmentItem.getBookedItem().setActualWeight(67);
        shipmentItem.setDestination("GYJ");
        shipmentItem.setOrigin("HYD");
        shipmentItems.add(shipmentItem);


        shipmentItem=new ShipmentItem();
        shipmentItem.setId("DJKFGH_BLN");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("Blanket");
        shipmentItem.getBookedItem().setDescription("Handle with care ");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_blanket).toString());
        shipmentItem.setShippedItemCount(60);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(60);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setWeight_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItem.getBookedItem().setLength(10);
        shipmentItem.getBookedItem().setWidth(20);
        shipmentItem.getBookedItem().setHeight(330);
        shipmentItem.getBookedItem().setActualWeight(2560);
        shipmentItem.setDestination("FRG");
        shipmentItem.setOrigin("HYD");
        shipmentItems.add(shipmentItem);

        booking.setItems(shipmentItems);
        bookings.put("DJKFGH",booking);


    }


    void setData3() {

        Booking booking=new Booking();
        booking.setBookingID("CJKLRR");

        List<ShipmentItem> shipmentItems=new ArrayList<>();

        ShipmentItem  shipmentItem=new ShipmentItem();
        shipmentItem.setId("CJKLRR_SCS");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("Suitcase");
        shipmentItem.getBookedItem().setDescription("Place this on top");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_suitcase).toString());
        shipmentItem.setShippedItemCount(20);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(20);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setWeight_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItem.getBookedItem().setLength(45);
        shipmentItem.getBookedItem().setWidth(345);
        shipmentItem.getBookedItem().setHeight(670);
        shipmentItem.getBookedItem().setActualWeight(40);
        shipmentItem.setDestination("TUY");
        shipmentItem.setOrigin("HYD");
        shipmentItems.add(shipmentItem);

        shipmentItem=new ShipmentItem();
        shipmentItem.setId("CJKLRR_ACL");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("AC -Lenovo");
        shipmentItem.getBookedItem().setDescription("Contains Electrical Equipment ");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_ac).toString());
        shipmentItem.setShippedItemCount(50);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setWeight_count(0);
        shipmentItem.setUnloadedCount(50);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItem.getBookedItem().setLength(550);
        shipmentItem.getBookedItem().setWidth(25);
        shipmentItem.getBookedItem().setHeight(37);
        shipmentItem.getBookedItem().setActualWeight(220);
        shipmentItem.setDestination("THJ");
        shipmentItem.setOrigin("HYD");
        shipmentItems.add(shipmentItem);


        booking.setItems(shipmentItems);
        bookings.put("CJKLRR",booking);


    }

    void setData4() {

        Booking booking=new Booking();
        booking.setBookingID("OPRTYT");

        List<ShipmentItem> shipmentItems=new ArrayList<>();

        ShipmentItem shipmentItem=new ShipmentItem();
        shipmentItem.setId("OPRTYT_DEO");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("AXE Deo");
        shipmentItem.getBookedItem().setDescription("Keep Liquid safe. ");
        shipmentItem.getBookedItem().setLength(10);
        shipmentItem.getBookedItem().setWidth(20);
        shipmentItem.getBookedItem().setHeight(30);
        shipmentItem.getBookedItem().setActualWeight(20);
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_body_spray).toString());
        shipmentItem.setShippedItemCount(500);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(500);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setWeight_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItem.setDestination("FGT");
        shipmentItem.setOrigin("HYD");
        shipmentItems.add(shipmentItem);


        shipmentItem=new ShipmentItem();
        shipmentItem.setId("OPRTYT_BLB");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("Bulbs");
        shipmentItem.getBookedItem().setDescription("Contains Glass Items ");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_bulb).toString());
        shipmentItem.setShippedItemCount(50);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(50);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setWeight_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItem.getBookedItem().setLength(45);
        shipmentItem.getBookedItem().setWidth(56);
        shipmentItem.getBookedItem().setHeight(45);
        shipmentItem.getBookedItem().setActualWeight(144);
        shipmentItem.setDestination("RTY");
        shipmentItem.setOrigin("HYD");
        shipmentItems.add(shipmentItem);

        shipmentItem=new ShipmentItem();
        shipmentItem.setId("OPRTYT_WALL");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("Wallpaper");
        shipmentItem.getBookedItem().setDescription("Contains Paper Items");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_wallpaper).toString());
        shipmentItem.setShippedItemCount(60);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(60);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setWeight_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItem.getBookedItem().setLength(106);
        shipmentItem.getBookedItem().setWidth(520);
        shipmentItem.getBookedItem().setHeight(45);
        shipmentItem.getBookedItem().setActualWeight(67);
        shipmentItem.setDestination("GYJ");
        shipmentItem.setOrigin("HYD");
        shipmentItems.add(shipmentItem);


        shipmentItem=new ShipmentItem();
        shipmentItem.setId("OPRTYT_BLN");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("Blanket");
        shipmentItem.getBookedItem().setDescription("Handle with care ");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_blanket).toString());
        shipmentItem.setShippedItemCount(60);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(60);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setWeight_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItem.getBookedItem().setLength(10);
        shipmentItem.getBookedItem().setWidth(20);
        shipmentItem.getBookedItem().setHeight(330);
        shipmentItem.getBookedItem().setActualWeight(2560);
        shipmentItem.setDestination("FRG");
        shipmentItem.setOrigin("HYD");
        shipmentItems.add(shipmentItem);

        booking.setItems(shipmentItems);
        bookings.put("OPRTYT",booking);


    }

    void setData5() {

        Booking booking=new Booking();
        booking.setBookingID("SSFGTE");

        List<ShipmentItem> shipmentItems=new ArrayList<>();

        ShipmentItem  shipmentItem=new ShipmentItem();
        shipmentItem.setId("SSFGTE_REF");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("Refrigerator");
        shipmentItem.getBookedItem().setDescription("Handle Top part with Care ");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_refrigerator).toString());
        shipmentItem.setShippedItemCount(20);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(20);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setWeight_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItem.getBookedItem().setLength(1045);
        shipmentItem.getBookedItem().setWidth(2066);
        shipmentItem.getBookedItem().setHeight(344);
        shipmentItem.getBookedItem().setActualWeight(345);
        shipmentItem.setOrigin("HYD");
        shipmentItem.setDestination("DEL");
        shipmentItems.add(shipmentItem);

        shipmentItem=new ShipmentItem();
        shipmentItem.setId("SSFGTE_CHR");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("Chair");
        shipmentItem.getBookedItem().setDescription("This contains Cushion ");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_chair).toString());
        shipmentItem.setShippedItemCount(5);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(5);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setWeight_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItem.getBookedItem().setLength(105);
        shipmentItem.getBookedItem().setWidth(5660);
        shipmentItem.getBookedItem().setHeight(357);
        shipmentItem.getBookedItem().setActualWeight(450);
        shipmentItem.setDestination("REW");
        shipmentItem.setOrigin("HYD");
        shipmentItems.add(shipmentItem);


        shipmentItem=new ShipmentItem();
        shipmentItem.setId("SSFGTE_BED");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("Bed");
        shipmentItem.getBookedItem().setDescription("Keep Beside Soft Items ");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_bed).toString());
        shipmentItem.setShippedItemCount(2);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(2);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setWeight_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItems.add(shipmentItem);
        shipmentItem.getBookedItem().setLength(144);
        shipmentItem.getBookedItem().setWidth(2056);
        shipmentItem.getBookedItem().setHeight(345);
        shipmentItem.setDestination("OMA");
        shipmentItem.setOrigin("HYD");
        shipmentItem.getBookedItem().setActualWeight(4550);

        booking.setItems(shipmentItems);
        bookings.put("SSFGTE",booking);


    }

    void setData6() {

        Booking booking=new Booking();
        booking.setBookingID("PWETYU");

        List<ShipmentItem> shipmentItems=new ArrayList<>();

        ShipmentItem shipmentItem=new ShipmentItem();
        shipmentItem.setId("PWETYU_DEO");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("AXE Deo");
        shipmentItem.getBookedItem().setDescription("Keep Liquid safe. ");
        shipmentItem.getBookedItem().setLength(10);
        shipmentItem.getBookedItem().setWidth(20);
        shipmentItem.getBookedItem().setHeight(30);
        shipmentItem.getBookedItem().setActualWeight(20);
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_body_spray).toString());
        shipmentItem.setShippedItemCount(500);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(500);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setWeight_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItem.setDestination("FGT");
        shipmentItem.setOrigin("HYD");
        shipmentItems.add(shipmentItem);


        shipmentItem=new ShipmentItem();
        shipmentItem.setId("PWETYU_BLB");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("Bulbs");
        shipmentItem.getBookedItem().setDescription("Contains Glass Items ");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_bulb).toString());
        shipmentItem.setShippedItemCount(50);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(50);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setWeight_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItem.getBookedItem().setLength(45);
        shipmentItem.getBookedItem().setWidth(56);
        shipmentItem.getBookedItem().setHeight(45);
        shipmentItem.getBookedItem().setActualWeight(144);
        shipmentItem.setDestination("RTY");
        shipmentItem.setOrigin("HYD");
        shipmentItems.add(shipmentItem);

        shipmentItem=new ShipmentItem();
        shipmentItem.setId("PWETYU_WALL");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("Wallpaper");
        shipmentItem.getBookedItem().setDescription("Contains Paper Items");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_wallpaper).toString());
        shipmentItem.setShippedItemCount(60);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(60);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setWeight_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItem.getBookedItem().setLength(106);
        shipmentItem.getBookedItem().setWidth(520);
        shipmentItem.getBookedItem().setHeight(45);
        shipmentItem.getBookedItem().setActualWeight(67);
        shipmentItem.setDestination("GYJ");
        shipmentItem.setOrigin("HYD");
        shipmentItems.add(shipmentItem);


        shipmentItem=new ShipmentItem();
        shipmentItem.setId("PWETYU_BLN");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("Blanket");
        shipmentItem.getBookedItem().setDescription("Handle with care ");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_blanket).toString());
        shipmentItem.setShippedItemCount(60);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(60);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setWeight_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItem.getBookedItem().setLength(10);
        shipmentItem.getBookedItem().setWidth(20);
        shipmentItem.getBookedItem().setHeight(330);
        shipmentItem.getBookedItem().setActualWeight(2560);
        shipmentItem.setDestination("FRG");
        shipmentItem.setOrigin("HYD");
        shipmentItems.add(shipmentItem);



        shipmentItem=new ShipmentItem();
        shipmentItem.setId("PWETYU_WALL");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("WallPaper");
        shipmentItem.getBookedItem().setDescription("Handle with care ");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_wallpaper).toString());
        shipmentItem.setShippedItemCount(60);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(60);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setWeight_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItem.getBookedItem().setLength(10);
        shipmentItem.getBookedItem().setWidth(20);
        shipmentItem.getBookedItem().setHeight(330);
        shipmentItem.getBookedItem().setActualWeight(2560);
        shipmentItem.setDestination("FRG");
        shipmentItem.setOrigin("HYD");
        shipmentItems.add(shipmentItem);

        booking.setItems(shipmentItems);
        bookings.put("PWETYU",booking);


    }


   /* void setTruckList(){
        Truck truck =new Truck();

        truck.setId("HYD-18346506");
        truck.setDriver_name("Rana Ji");
        truck.setOrigin("HYD");
        truck.setDestination("VIZ");

        trucks.add(truck);

        truck =new Truck();
        truck.setId("HYD-34354554");
        truck.setDriver_name("Shekhawat");
        truck.setOrigin("HYD");
        truck.setDestination("CHN");
        trucks.add(truck);

        truck =new Truck();
        truck.setId("HYD-153444555");
        truck.setDriver_name("Tara Singh");
        truck.setOrigin("HYD");
        truck.setDestination("DEL");
        trucks.add(truck);

        truck =new Truck();
        truck.setId("HYD-45586254");
        truck.setDriver_name("Lala Ji");
        truck.setOrigin("HYD");
        truck.setDestination("KOL");
        trucks.add(truck);

        truck =new Truck();
        truck.setId("HYD-34345353");
        truck.setDriver_name("Xander");
        truck.setOrigin("HYD");
        truck.setDestination("BHL");
        trucks.add(truck);

        truck =new Truck();
        truck.setId("HYD-16534422");
        truck.setDriver_name("Panna Seth");
        truck.setOrigin("HYD");
        truck.setDestination("RNC");
        trucks.add(truck);

        truck =new Truck();
        truck.setId("HYD-45355443");
        truck.setDriver_name("Aakash Seth");
        truck.setOrigin("HYD");
        truck.setDestination("PAN");
        trucks.add(truck);

        truck =new Truck();
        truck.setId("HYD-32543334");
        truck.setDriver_name("Mahendra");
        truck.setOrigin("HYD");
        truck.setDestination("MUM");
        trucks.add(truck);

        truck =new Truck();
        truck.setId("HYD-65442425");
        truck.setDriver_name("Sunder");
        truck.setOrigin("HYD");
        truck.setDestination("AHM");
        trucks.add(truck);

        truck =new Truck();
        truck.setId("HYD-34244531");
        truck.setDriver_name("Pintu Singh");
        truck.setOrigin("HYD");
        truck.setDestination("BHP");
        trucks.add(truck);

        truck =new Truck();
        truck.setId("HYD-87975653");
        truck.setDriver_name("Pillu Lal");
        truck.setOrigin("HYD");
        truck.setDestination("NIZ");
        trucks.add(truck);

        truck =new Truck();
        truck.setId("HYD-76123424");
        truck.setDriver_name("PriyaSingh");
        truck.setOrigin("HYD");
        truck.setDestination("DES");
        trucks.add(truck);

        truck =new Truck();
        truck.setId("HYD-78598492");
        truck.setDriver_name("Chinna Set");
        truck.setOrigin("HYD");
        truck.setDestination("CHA");
        trucks.add(truck);

        truck =new Truck();
        truck.setId("HYD-43437855");
        truck.setDriver_name("Ratan ");
        truck.setOrigin("HYD");
        truck.setDestination("VIZ");
        trucks.add(truck);

        truck =new Truck();
        truck.setId("HYD-67654923");
        truck.setDriver_name("Shyam Lal");
        truck.setOrigin("HYD");
        truck.setDestination("MED");
        trucks.add(truck);


    }
*/
    final Map<String,ShipmentItem> shipments_1=new HashMap<>();
    final Map<String,ShipmentItem> shipments_2=new HashMap<>();
    final Map<String,ShipmentItem> shipments_3=new HashMap<>();
    void setTruckList_1(){
        Truck_1 truck_1=new Truck_1();

        for(Map.Entry<String, Booking> booking:bookings.entrySet()){
            List<ShipmentItem> shipments=booking.getValue().getItems();
            for(ShipmentItem shipmentItem:shipments){
                if("CJKLRR".equalsIgnoreCase(booking.getKey())|| "OPRTYT".equalsIgnoreCase(booking.getKey())) {
                    shipments_1.put(shipmentItem.getId(), shipmentItem);
                }else if("DJKFGH".equalsIgnoreCase(booking.getKey())|| "SSFGTE".equalsIgnoreCase(booking.getKey())){
                    shipments_2.put(shipmentItem.getId(), shipmentItem);
                }else if("RTYUFG".equalsIgnoreCase(booking.getKey())){
                    shipments_3.put(shipmentItem.getId(), shipmentItem);
                }
            }
        }



        List<ShipmentItem> shipment_list1=new LinkedList<>();
        List<ShipmentItem> shipment_list2=new LinkedList<>();
        List<ShipmentItem> shipment_list3=new LinkedList<>();
        for(Map.Entry<String,ShipmentItem> entry:shipments_1.entrySet()){
            shipment_list1.add(entry.getValue());
        }
        for(Map.Entry<String,ShipmentItem> entry:shipments_2.entrySet()){
            shipment_list2.add(entry.getValue());
        }
        for(Map.Entry<String,ShipmentItem> entry:shipments_3.entrySet()){
            shipment_list3.add(entry.getValue());
        }





        truck_1.setId("ABCDEFGH12");
        truck_1.setOrigin("HYD");
        truck_1.setDestination("VIZ");
        truck_1.setStops(Arrays.asList("HYD","KOL","CHN","DEL","VIZ"));
        truck_1.setShipmentItems((LinkedList)shipment_list1);
        trucks_1.put(truck_1.getId(),truck_1);

        truck_1=new Truck_1();
        truck_1.setId("MNOPQRST34");
        truck_1.setOrigin("HYD");
        truck_1.setDestination("AHM");
        truck_1.setStops(Arrays.asList("HYD","CAL","MUM","SUR","AHM"));
        truck_1.setShipmentItems((LinkedList)shipment_list2);
        trucks_1.put(truck_1.getId(),truck_1);


        truck_1=new Truck_1();
        truck_1.setId("GJKFJRTU59");
        truck_1.setOrigin("HYD");
        truck_1.setDestination("BHP");
        truck_1.setStops(Arrays.asList("HYD","TEA","HJK","BHL","BHP"));
        truck_1.setShipmentItems((LinkedList)shipment_list3);
        trucks_1.put(truck_1.getId(),truck_1);



        truck_1=new Truck_1();
        truck_1.setId("GKBNRNTI89");
        truck_1.setOrigin("HYD");
        truck_1.setDestination("CGP");
        truck_1.setStops(Arrays.asList("HYD","CGK","FIT","HKL","CGP"));
        truck_1.setShipmentItems((LinkedList)shipment_list1);
        trucks_1.put(truck_1.getId(),truck_1);


        truck_1=new Truck_1();
        truck_1.setId("GJHLYPOT67");
        truck_1.setOrigin("HYD");
        truck_1.setDestination("BHP");
        truck_1.setStops(Arrays.asList("HYD","TEA","HJK","BHL","BHP"));
        truck_1.setShipmentItems((LinkedList)shipment_list2);
        trucks_1.put(truck_1.getId(),truck_1);

        truck_1=new Truck_1();
        truck_1.setId("HKGOPTMG56");
        truck_1.setOrigin("HYD");
        truck_1.setDestination("BHP");
        truck_1.setStops(Arrays.asList("HYD","TEA","HJK","BHL","BHP"));
        truck_1.setShipmentItems((LinkedList)shipment_list3);
        trucks_1.put(truck_1.getId(),truck_1);

    }

    void setTruckLoc(){
        truck_drop_loc.add("HYD");
        truck_drop_loc.add("CHN");
        truck_drop_loc.add("VIZ");
        truck_drop_loc.add("KOL");
        truck_drop_loc.add("DEL");
    }


    @Override
    public void onBackPressed() {
        if(onBackPressedListener!=null){
            onBackPressedListener.onBack();
            onBackPressedListener=null;
        }
        super.onBackPressed();
    }
}
