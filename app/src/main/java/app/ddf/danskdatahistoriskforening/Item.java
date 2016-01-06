package app.ddf.danskdatahistoriskforening;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Item implements Parcelable{

    private int itemId;
    private String itemHeadline;
    private String itemDescription;
    private Date itemRecieved;
    private Date itemDatingFrom;
    private Date itemDatingTo;
    private String donator;
    private String producer;
    private String postalCode;
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public Item(){}

    public Item(String itemHeadline, String itemDescription){
        this.itemHeadline = itemHeadline;
        this.itemDescription = itemDescription;
    }

    public Item(int itemId, String itemHeadline, String itemDescription, Date itemRecieved,
                Date itemDatingFrom, Date itemDatingTo, String donator, String producer, String postalCode){
        this.itemId = itemId;
        this.itemHeadline = itemHeadline;
        this.itemDescription = itemDescription;
        this.itemRecieved = itemRecieved;
        this.itemDatingFrom = itemDatingFrom;
        this.itemDatingTo = itemDatingTo;
        this.donator = donator;
        this.producer = producer;
        this.postalCode = postalCode;
    }

    public JSONObject toJSON(){
        JSONObject item = new JSONObject();
        try{
            item.put("itemid", itemId);
            item.put("itemheadline", itemHeadline);
            item.put("itemdescription", itemDescription);
            item.put("itemreceived", this.getItemRecievedAsString());
            item.put("itemdatingfrom", this.getItemDatingFromAsString());
            item.put("itemdatingto", this.getItemDatingToAsString());
            item.put("donator", donator);
            item.put("producer", producer);
            item.put("postalCode", postalCode);
        } catch (JSONException e){

            e.printStackTrace();
            return null;
        }
        return item;
    }

    public Item(Parcel in){

        this.itemId = in.readInt();
        this.itemHeadline = in.readString();
        this.itemDescription = in.readString();
        String itemRecievedString = in.readString();
        String itemDatingFromString = in.readString();
        String itemDatingToString = in.readString();

        System.out.println(itemRecievedString);
        System.out.println(itemDatingFromString);
        System.out.println(itemDatingToString);

        try {
            this.itemRecieved = ((itemRecievedString == null) ? null : formatter.parse(itemRecievedString));
            this.itemDatingFrom = ((itemDatingFromString == null) ? null : formatter.parse(itemDatingFromString));
            this.itemDatingTo = ((itemDatingToString == null) ? null : formatter.parse(itemDatingToString));
        } catch (ParseException e){
            e.printStackTrace();
            return;
        }
        this.donator = in.readString();
        this.producer = in.readString();
        this.postalCode = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        String[] data = new String[9];

        dest.writeInt(this.itemId);
        dest.writeString(this.itemHeadline);
        dest.writeString(this.itemDescription);
        dest.writeString(this.getItemRecievedAsString());
        dest.writeString(this.getItemDatingFromAsString());
        dest.writeString(this.getItemDatingToAsString());
        dest.writeString(donator);
        dest.writeString(producer);
        dest.writeString(postalCode);
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

    public String getItemRecievedAsString() {return ((this.itemRecieved == null) ? null : formatter.format(this.itemRecieved));}

    public void setItemRecieved(Date itemRecieved) {
        this.itemRecieved = itemRecieved;
    }

    public Date getItemDatingFrom() {
        return itemDatingFrom;
    }

    public String getItemDatingFromAsString() {return ((this.itemDatingFrom == null) ? null : formatter.format(this.itemDatingFrom));}

    public void setItemDatingFrom(Date itemDatingFrom) {
        this.itemDatingFrom = itemDatingFrom;
    }

    public Date getItemDatingTo() {
        return itemDatingTo;
    }

    public String getItemDatingToAsString() {return ((this.itemDatingTo == null) ? null : formatter.format(this.itemDatingTo));}

    public void setItemDatingTo(Date itemDatingTo) {
        this.itemDatingTo = itemDatingTo;
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
