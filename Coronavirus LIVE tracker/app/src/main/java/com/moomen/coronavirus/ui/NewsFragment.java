package com.moomen.coronavirus.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.moomen.coronavirus.utils.Utils;
import com.moomen.coronavirus.adapters.NewsAdapter;
import com.moomen.coronavirus.databinding.FragmentNewsBinding;
import com.moomen.coronavirus.model.News;
import com.moomen.coronavirus.model.SlidPager;
import com.moomen.coronavirus.network.NewsNetworkParser;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment implements NewsNetworkParser.OnNewsFilledListener {
    private FragmentNewsBinding binding;

    private NewsNetworkParser parser;

    private boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;

    private static int currentPageNumber = 1;

    private RecyclerView recyclerViewNews;
    private NewsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private static ArrayList<News> mArticleNews = new ArrayList<>();
    private static ArrayList<News> mTopNews = new ArrayList<>();

    private static int currentScrollPosition;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerViewNews = binding.recyclerViewNewsId;
        adapter = new NewsAdapter(mArticleNews, getContext());
        layoutManager = new LinearLayoutManager(getContext());
        recyclerViewNews.setLayoutManager(layoutManager);
        recyclerViewNews.setAdapter(adapter);

        showDialogWhenNoInternet(getContext());
        updateWhenUserWantsMoreNews();
        return root;
    }

    private void updateWhenUserWantsMoreNews() {
        recyclerViewNews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) { //check for scroll down
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
                            binding.progressBarAddMoreNewsId.setVisibility(View.VISIBLE);
                            currentPageNumber++;
                            parser.parseEverythingJson(currentPageNumber);
                        }
                    }
                }
            }
        });
    }

    private void downloadNews() {
        parser = new NewsNetworkParser(getContext());
        parser.setOnNewsFilledListener(this);
        binding.progressBarLoadFirstNewsId.setVisibility(View.VISIBLE);
        parser.parseEverythingJson(currentPageNumber);
    }

    @Override
    public void startFillingNews(List<News> articles) {
        loading = true;
        binding.progressBarLoadFirstNewsId.setVisibility(View.GONE);
        recyclerViewNews.setVisibility(View.VISIBLE);
        mArticleNews.clear();
        mArticleNews.addAll(articles);
        adapter.notifyDataSetChanged();
        binding.progressBarLoadFirstNewsId.setVisibility(View.GONE);
    }

    public void showDialogWhenNoInternet(final Context context) {
        if(!Utils.isNetworkAvailable(context) && mArticleNews.size()==0 && mTopNews.size()==0)  // Don't show the dialog when news are already loaded
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
        else {
            downloadNews();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(layoutManager !=null)
            currentScrollPosition = layoutManager.findFirstVisibleItemPosition();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(recyclerViewNews!=null)
            recyclerViewNews.scrollToPosition(currentScrollPosition);
    }
}
