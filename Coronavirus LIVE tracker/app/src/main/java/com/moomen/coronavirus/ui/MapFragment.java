package com.moomen.coronavirus.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;
import com.moomen.coronavirus.R;
import com.moomen.coronavirus.databinding.FragmentMapBinding;
import com.moomen.coronavirus.model.Case;
import com.moomen.coronavirus.network.CasesNetworkParser;
import com.moomen.coronavirus.utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, CasesNetworkParser.OnCasesFilledListener {
    private FragmentMapBinding binding;

    private MapView map;

    private GoogleMap googleMap;

    private List<Case> mCases;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setUpMapView(savedInstanceState);

        return root;
    }

    private void setUpMapView(Bundle savedInstanceState) {
        map = binding.getRoot().findViewById(R.id.map_id);
        map.onCreate(savedInstanceState);
        map.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        CasesNetworkParser parser = new CasesNetworkParser(getContext());
        parser.setOnCasesFilledListener(this);
        parser.parseCountriesCasesJson();

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(final Marker marker) {
                final Case markerCase = (Case) marker.getTag();
                View window = LayoutInflater.from(getContext()).inflate(R.layout.case_item, null);

                TextView name = window.findViewById(R.id.text_view_country_id);
                TextView newConfirmed = window.findViewById(R.id.text_view_new_confirmed_id);
                TextView newDeaths = window.findViewById(R.id.text_view_new_deaths_id);
                TextView newRecovered = window.findViewById(R.id.text_view_new_recovered_id);
                TextView confirmed = window.findViewById(R.id.text_view_count_confirmed_id);
                TextView deaths = window.findViewById(R.id.text_view_count_recovered_id);
                TextView recovered = window.findViewById(R.id.text_view_count_deaths_id);

                ImageView flag = window.findViewById(R.id.image_view_flag_id);
                Picasso.get()
                        .load(markerCase.getFlagUrl())
                        .into(flag, new Callback() {
                            @Override
                            public void onSuccess() {
                                if (markerCase.canUpdateWindow()) {
                                    markerCase.setCanUpdateWindow(false);
                                    marker.showInfoWindow();
                                }
                            }

                            @Override
                            public void onError(Exception e) { }
                        });
                name.setText(markerCase.getName());
                newConfirmed.setText(Integer.toString(markerCase.getNewConfirmed()));
                newDeaths.setText(Integer.toString(markerCase.getNewDeaths()));
                confirmed.setText(Integer.toString(markerCase.getTotalConfirmed()));
                deaths.setText(Integer.toString(markerCase.getTotalDeaths()));
                recovered.setText(Integer.toString(markerCase.getTotalRecovered()));
                return window;
            };

            @Override
            public View getInfoContents(final Marker marker) {
                final Case markerCase = (Case) marker.getTag();
                View window = LayoutInflater.from(getContext()).inflate(R.layout.case_item, null);

                TextView name = window.findViewById(R.id.text_view_country_id);
                TextView newConfirmed = window.findViewById(R.id.text_view_new_confirmed_id);
                TextView newDeaths = window.findViewById(R.id.text_view_new_deaths_id);
                TextView newRecovered = window.findViewById(R.id.text_view_new_recovered_id);
                TextView confirmed = window.findViewById(R.id.text_view_count_confirmed_id);
                TextView deaths = window.findViewById(R.id.text_view_count_deaths_id);
                TextView recovered = window.findViewById(R.id.text_view_count_recovered_id);

                ImageView flag = window.findViewById(R.id.image_view_flag_id);
                Picasso.get()
                        .load(markerCase.getFlagUrl())
                        .into(flag, new Callback() {
                            @Override
                            public void onSuccess() {
                                if (markerCase.canUpdateWindow()) {
                                    markerCase.setCanUpdateWindow(false);
                                    marker.showInfoWindow();
                                }
                            }

                            @Override
                            public void onError(Exception e) { }
                        });

                name.setText(markerCase.getName());
                newConfirmed.setText(Integer.toString(markerCase.getNewConfirmed()));
                newDeaths.setText(Integer.toString(markerCase.getNewDeaths()));
                newRecovered.setText(Integer.toString(markerCase.getNewRecovered()));
                confirmed.setText(Integer.toString(markerCase.getTotalConfirmed()));
                deaths.setText(Integer.toString(markerCase.getTotalDeaths()));
                recovered.setText(Integer.toString(markerCase.getTotalRecovered()));
                return window;
            }
        });
    }

    @Override
    public void startFillingCases(List<Case> cases) {
        mCases = cases;
        int maxCountryCases = cases.get(1).getTotalConfirmed();
        Case globalCase = cases.get(0); // TODO Do something with it
        for (int i = 1 ; i<cases.size() ; i++) {
            Case currentCase = cases.get(i);

            // Handle the case for the country with country code of DO, since do is a java keyword
            int resourceId = 0;
            if(!currentCase.getCountryCode().equals("DO"))
                resourceId = getResources().getIdentifier(currentCase.getCountryCode().toLowerCase(), "raw", getContext().getPackageName());
            else
                resourceId = getResources().getIdentifier("country_do", "raw", getContext().getPackageName());

            GeoJsonLayer layer;
            try {
                layer = new GeoJsonLayer(googleMap, resourceId, getContext());
            } catch (Exception e) {
                continue;
            }
            GeoJsonPolygonStyle style = layer.getDefaultPolygonStyle();
            style.setFillColor(Utils.colorCountryBasedOnCases(maxCountryCases, currentCase.getTotalConfirmed()));
            style.setStrokeWidth(0.5F);
            layer.addLayerToMap();

            LatLng latLng = currentCase.getLatLng();
            googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(Utils.resizeMapIcons(getContext(), getResources().getResourceName(R.drawable.map_marker2), 16, 16))))
                .setTag(currentCase);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        map.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        map.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        map.onLowMemory();
    }
}