package com.shwetak3e.loading.model;

/**
 * Created by Pervacio on 5/20/2017.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;


public class ShipmentItem {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("shipment")
    @Expose
    private Shipment shipment;
    @SerializedName("bookedItem")
    @Expose
    private BookedItem bookedItem;
    @SerializedName("shippedItemCount")
    @Expose
    private Integer shippedItemCount;
    @SerializedName("loadedCount")
    @Expose
    private Integer loadedCount;
    @SerializedName("unloadedCount")
    @Expose
    private Integer unloadedCount;

    private String imageUri;


    private Integer damaged_count;

    private Integer missing_count;

    private  Integer leakage_count;

    private Integer Status ;

    private Boolean damagedStatus;

    private Boolean same_truck_status;


    private List<Issues> issues;


    private String destination;
    private String origin;
    private LinkedList<String> stops;
    private boolean stopSet;

    public boolean isStopSet() {
        return stopSet;
    }

    public void setStopSet(boolean stopSet) {
        this.stopSet = stopSet;
    }

    public LinkedList<String> getStops() {
        if(stops!=null) return stops;
        else return new LinkedList<>();
    }

    public void setStops(LinkedList<String> stops) {
        this.stops = stops;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public List<Issues> getIssues() {
        return issues;
    }

    public void setIssues(List<Issues> issues) {
        this.issues = issues;
    }

    public Boolean getSame_truck_status() {
        return same_truck_status;
    }

    public void setSame_truck_status(Boolean same_truck_status) {
        this.same_truck_status = same_truck_status;
    }

    public Boolean getDamagedStatus() {
        return damagedStatus;
    }



    public void setDamagedStatus(Boolean damagedStatus) {
        this.damagedStatus = damagedStatus;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public Integer getDamaged_count() {
        return damaged_count;
    }

    public void setDamaged_count(Integer damaged_count) {
        this.damaged_count = damaged_count;
    }

    public Integer getMissing_count() {
        return missing_count;
    }

    public void setMissing_count(Integer missing_count) {
        this.missing_count = missing_count;
    }

    public Integer getLeakage_count() {
        return leakage_count;
    }

    public void setLeakage_count(Integer leakage_count) {
        this.leakage_count = leakage_count;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public BookedItem getBookedItem() {
        return bookedItem;
    }

    public void setBookedItem(BookedItem bookedItem) {
        this.bookedItem = bookedItem;
    }

    public Integer getShippedItemCount() {
        return shippedItemCount;
    }

    public void setShippedItemCount(Integer shippedItemCount) {
        this.shippedItemCount = shippedItemCount;
    }

    public Integer getLoadedCount() {
        return loadedCount;
    }

    public void setLoadedCount(Integer loadedCount) {
        this.loadedCount = loadedCount;
    }

    public Integer getUnloadedCount() {
        return unloadedCount;
    }

    public void setUnloadedCount(Integer unloadedCount) {
        this.unloadedCount = unloadedCount;
    }


    public static class BookedItem {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("articleType")
        @Expose
        private ArticleType articleType;
        @SerializedName("itemNumber")
        @Expose
        private String itemNumber;
        @SerializedName("commodityName")
        @Expose
        private String commodityName;
        @SerializedName("sizeUOM")
        @Expose
        private String sizeUOM;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("length")
        @Expose
        private Integer length;
        @SerializedName("width")
        @Expose
        private Integer width;
        @SerializedName("height")
        @Expose
        private Integer height;
        @SerializedName("cftVolume")
        @Expose
        private Integer cftVolume;
        @SerializedName("cftWeight")
        @Expose
        private Integer cftWeight;
        @SerializedName("actualWeight")
        @Expose
        private Integer actualWeight;
        @SerializedName("chargedWeight")
        @Expose
        private Integer chargedWeight;
        @SerializedName("unitPrice")
        @Expose
        private Integer unitPrice;
        @SerializedName("totalPrice")
        @Expose
        private Integer totalPrice;
        @SerializedName("itemCount")
        @Expose
        private Integer itemCount;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public ArticleType getArticleType() {
            return articleType;
        }

        public void setArticleType(ArticleType articleType) {
            this.articleType = articleType;
        }

        public String getItemNumber() {
            return itemNumber;
        }

        public void setItemNumber(String itemNumber) {
            this.itemNumber = itemNumber;
        }

        public String getCommodityName() {
            return commodityName;
        }

        public void setCommodityName(String commodityName) {
            this.commodityName = commodityName;
        }

        public String getSizeUOM() {
            return sizeUOM;
        }

        public void setSizeUOM(String sizeUOM) {
            this.sizeUOM = sizeUOM;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Integer getLength() {
            return length;
        }

        public void setLength(Integer length) {
            this.length = length;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public Integer getCftVolume() {
            return cftVolume;
        }

        public void setCftVolume(Integer cftVolume) {
            this.cftVolume = cftVolume;
        }

        public Integer getCftWeight() {
            return cftWeight;
        }

        public void setCftWeight(Integer cftWeight) {
            this.cftWeight = cftWeight;
        }

        public Integer getActualWeight() {
            return actualWeight;
        }

        public void setActualWeight(Integer actualWeight) {
            this.actualWeight = actualWeight;
        }

        public Integer getChargedWeight() {
            return chargedWeight;
        }

        public void setChargedWeight(Integer chargedWeight) {
            this.chargedWeight = chargedWeight;
        }

        public Integer getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(Integer unitPrice) {
            this.unitPrice = unitPrice;
        }

        public Integer getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(Integer totalPrice) {
            this.totalPrice = totalPrice;
        }

        public Integer getItemCount() {
            return itemCount;
        }

        public void setItemCount(Integer itemCount) {
            this.itemCount = itemCount;
        }


        public class ArticleType {

            @SerializedName("id")
            @Expose
            private String id;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

        }

    }

    public class Shipment {

        @SerializedName("id")
        @Expose
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

    }

}

