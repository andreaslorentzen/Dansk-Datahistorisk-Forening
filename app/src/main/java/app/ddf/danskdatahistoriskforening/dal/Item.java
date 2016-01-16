package app.ddf.danskdatahistoriskforening.dal;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import app.ddf.danskdatahistoriskforening.Model;

public class Item implements Parcelable {

    private int itemId;
    private String itemHeadline;
    private String itemDescription;
    private Date itemRecieved;
    private Date itemDatingFrom;
    private Date itemDatingTo;
    private String donator;
    private String producer;
    private String postalCode;
    private ArrayList<Uri> pictures;
    private ArrayList<Uri> recordings;
    private ArrayList<Uri> addedRecordings;
    private boolean picturesChanged;
    private boolean recordingsChanged;
    private ArrayList<Uri> deletedPictures;
    private ArrayList<Uri> addedPictures;

    public Item() {
    }

    public Item(String itemHeadline, String itemDescription) {
        this.itemHeadline = itemHeadline;
        this.itemDescription = itemDescription;
    }

    public Item(int itemId, String itemHeadline, String itemDescription, Date itemRecieved,
                Date itemDatingFrom, Date itemDatingTo, String donator, String producer, String postalCode) {
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

    public JSONObject toJSON() {
        JSONObject item = new JSONObject();
        try {
            item.put("itemid", itemId);
            item.put("itemheadline", itemHeadline);
            item.put("itemdescription", itemDescription);
            item.put("itemreceived", this.getItemRecievedAsString());
            item.put("itemdatingfrom", this.getItemDatingFromAsString());
            item.put("itemdatingto", this.getItemDatingToAsString());
            item.put("donator", donator);
            item.put("producer", producer);
            item.put("postalCode", postalCode);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return item;
    }

    public Item(Parcel in) {

        this.itemId = in.readInt();
        this.itemHeadline = in.readString();
        this.itemDescription = in.readString();
        String itemRecievedString = in.readString();
        String itemDatingFromString = in.readString();
        String itemDatingToString = in.readString();

        try {
            this.itemRecieved = ((itemRecievedString == null) ? null : Model.getFormatter().parse(itemRecievedString));
            this.itemDatingFrom = ((itemDatingFromString == null) ? null : Model.getFormatter().parse(itemDatingFromString));
            this.itemDatingTo = ((itemDatingToString == null) ? null : Model.getFormatter().parse(itemDatingToString));
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        this.donator = in.readString();
        this.producer = in.readString();
        this.postalCode = in.readString();
        this.pictures = in.readArrayList(Uri.class.getClassLoader());
        this.recordings = in.readArrayList(Uri.class.getClassLoader());
        this.addedRecordings = in.readArrayList(Uri.class.getClassLoader());
        this.picturesChanged = in.readInt() == 1;
        this.recordingsChanged = in.readInt() == 1;
        this.deletedPictures = in.readArrayList(Uri.class.getClassLoader());
        this.addedPictures = in.readArrayList(Uri.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.itemId);
        dest.writeString(this.itemHeadline);
        dest.writeString(this.itemDescription);
        dest.writeString(this.getItemRecievedAsString());
        dest.writeString(this.getItemDatingFromAsString());
        dest.writeString(this.getItemDatingToAsString());
        dest.writeString(donator);
        dest.writeString(producer);
        dest.writeString(postalCode);
        dest.writeList(pictures);
        dest.writeList(recordings);
        dest.writeList(addedRecordings);
        dest.writeInt(picturesChanged ? 1 : 0);
        dest.writeInt(recordingsChanged ? 1 : 0);
        dest.writeList(deletedPictures);
        dest.writeList(addedPictures);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
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

    public String getItemRecievedAsString() {
        return ((this.itemRecieved == null) ? null : Model.getFormatter().format(this.itemRecieved));
    }

    public void setItemRecieved(Date itemRecieved) {
        this.itemRecieved = itemRecieved;
    }

    public Date getItemDatingFrom() {
        return itemDatingFrom;
    }

    public String getItemDatingFromAsString() {
        return ((this.itemDatingFrom == null) ? null : Model.getFormatter().format(this.itemDatingFrom));
    }

    public void setItemDatingFrom(Date itemDatingFrom) {
        this.itemDatingFrom = itemDatingFrom;
    }

    public Date getItemDatingTo() {
        return itemDatingTo;
    }

    public String getItemDatingToAsString() {
        return ((this.itemDatingTo == null) ? null : Model.getFormatter().format(this.itemDatingTo));
    }

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

    public ArrayList<Uri> getPictures() {
        return this.pictures;
    }

    public void setPictures(ArrayList<Uri> pictures) {
        this.pictures = pictures;
    }

    public void addToPictures(Uri picture) {
        if (pictures == null)
            pictures = new ArrayList<Uri>();
        pictures.add(picture);
    }

    public void removeFromPictures(Uri uri){
        if(pictures != null){
            if (pictures.contains(uri)){
                pictures.remove(uri);
            }
        }
    }

    public ArrayList<Uri> getRecordings() {
        return this.recordings;
    }

    public ArrayList<Uri> getAddedRecordings() {
        return this.addedRecordings;
    }

    public void setRecordings(ArrayList<Uri> recordings) {
        this.recordings = recordings;
    }
    public void setAddedRecordings(ArrayList<Uri> addedRecordings) {
        this.recordings = recordings;
    }

    public void addToRecordings(Uri recording) {
        if (recordings == null)
            recordings = new ArrayList<Uri>();
        recordings.add(recording);
    }

    public void addToAddRecordings(Uri addedRecording) {
        if (addedRecordings == null)
            addedRecordings = new ArrayList<Uri>();
        addedRecordings.add(addedRecording);
    }

    public boolean hasPicturesChanged() {
        return picturesChanged;
    }

    public void setPicturesChanged(boolean picturesChanged) {
        this.picturesChanged = picturesChanged;
    }

    public boolean isRecordingsChanged() {
        return recordingsChanged;
    }

    public void setRecordingsChanged(boolean recordingsChanged) {
        this.recordingsChanged = recordingsChanged;
    }

    public ArrayList<Uri> getDeletedPictures() {
        return deletedPictures;
    }

    public void setDeletedPictures(ArrayList<Uri> deletedPictures) {
        this.deletedPictures = deletedPictures;
    }

    public void addDeletedPicture(Uri uri) {
        if (deletedPictures == null)
            deletedPictures = new ArrayList<Uri>();
        deletedPictures.add(uri);
    }

    public void removeFromDeletedPicture(Uri uri) {
        if (deletedPictures.contains(uri)) {
            if (deletedPictures.size() == 0) {
                deletedPictures = null;
            } else {
                deletedPictures.remove(uri);
            }
        }
    }

    public ArrayList<Uri> getAddedPictures() {
        return addedPictures;
    }

    public void setAddedPictures(ArrayList<Uri> addedPictures) {
        this.addedPictures = addedPictures;
    }

    public void addToAddedPictures(Uri uri) {
        if (addedPictures == null)
            addedPictures = new ArrayList<Uri>();
        addedPictures.add(uri);
    }

    public void removeFromAddedPicture(Uri uri) {
        if (addedPictures.contains(uri)) {
            if (addedPictures.size() == 0) {
                addedPictures = null;
            } else {
                addedPictures.remove(uri);
            }
        }
    }
}
