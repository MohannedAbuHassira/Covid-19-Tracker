package com.moomen.coronavirus.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.moomen.coronavirus.databinding.FragmentCaseDetailsBinding;
import com.moomen.coronavirus.utils.Utils;
import com.moomen.coronavirus.adapters.CasesAdapter;
import com.moomen.coronavirus.databinding.FragmentCasesBinding;
import com.moomen.coronavirus.model.Case;
import com.moomen.coronavirus.network.CasesNetworkParser;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.moomen.coronavirus.R;

public class CasesFragment extends Fragment implements CasesNetworkParser.OnCasesFilledListener {
    private FragmentCasesBinding binding;

    private final String[] sortAsValues = {"Asc", "Desc"};

    private static ArrayList<Case> mCases = new ArrayList<>();

    private RecyclerView recyclerViewCases;
    private CasesAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private static int position = 1;

    private static int currentScrollPosition;

    public interface OnCasesFragmentClickedCaseItem {
        void onClickingCaseItem(Case clickedCase);
    }

    private OnCasesFragmentClickedCaseItem clickedCaseItemListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCasesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Spinner spinner = binding.sortSpinner;
        setUpSpinner(spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                position = i;
                if (mCases != null)
                    notifyAdapterWithSortValue(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final SearchView searchView = binding.searchId;
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.setSortValue(position);
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                notifyAdapterWithSortValue(position);
                return false;
            }
        });

        showDialogWhenNoInternet(getContext());

        return root;
    }

    private void setUpSpinner(Spinner spinner) {
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, sortAsValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(position);
    }

    private void downloadCases() {
        CasesNetworkParser parser = new CasesNetworkParser(getContext());
        parser.setOnCasesFilledListener(this);
        parser.parseCountriesCasesJson();
    }

    public void showDialogWhenNoInternet(final Context context) {
        if(!Utils.isNetworkAvailable(context) && mCases.size()==0)  // Don't show the dialog when cases are already loaded
            new AlertDialog.Builder(context)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Internet Connection Alert")
                    .setMessage("Please Check Your Internet Connection")
                    .setCancelable(false)
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            showDialogWhenNoInternet(context);
                        }
                    }).show();
        else
            downloadCases();
    }

    @Override
    public void startFillingCases(List<Case> cases) {
        showInvincibleViews();
        binding.progressBarCasesId.setVisibility(View.GONE);
        mCases = new ArrayList<>(cases);
        Case globalCase = cases.get(0);
        startFillingGlobalCases(globalCase);
        setUpPieChart(globalCase);
        mCases.remove(0);
        setUpRecycleViewWithSortValue();
    }

    private void setUpRecycleViewWithSortValue() {
        recyclerViewCases = binding.recyclerViewCasesId;
        recyclerViewCases.setVisibility(View.VISIBLE);
        adapter = new CasesAdapter(mCases, new CasesAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Case clickedCase) {
                        clickedCaseItemListener.onClickingCaseItem(clickedCase);
                    }
                }, getContext());

        recyclerViewCases.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewCases.setLayoutManager(linearLayoutManager);
        recyclerViewCases.setAdapter(adapter);
        notifyAdapterWithSortValue(position);
    }

    private void notifyAdapterWithSortValue(int position) {
        if (position == 1)
            Collections.sort(mCases, Collections.<Case>reverseOrder());
        else
            Collections.sort(mCases);
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }

    private void startFillingGlobalCases(Case currentCase) {
        binding.progressBarGlobalCaseId.setVisibility(View.GONE);
        binding.countryCaseDetails.textViewCountryId.setText("Global");
        binding.textViewNewConfirmedId.setText(String.valueOf(currentCase.getNewConfirmed()));
        binding.textViewNewDeathsId.setText(String.valueOf(currentCase.getNewDeaths()));
        binding.textViewCountConfirmedId.setText(String.valueOf(currentCase.getTotalConfirmed()));
        binding.textViewCountDeathsId.setText(String.valueOf(currentCase.getTotalDeaths()));
        binding.textViewCountRecoveredId.setText(String.valueOf(currentCase.getTotalRecovered()));
        binding.textViewNewRecoveredId.setText(String.valueOf(currentCase.getNewRecovered()));
    }

    private void showInvincibleViews() {
        binding.countryCaseDetails.countryIconId.setVisibility(View.VISIBLE);
        binding.countryCaseDetails.textViewCountryId.setVisibility(View.VISIBLE);
        binding.textViewConfirmedId.setVisibility(View.VISIBLE);
        binding.textViewRecoveredId.setVisibility(View.VISIBLE);
        binding.textViewDeathsId.setVisibility(View.VISIBLE);
        binding.textViewNewRecoveredId.setVisibility(View.VISIBLE);
        binding.icon1Id.setVisibility(View.VISIBLE);
        binding.icon2Id.setVisibility(View.VISIBLE);
        binding.icon3Id.setVisibility(View.VISIBLE);
    }

    private void setUpPieChart(Case globalCase) {
        PieChart pieChart = binding.countryCaseDetails.piechart;
        pieChart.addPieSlice(new PieModel("Recovered", globalCase.getTotalRecovered(), getResources().getColor(R.color.green_pie)));
        pieChart.addPieSlice(new PieModel("Confirmed", globalCase.getTotalConfirmed(), getResources().getColor(R.color.yellow_pie)));
        pieChart.addPieSlice(new PieModel("Deaths", globalCase.getTotalDeaths(), getResources().getColor(R.color.red_pie)));
        pieChart.startAnimation();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof OnCasesFragmentClickedCaseItem) {
            clickedCaseItemListener = (OnCasesFragmentClickedCaseItem) context;
        } else {
            throw new RuntimeException();
        }
    }

    public void setClickedCaseItemListener(OnCasesFragmentClickedCaseItem clickedCaseItemListener) {
        this.clickedCaseItemListener = clickedCaseItemListener;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(linearLayoutManager!=null)
            currentScrollPosition = linearLayoutManager.findFirstVisibleItemPosition();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(recyclerViewCases!=null)
            recyclerViewCases.scrollToPosition(currentScrollPosition);
    }
}
