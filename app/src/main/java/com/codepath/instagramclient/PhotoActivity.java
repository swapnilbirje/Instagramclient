package com.codepath.instagramclient;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class PhotoActivity extends AppCompatActivity {
    public static String CLIENT_ID = "e05c462ebd86446ea48a5af73769b602";
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotosAdapter aPhotos;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        /* set custome typeface for action bar
        final int titleId =
                Resources.getSystem().getIdentifier("action_bar_title", "id", getPackageName());
        findViewById(titleId);
        */
        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                aPhotos.clear();
                fetchPopularPhotos();
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        photos = new ArrayList<>();
        //create the adpter ..linking it to resource
        aPhotos = new InstagramPhotosAdapter(this,photos);
        //find list view from layout
        //then finally set the adapter binding it to the List view
        ListView lvPhotos = (ListView) findViewById(R.id.lvPhotos);
        //attach
        lvPhotos.setAdapter(aPhotos);
        //Send OUT API call to instagram to fetch photos
        fetchPopularPhotos();


    }

    private void fetchPopularPhotos() {
        /* clientID e05c462ebd86446ea48a5af73769b602
            URL for popular
            https://api.instagram.com/v1/media/popular?access_token=ACCESS-TOKEN
            “data” [x]==>  type} either image or video
            Caption :  data [x] ==> caption ==> text
            image : data [x] => images = > standard_resolution = > url
            username : Data => [x] user => username
        */
        //iterate over the response
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.instagram.com/v1/media/popular?client_id="+CLIENT_ID;
        //url, params are null and Response handler is a callback for the async call
        client.get(url,null,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                JSONArray photosJSON;
                Log.d(getClass().toString(), "Instagram Response: " + response.toString());
                try {
                    photosJSON = response.getJSONArray("data");
                    for(int i=0; i < photosJSON.length(); i++) {
                        JSONObject photoJSON = photosJSON.getJSONObject(i);
                        InstagramPhoto photo = new InstagramPhoto();
                        photo.username = photoJSON.getJSONObject("user").getString("username");
                        photo.caption = photoJSON.optJSONObject("caption") == null ?
                                null: photoJSON.optJSONObject("caption").getString("text");
                        photo.imageUrl = photoJSON.getJSONObject("images")
                                .getJSONObject("standard_resolution").getString("url");

                        photo.imageHeight = photoJSON.getJSONObject("images")
                                .getJSONObject("standard_resolution").getInt("height");

                        photo.likesCount = photoJSON.getJSONObject("likes").getInt("count");

                        photo.createdTime = photoJSON.getLong("created_time");
                        photo.profilePicUrl =
                                photoJSON.getJSONObject("user").optString("profile_picture");
                        photos.add(photo);
                    }
                } catch (JSONException e) {
                    Log.e(getClass().toString(), "Error parsing JSON", e);
                }
                Log.i("Response:",response.toString());
                aPhotos.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });

    }
}
