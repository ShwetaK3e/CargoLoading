package com.shwetak3e.loading;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.shwetak3e.loading.fragments.AddNewTruck;
import com.shwetak3e.loading.fragments.DrawerFragment;
import com.shwetak3e.loading.fragments.LoadItems;
import com.shwetak3e.loading.fragments.LoadItemsItemwise;
import com.shwetak3e.loading.fragments.LoadingSheet;
import com.shwetak3e.loading.fragments.ReadBookingID;
import com.shwetak3e.loading.fragments.TruckList;
import com.shwetak3e.loading.model.Booking;
import com.shwetak3e.loading.model.ShipmentItem;
import com.shwetak3e.loading.model.Truck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements DrawerFragment.FragmentDrawerListener {

    public static Map<Integer,Booking> bookings=new HashMap<>();
    static int i=-1;


    Toolbar mToolbar;
    DrawerFragment drawerFragment;
    static public List<String> truck_drop_loc=new ArrayList<>();
    static public List<Truck> trucks=new LinkedList<>();
    static public Truck current_truck=new Truck();
    static public boolean express=false;

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
            setTruckList();
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (DrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        if("TO_LOAD".equalsIgnoreCase(activity)) {
            displayView(2);
        }else if("Enter_booking_ID".equalsIgnoreCase(activity)) {
            displayView(3);
        }else if("Add_New_Truck".equalsIgnoreCase(activity)){
            displayView(1);
        } else{
            displayView(0);
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

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = TruckList.newInstance();;
                title = getString(R.string.nav_truck_list);
                break;
            case 1:
                fragment = AddNewTruck.newInstance();
                title = getString(R.string.nav_add_new_truck);
                break;
            case 2:
                fragment = LoadItemsItemwise.newInstance();
                title = getString(R.string.nav_item_to_load);
                break;
            case 3:
                fragment = ReadBookingID.newInstance();
                title = getString(R.string.nav_booking_id);
                break;
            case 4:
                fragment = LoadingSheet.newInstance();
                title = getString(R.string.nav_item_to_unload);
                break;
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
        booking.setBookingID(120);

        List<ShipmentItem> shipmentItems=new ArrayList<>();

        ShipmentItem  shipmentItem=new ShipmentItem();
        shipmentItem.setId("120_1");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("Refrigerator");
        shipmentItem.getBookedItem().setDescription("Handle Top part with Care ");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_refrigerator).toString());
        shipmentItem.setShippedItemCount(20);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(20);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItems.add(shipmentItem);

        shipmentItem=new ShipmentItem();
        shipmentItem.setId("120_2");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("Chair");
        shipmentItem.getBookedItem().setDescription("This contains Cushion ");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_chair).toString());
        shipmentItem.setShippedItemCount(5);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(5);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItems.add(shipmentItem);

        shipmentItem=new ShipmentItem();
        shipmentItem.setId("120_3");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("Bed");
        shipmentItem.getBookedItem().setDescription("Keep Beside Soft Items ");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_bed).toString());
        shipmentItem.setShippedItemCount(2);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(2);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItems.add(shipmentItem);

        booking.setItems(shipmentItems);
        bookings.put(120,booking);


    }



    void setData2() {

        Booking booking=new Booking();
        booking.setBookingID(121);

        List<ShipmentItem> shipmentItems=new ArrayList<>();

        ShipmentItem shipmentItem=new ShipmentItem();
        shipmentItem.setId("121_1");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("AXE Deo");
        shipmentItem.getBookedItem().setDescription("Keep Liquid safe. ");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_body_spray).toString());
        shipmentItem.setShippedItemCount(500);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(500);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItems.add(shipmentItem);

        shipmentItem=new ShipmentItem();
        shipmentItem.setId("121_2");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("Bulbs");
        shipmentItem.getBookedItem().setDescription("Contains Glass Items ");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_bulb).toString());
        shipmentItem.setShippedItemCount(50);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(50);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItems.add(shipmentItem);

        shipmentItem=new ShipmentItem();
        shipmentItem.setId("121_3");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("Wallpaper");
        shipmentItem.getBookedItem().setDescription("Contains Paper Items");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_wallpaper).toString());
        shipmentItem.setShippedItemCount(60);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(60);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItems.add(shipmentItem);


        shipmentItem=new ShipmentItem();
        shipmentItem.setId("121_4");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("Blanket");
        shipmentItem.getBookedItem().setDescription("Handle with care ");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_blanket).toString());
        shipmentItem.setShippedItemCount(60);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(60);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItems.add(shipmentItem);

        booking.setItems(shipmentItems);
        bookings.put(121,booking);


    }


    void setData3() {

        Booking booking=new Booking();
        booking.setBookingID(122);

        List<ShipmentItem> shipmentItems=new ArrayList<>();

        ShipmentItem  shipmentItem=new ShipmentItem();
        shipmentItem.setId("122_1");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("Suitcase");
        shipmentItem.getBookedItem().setDescription("Place this on top");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_suitcase).toString());
        shipmentItem.setShippedItemCount(20);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(20);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItems.add(shipmentItem);

        shipmentItem=new ShipmentItem();
        shipmentItem.setId("122_2");
        shipmentItem.setBookedItem(new ShipmentItem.BookedItem());
        shipmentItem.getBookedItem().setCommodityName("AC -Lenovo");
        shipmentItem.getBookedItem().setDescription("Contains Electrical Equipment ");
        shipmentItem.setImageUri(Uri.parse("android.resource://com.shwetak3e.loading/" + R.drawable.ic_ac).toString());
        shipmentItem.setShippedItemCount(50);
        shipmentItem.setLoadedCount(0);
        shipmentItem.setUnloadedCount(50);
        shipmentItem.setStatus(1);
        shipmentItem.setDamaged_count(0);
        shipmentItem.setMissing_count(0);
        shipmentItem.setLeakage_count(0);
        shipmentItem.setDamagedStatus(false);
        shipmentItem.setSame_truck_status(false);
        shipmentItems.add(shipmentItem);


        booking.setItems(shipmentItems);
        bookings.put(122,booking);


    }

    void setTruckList(){
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





}
