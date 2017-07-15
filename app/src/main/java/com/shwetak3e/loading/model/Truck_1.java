package com.shwetak3e.loading.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Pervacio on 6/18/2017.
 */

public class Truck_1 {

    String id;
    String origin;
    String destination;
    List<String> stops;
    LinkedList<ShipmentItem> shipmentItems;
    LinkedList<ShipmentItem> loadedItems;




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

    public LinkedList<ShipmentItem> getShipmentItems() {
        if(shipmentItems!=null) return shipmentItems;
        return new LinkedList<>();
    }

    public void setShipmentItems(LinkedList<ShipmentItem> shipmentItems) {
        this.shipmentItems = shipmentItems;
    }

    public LinkedList<ShipmentItem> getLoadedItems() {
        if (loadedItems == null) {
            return new LinkedList<>();
        }
        return loadedItems;
    }


    public void setLoadedItems(LinkedList<ShipmentItem> loadedItems) {
        this.loadedItems = loadedItems;
    }


}
