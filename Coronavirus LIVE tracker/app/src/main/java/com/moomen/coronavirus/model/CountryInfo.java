package com.moomen.coronavirus.model;

import com.google.android.gms.maps.model.LatLng;

public class CountryInfo {
    private LatLng latLng;
    private String flagUrl;

    public CountryInfo(LatLng latLng, String flagUrl) {
        this.latLng = latLng;
        this.flagUrl = flagUrl;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getFlagUrl() {
        return flagUrl;
    }

    public void setFlagUrl(String flagUrl) {
        this.flagUrl = flagUrl;
    }
}

