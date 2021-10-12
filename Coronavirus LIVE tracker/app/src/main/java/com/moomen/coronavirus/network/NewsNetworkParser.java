package com.moomen.coronavirus.network;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.moomen.coronavirus.utils.Utils;
import com.moomen.coronavirus.model.News;
import com.moomen.coronavirus.ui.NewsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;

public class NewsNetworkParser {
    private Context context;
    public static final String NEWS_EVERYTHING =
            "https://newsapi.org/v2/everything?language=en&q=covid%20OR%20corona&sortBy=publishedAt&pageSize=20&sortBy=popularity&apiKey=71419b7f83494d58b3fd8c166063d98b";

    public interface OnNewsFilledListener {
        void startFillingNews(List<News> articles);
    }

    private OnNewsFilledListener newsListener;

    public void setOnNewsFilledListener(NewsFragment newsListener){
        this.newsListener = newsListener;
    }

    private static ArrayList<News> mNews = new ArrayList<>();
    private static int currentPageNumber = 0;

    public NewsNetworkParser(Context context) {
        this.context = context;
    }

    public void parseEverythingJson(final int currentPageNumber) {
        if(NewsNetworkParser.currentPageNumber == currentPageNumber) {
            if(newsListener != null)
                newsListener.startFillingNews(mNews);
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(context);
        Uri builtUri = Uri.parse(NEWS_EVERYTHING)
                .buildUpon()
                .appendQueryParameter("page", String.valueOf(currentPageNumber))
                .build();
        StringRequest request = new StringRequest(Request.Method.GET, builtUri.toString(), new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject root = new JSONObject(response);
                    JSONArray articles = root.getJSONArray("articles");
                    ArrayList<News> listOfArticles = new ArrayList<>();
                    for(int i = 0; i<articles.length(); i++) {
                        JSONObject article = articles.getJSONObject(i);
                        String sourceName = article.getJSONObject("source").getString("name");
                        News news = new News(article.getString("urlToImage"), article.getString("title"),
                                article.getString("description"), "",
                                article.getString("url"), sourceName);
                        String publishedAt = article.getString("publishedAt");
                        news.setTimeDiff(Utils.DateToTimeFormat(publishedAt));
                        news.setDate(Utils.DateFormat(publishedAt));
                        if(news.isValidNews())
                            listOfArticles.add(news);
                    }
                    mNews.addAll(listOfArticles);
                    NewsNetworkParser.currentPageNumber++;
                    if(newsListener != null)
                        newsListener.startFillingNews(mNews);
                }catch (JSONException ex) { }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        parseEverythingJson(currentPageNumber);
                    }
                }, 500);
            }
        }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "Mozilla/5.0");
                return headers;
            }
        };
        queue.add(request);
    }
}
