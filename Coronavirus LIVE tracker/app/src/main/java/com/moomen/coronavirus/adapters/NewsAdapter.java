package com.moomen.coronavirus.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moomen.coronavirus.R;
import com.moomen.coronavirus.model.News;
import com.moomen.coronavirus.ui.WebViewActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder>{
    private ArrayList<News> data;
    private static Context context;

    public NewsAdapter(ArrayList<News> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.news_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        News currentData = data.get(position);
        Picasso.get()
                .load(currentData.getImageUrl())
                .error(R.drawable.ic_error_black_24dp)
                .into(holder.icon);
        holder.title.setText(currentData.getTitle());
        holder.description.setText(currentData.getDescription());
        holder.dateDiff.setText(currentData.getTimeDiff());
        holder.date.setText(currentData.getDate());
        holder.sourceNameTV.setText(currentData.getSourceName());
        holder.setUrl(currentData.getUrl());
        holder.setSourceName(currentData.getSourceName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView entireListItem;
        ImageView icon;
        TextView title;
        TextView description;
        TextView dateDiff;
        TextView date;
        TextView sourceNameTV;
        String url;
        String sourceName;

        public ViewHolder(View itemView) {
            super(itemView);
            this.icon = itemView.findViewById(R.id.news_icon);
            this.title = itemView.findViewById(R.id.text_view_title_news_item_id);
            this.description = itemView.findViewById(R.id.text_view_description_news_item_id);
            this.dateDiff = itemView.findViewById(R.id.text_view_published_at_news_item_id);
            this.date = itemView.findViewById(R.id.text_view_full_date_id);
            this.entireListItem = itemView.findViewById(R.id.entire_view_news_item);
            this.sourceNameTV = itemView.findViewById(R.id.text_view_source_news_item_id);
            entireListItem.setOnClickListener(this);
        }

        void setUrl(String url) {
            this.url = url;
        }

        void setSourceName(String sourceName) {
            this.sourceName = sourceName;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(WebViewActivity.ARTICLE_NEWS_URL, url);
            intent.putExtra(WebViewActivity.NEWS_SOURCE, sourceName);
            context.startActivity(intent);
        }
    }
}
