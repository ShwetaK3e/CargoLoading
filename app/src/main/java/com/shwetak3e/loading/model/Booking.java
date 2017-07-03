package com.shwetak3e.loading.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pervacio on 5/29/2017.
 */

public class Booking {

    String bookingID;
    List<ShipmentItem> items=new ArrayList<>();

    public String getBookingID() {
        return bookingID;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public List<ShipmentItem> getItems() {
        return items;
    }

    public void setItems(List<ShipmentItem> items) {
        this.items = items;
    }
}
