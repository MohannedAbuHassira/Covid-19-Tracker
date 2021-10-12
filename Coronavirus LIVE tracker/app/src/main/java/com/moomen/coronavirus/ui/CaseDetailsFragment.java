package com.moomen.coronavirus.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moomen.coronavirus.R;
import com.moomen.coronavirus.databinding.FragmentCaseDetailsBinding;
import com.moomen.coronavirus.databinding.FragmentNewsBinding;
import com.moomen.coronavirus.model.Case;

public class CaseDetailsFragment extends Fragment {

    public final static String CLICKED_CASE_KEY = "clickedCaseKey";
    private Case clickedCase;

    private FragmentCaseDetailsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCaseDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        clickedCase = (Case) getArguments().getSerializable(CLICKED_CASE_KEY);
        Log.d("!!!", clickedCase.getName());

        return root;
    }
}
