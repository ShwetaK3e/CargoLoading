package com.shwetak3e.loading.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pervacio on 5/29/2017.
 */

public class Booking {

    int bookingID;
    List<ShipmentItem> items=new ArrayList<>();

    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public List<ShipmentItem> getItems() {
        return items;
    }

    public void setItems(List<ShipmentItem> items) {
        this.items = items;
    }
}
