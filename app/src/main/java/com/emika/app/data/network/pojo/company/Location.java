package com.emika.app.data.network.pojo.company;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location {

    @SerializedName("zip")
    @Expose
    private Object zip;
    @SerializedName("city")
    @Expose
    private Object city;
    @SerializedName("country_code")
    @Expose
    private Object countryCode;
    @SerializedName("country_name")
    @Expose
    private Object countryName;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("country_flag")
    @Expose
    private Object countryFlag;
    @SerializedName("country_flag_emoji")
    @Expose
    private Object countryFlagEmoji;
    @SerializedName("calling_code")
    @Expose
    private Object callingCode;

    public Object getZip() {
        return zip;
    }

    public void setZip(Object zip) {
        this.zip = zip;
    }

    public Object getCity() {
        return city;
    }

    public void setCity(Object city) {
        this.city = city;
    }

    public Object getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(Object countryCode) {
        this.countryCode = countryCode;
    }

    public Object getCountryName() {
        return countryName;
    }

    public void setCountryName(Object countryName) {
        this.countryName = countryName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Object getCountryFlag() {
        return countryFlag;
    }

    public void setCountryFlag(Object countryFlag) {
        this.countryFlag = countryFlag;
    }

    public Object getCountryFlagEmoji() {
        return countryFlagEmoji;
    }

    public void setCountryFlagEmoji(Object countryFlagEmoji) {
        this.countryFlagEmoji = countryFlagEmoji;
    }

    public Object getCallingCode() {
        return callingCode;
    }

    public void setCallingCode(Object callingCode) {
        this.callingCode = callingCode;
    }

}