package com.moomen.coronavirus.network;

import android.content.Context;
import android.os.Handler;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.moomen.coronavirus.utils.Utils;
import com.moomen.coronavirus.model.Case;
import com.moomen.coronavirus.model.CountryInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CasesNetworkParser {
    private static final int LIMIT_OF_COUNTRIES = 200;
    private Context context;

    // Old api
    public static final String CASES_SUMMARY =
            "https://coronavirus-19-api.herokuapp.com/countries";

    public static final String CASES_GLOBAL =
            "https://disease.sh/v3/covid-19/all?yesterday=true";
    public static final String CASES_COUNTRIES =
            "https://disease.sh/v3/covid-19/countries?yesterday=true&sort=cases";

    public interface OnCasesFilledListener {
        void startFillingCases(List<Case> cases);
    }

    private OnCasesFilledListener casesListener;

    public <T extends OnCasesFilledListener> void setOnCasesFilledListener(T casesOrMapFragment) {
        this.casesListener = casesOrMapFragment;
    }

    private static ArrayList<Case> mCases;

    public CasesNetworkParser(Context context){
        this.context = context;
    }

    public void parseCountriesCasesJson() {
        if(mCases != null) {
            if(casesListener != null)
                casesListener.startFillingCases(mCases);
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, CASES_GLOBAL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ArrayList<Case> listOfCases = new ArrayList<>();
                    JSONObject global = new JSONObject(response);

                    Case globalCase = fillCase(global, true);
                    listOfCases.add(globalCase);
                    parseCountriesCasesJson(listOfCases);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                casesListener.startFillingCases(null);
            }
        });
        queue.add(request);
    }

    public void parseMockCountriesCasesJson() {
        if(mCases != null) {
            if(casesListener != null)
                casesListener.startFillingCases(mCases);
            return;
        }
        try {
            ArrayList<Case> listOfCases = new ArrayList<>();
            String response = Utils.loadJSONFromAsset(context, "mock_global_json.txt");
            JSONObject global = new JSONObject(response);

            Case globalCase = fillCase(global, true);
            listOfCases.add(globalCase);
            parseMockCountriesCasesJson(listOfCases);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseCountriesCasesJson(final List<Case> listOfCases) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, CASES_COUNTRIES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray root = new JSONArray(response);
                    for(int i = 0; i<LIMIT_OF_COUNTRIES; i++) {
                        JSONObject country = root.getJSONObject(i);
                        Case currentCase = fillCase(country, false);
                        listOfCases.add(currentCase);
                    }
                    mCases = new ArrayList<>(listOfCases);
                    if(casesListener != null)
                        casesListener.startFillingCases(listOfCases);
                }catch (JSONException ex) { }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                casesListener.startFillingCases(null);
            }
        });
        queue.add(request);
    }

    private void parseMockCountriesCasesJson(final List<Case> listOfCases) {
        try {
            String response = Utils.loadJSONFromAsset(this.context, "mock_countries_json.txt");
            JSONArray root = new JSONArray(response);
            for (int i = 0; i < LIMIT_OF_COUNTRIES; i++) {
                JSONObject country = root.getJSONObject(i);
                Case currentCase = fillCase(country, false);
                listOfCases.add(currentCase);
            }
            mCases = new ArrayList<>(listOfCases);
            if (casesListener != null)
                casesListener.startFillingCases(listOfCases);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Case fillCase(JSONObject jsonObject, boolean isGlobal) {
        Case currentCase = new Case();
        try {
            if(isGlobal)
                currentCase.setGlobal(true);
            currentCase.setNewConfirmed(jsonObject.getInt("todayCases"));
            currentCase.setTotalConfirmed(jsonObject.getInt("cases"));
            currentCase.setNewDeaths(jsonObject.getInt("todayDeaths"));
            currentCase.setTotalDeaths(jsonObject.getInt("deaths"));
            currentCase.setTotalRecovered(jsonObject.getInt("recovered"));
            currentCase.setNewRecovered(jsonObject.getInt("todayRecovered"));
            if(isGlobal)
                currentCase.setName("GLOBAL");
            else
                currentCase.setName(jsonObject.getString("country"));
            if(isGlobal)
                return currentCase;
            JSONObject countryInfo = jsonObject.getJSONObject("countryInfo");
            currentCase.setLatLng(
                    new LatLng(
                            countryInfo.getInt("lat"),
                            countryInfo.getInt("long")
                    )
            );
            currentCase.setCountryCode(countryInfo.getString("iso2"));
            currentCase.setFlagUrl(countryInfo.getString("flag"));
            return currentCase;
        }catch (JSONException ex) { return currentCase; }
    }
}

