package com.shwetak3e.loading;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;


import com.shwetak3e.loading.adapter.DrawerAdapter;
import com.shwetak3e.loading.adapter.DrawerItem;
import com.shwetak3e.loading.adapter.SimpleItem;
import com.shwetak3e.loading.adapter.SpaceItem;
import com.shwetak3e.loading.fragments.LoadItems;
import com.shwetak3e.loading.fragments.LoadingSheet;
import com.shwetak3e.loading.model.Booking;
import com.shwetak3e.loading.model.ShipmentItem;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity  implements DrawerAdapter.OnItemSelectedListener{

    private static final int POS_LOAD_ITEMS = 0;
    private static final int POS_LOADED_ITEMS= 1;
    private static final int POS_LOGOUT = 2;


    private List<String> menuTitles=new ArrayList<>();
    private List<Drawable> menunIconsURI=new ArrayList<>();
    public static Map<Integer,Booking> bookings=new HashMap<>();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setData1();
        setData2();
        setData3();

        new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        loadScreenIcons();
        loadScreenTitles();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_LOAD_ITEMS).setChecked(true),
                createItemFor(POS_LOADED_ITEMS),
                new SpaceItem(48),
                createItemFor(POS_LOGOUT)));
        adapter.setListener(this);
        RecyclerView menu_list = (RecyclerView) findViewById(R.id.menu_list);
        menu_list.setNestedScrollingEnabled(false);
        menu_list.setLayoutManager(new GridLayoutManager(this,1));
        menu_list.setAdapter(adapter);

        adapter.setSelected(POS_LOAD_ITEMS);

    }


    private DrawerItem createItemFor(int position) {
        return new SimpleItem(menunIconsURI.get(position), menuTitles.get(position))
                .withIconTint(color(R.color.textColorSecondary))
                .withTextTint(color(R.color.textColorPrimary))
                .withSelectedIconTint(color(R.color.colorAccent))
                .withSelectedTextTint(color(R.color.colorAccent));
    }


    @Override
    public void onItemSelected(int position) {
        Fragment selectedScreen=null;
        if (position == POS_LOGOUT) {
            finish();
        }else if(position==POS_LOAD_ITEMS){
            selectedScreen = LoadItems.newInstance();
        }else if( position==POS_LOADED_ITEMS){
            selectedScreen = LoadingSheet.newInstance();
        }
        showFragment(selectedScreen);
    }


    private void showFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }



    private void  loadScreenTitles() {
        menuTitles.add("Load Items");
        menuTitles.add ("Loaded Items");
        menuTitles.add("LogOut");

    }

    private  void loadScreenIcons() {
        menunIconsURI.add(getResources().getDrawable(R.drawable.ic_home_outline_grey600_24dp));
        menunIconsURI.add(getResources().getDrawable(R.drawable.ic_cart_outline_grey600_24dp));
        menunIconsURI.add(getResources().getDrawable(R.drawable.ic_logout_grey600_24dp));

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


    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }


}
