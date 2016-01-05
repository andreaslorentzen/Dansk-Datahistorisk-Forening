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
        String[] data = new String[9];

        in.readStringArray(data);
        this.itemId = Integer.getInteger(data[0]);
        this.itemHeadline = data[1];
        this.itemDescription = data[2];
        try {
            this.itemRecieved = ((data[3] == null) ? null : formatter.parse(data[3]));
            this.itemDatingFrom = ((data[4] == null) ? null : formatter.parse(data[4]));
            this.itemDatingTo = ((data[5] == null) ? null : formatter.parse(data[5]));
        } catch (ParseException e){
            e.printStackTrace();
            return;
        }
        this.donator = data[6];
        this.producer = data[7];
        this.postalCode = data[8];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String[] data = new String[9];


        data[0] = Integer.toString(this.itemId);
        data[1] = this.itemHeadline;
        data[2] = this.itemDescription;
        data[3] = this.getItemRecievedAsString();
        data[4] = this.getItemDatingFromAsString();
        data[5] = this.getItemDatingToAsString();
        data[6] = donator;
        data[7] = producer;
        data[8] = postalCode;
        dest.writeStringArray(data);
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
