package com.moomen.coronavirus.ui;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.moomen.coronavirus.R;
import com.moomen.coronavirus.model.Case;
import com.moomen.coronavirus.model.CountryInfo;
import com.moomen.coronavirus.network.CasesNetworkParser;
import com.moomen.coronavirus.network.NewsNetworkParser;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity implements CasesFragment.OnCasesFragmentClickedCaseItem {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        CasesFragment casesFragment = new CasesFragment();
        casesFragment.setClickedCaseItemListener(this);
    }

    @Override
    public void onClickingCaseItem(Case clickedCase) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CaseDetailsFragment.CLICKED_CASE_KEY, clickedCase);
        navController.navigate(R.id.navigation_case_details, bundle);
    }
}
