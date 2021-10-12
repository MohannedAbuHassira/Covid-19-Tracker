package com.moomen.coronavirus.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Case implements Comparable<Case>, Serializable {
    private String countryCode;
    private String flagUrl;
    private LatLng latLng;
    private int newConfirmed;
    private int totalConfirmed;
    private int newDeaths;
    private int newRecovered;
    private int totalDeaths;
    private int totalRecovered;
    private String name;
    private boolean isGlobal = false;
    private boolean canUpdateWindow = true;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getFlagUrl() {
        return flagUrl;
    }

    public void setFlagUrl(String flagUrl) {
        this.flagUrl = flagUrl;
    }

    public int getNewConfirmed() {
        return newConfirmed;
    }

    public void setNewConfirmed(int newConfirmed) {
        this.newConfirmed = newConfirmed;
    }

    public int getTotalConfirmed() { return totalConfirmed; }

    public void setTotalConfirmed(int totalConfirmed) {
        this.totalConfirmed = totalConfirmed;
    }

    public int getNewDeaths() {
        return newDeaths;
    }

    public void setNewDeaths(int newDeaths) {
        this.newDeaths = newDeaths;
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public void setTotalDeaths(int totalDeaths) {
        this.totalDeaths = totalDeaths;
    }

    public int getTotalRecovered() {
        return totalRecovered;
    }

    public void setTotalRecovered(int totalRecovered) {
        this.totalRecovered = totalRecovered;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getNewRecovered() { return newRecovered; }

    public void setNewRecovered(int newRecovered) { this.newRecovered = newRecovered; }

    @Override
    public int compareTo(Case aCase) { return this.getTotalConfirmed() - aCase.getTotalConfirmed(); }

    public void setGlobal(boolean isGlobal) { this.isGlobal = isGlobal; }

    public boolean canUpdateWindow() { return canUpdateWindow; }

    public void setCanUpdateWindow(boolean canUpdateWindow) { this.canUpdateWindow = canUpdateWindow; }

}
