package com.shwetak3e.loading.model;

import java.util.List;

/**
 * Created by Pervacio on 6/18/2017.
 */

public class Truck_1 {

    String id;
    String origin;
    String destination;
    List<String> stops;
    List<ShipmentItem> shipmentItems;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public List<String> getStops() {
        return stops;
    }

    public void setStops(List<String> stops) {
        this.stops = stops;
    }

    public List<ShipmentItem> getShipmentItems() {
        return shipmentItems;
    }

    public void setShipmentItems(List<ShipmentItem> shipmentItems) {
        this.shipmentItems = shipmentItems;
    }
}
