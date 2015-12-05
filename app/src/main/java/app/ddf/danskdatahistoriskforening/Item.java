package app.ddf.danskdatahistoriskforening;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;

public class Item implements Parcelable{
    private int itemId;

    private String itemHeadline;
    private String itemDescription;
    private Date itemRecieved;
    private Date itemDatingFrom;
    private Date getItemDatingTo;
    private String donator;
    private String producer;
    private String postalCode;

    public Item(){}

    public Item(String itemHeadline, String itemDescription){
        this.itemHeadline = itemHeadline;
        this.itemDescription = itemDescription;
    }

    public Item(Parcel in){
        String[] data = new String[2];

        in.readStringArray(data);
        this.itemHeadline = data[0];
        this.itemDescription = data[1];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.itemHeadline, this.itemDescription});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public Item createFromParcel(Parcel in){
            return new Item(in);
        }

        public Item[] newArray(int size){
            return new Item[size];
        }
    };

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemHeadline() {
        return itemHeadline;
    }

    public void setItemHeadline(String itemHeadline) {
        this.itemHeadline = itemHeadline;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public Date getItemRecieved() {
        return itemRecieved;
    }

    public void setItemRecieved(Date itemRecieved) {
        this.itemRecieved = itemRecieved;
    }

    public Date getItemDatingFrom() {
        return itemDatingFrom;
    }

    public void setItemDatingFrom(Date itemDatingFrom) {
        this.itemDatingFrom = itemDatingFrom;
    }

    public Date getGetItemDatingTo() {
        return getItemDatingTo;
    }

    public void setGetItemDatingTo(Date getItemDatingTo) {
        this.getItemDatingTo = getItemDatingTo;
    }

    public String getDonator() {
        return donator;
    }

    public void setDonator(String donator) {
        this.donator = donator;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
