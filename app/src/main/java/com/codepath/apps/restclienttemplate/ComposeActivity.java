package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 280;

    ActivityComposeBinding binding;

    TwitterClient client;

    MenuItem miActionProgressItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityComposeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        client = TwitterApp.getRestClient(this);
    }

    public void onButtonClick(View v) {
        String tweetContent = binding.etCompose.getText().toString();
        if (tweetContent.isEmpty()) {
            Toast.makeText(ComposeActivity.this, "Sorry, your tweet cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }
        if (tweetContent.length() > MAX_TWEET_LENGTH) {
            Toast.makeText(ComposeActivity.this, "Sorry, your tweet is too long", Toast.LENGTH_LONG).show();
            return;
        }
        showProgressBar();
        // Make an API call to Twitter to publish the tweet
        client.publishTweet(tweetContent, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                hideProgressBar();
                Log.i(TAG, "onSuccess to publish tweet");
                try {
                    Tweet tweet = Tweet.fromJson(json.jsonObject);
                    Log.i(TAG, "Published tweet says: " + tweet);
                    Intent intent = new Intent();
                    intent.putExtra("tweet", Parcels.wrap(tweet));
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                hideProgressBar();
                Toast.makeText(ComposeActivity.this, "Error: " + response, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu to add the progress indicator
        getMenuInflater().inflate(R.menu.menu_compose, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);

        return super.onPrepareOptionsMenu(menu);
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }
}
