package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class DataClass implements Parcelable {
    private String dataTitle;
    private String dataDesc;
    private String dataLang;
    private String dataImage;
    private String key;
    private String userId;
    private String category;
    private String phoneNumber;
    private boolean callButtonPressed;

    public DataClass(String title, String desc, String lang, String imageURL) {
        this.dataTitle = title;
        this.dataDesc = desc;
        this.dataLang = lang;
        this.dataImage = imageURL;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isCallButtonPressed() {
        return callButtonPressed;
    }

    public void setCallButtonPressed(boolean callButtonPressed) {
        this.callButtonPressed = callButtonPressed;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDataTitle() {
        return dataTitle;
    }

    public String getDataDesc() {
        return dataDesc;
    }

    public String getDataLang() {
        return dataLang;
    }

    public String getDataImage() {
        return dataImage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public DataClass() {

    }

    protected DataClass(Parcel in) {
        dataTitle = in.readString();
        dataDesc = in.readString();
        dataLang = in.readString();
        dataImage = in.readString();
        key = in.readString();
        userId = in.readString();
        category = in.readString();
        phoneNumber = in.readString();
        callButtonPressed = in.readByte() != 0;
    }

    public static final Creator<DataClass> CREATOR = new Creator<DataClass>() {
        @Override
        public DataClass createFromParcel(Parcel in) {
            return new DataClass(in);
        }

        @Override
        public DataClass[] newArray(int size) {
            return new DataClass[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dataTitle);
        dest.writeString(dataDesc);
        dest.writeString(dataLang);
        dest.writeString(dataImage);
        dest.writeString(key);
        dest.writeString(userId);
        dest.writeString(category);
        dest.writeString(phoneNumber);
        dest.writeByte((byte) (callButtonPressed ? 1 : 0));
    }

    public boolean isFavorite() {
        return false;
    }
}
