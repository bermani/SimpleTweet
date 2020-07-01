package com.codepath.apps.restclienttemplate;

import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.databinding.ActivityDetailBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding binding;
    Tweet tweet;
    MenuItem miActionProgressItem;
    TwitterClient client;
    Integer position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        client = TwitterApp.getRestClient(this);

        Intent i = getIntent();
        tweet = Parcels.unwrap(i.getParcelableExtra("tweet"));
        position = i.getIntExtra("position", -1);

        binding.item.tvBody.setText(tweet.body);
        binding.item.tvScreenName.setText(tweet.user.screenName);
        binding.item.tvRelativeTime.setText(tweet.relativeTime);
        binding.item.ivReplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, ComposeActivity.class);
                intent.putExtra("tweet", Parcels.wrap(tweet));
                startActivityForResult(intent, TimelineActivity.COMPOSE_CODE);
            }
        });
        if (tweet.media_url.isEmpty()) {
            binding.item.ivMediaImage.setVisibility(View.GONE);
        } else {
            binding.item.ivMediaImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(tweet.media_url).override(tweet.media_width, tweet.media_height).into(binding.item.ivMediaImage);
        }
        Glide.with(this).load(tweet.user.profileImageUrl).into(binding.item.ivProfileImage);


        // LIKE AND FAVORITE CODE

        // initialize the button colors
        setRetweetButtonColor(binding.item.ivRetweetButton,tweet.retweeted);
        setFavoriteButtonColor(binding.item.ivFavoriteButton, tweet.favorited);

        // declare a response handler that toggles the tweet's retweeted state
        final JsonHttpResponseHandler retweetHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                tweet.toggleRetweeted();
                setRetweetButtonColor(binding.item.ivRetweetButton, tweet.retweeted);
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                hideProgressBar();
            }
        };

        // declare a response handler that toggles the tweet's favorited state
        final JsonHttpResponseHandler favoriteHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                tweet.toggleFavorited();
                setFavoriteButtonColor(binding.item.ivFavoriteButton, tweet.favorited);
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                hideProgressBar();
            }
        };

        // Unretweet if tweet is retweeted, and retweet if not, make API call
        binding.item.ivRetweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                if (tweet.retweeted) {
                    client.unretweetTweet(tweet.id_str,retweetHandler);
                } else {
                    client.retweetTweet(tweet.id_str, retweetHandler);
                }
            }
        });

        binding.item.ivFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                if (tweet.favorited) {
                    client.unfavoriteTweet(tweet.id_str, favoriteHandler);
                } else {
                    client.favoriteTweet(tweet.id_str, favoriteHandler);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra("tweet",Parcels.wrap(tweet));
        data.putExtra("position", position);
        setResult(RESULT_OK, data);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu to add the compose button
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);

        // Return to finish
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

    private void setTint(ImageView iv,  @ColorRes int colorRes) {
        ImageViewCompat.setImageTintList(iv, ColorStateList.valueOf(ContextCompat.getColor(this, colorRes)));
    }

    private void setRetweetButtonColor(ImageView ivRetweetButton, Boolean retweeted) {
        if (retweeted) {
            setTint(ivRetweetButton, R.color.inline_action_retweet);
            ivRetweetButton.setImageResource(R.drawable.ic_vector_retweet);
        } else {
            setTint(ivRetweetButton, R.color.inline_action);
            ivRetweetButton.setImageResource(R.drawable.ic_vector_retweet_stroke);
        }
    }

    private void setFavoriteButtonColor(ImageView ivFavoriteButton, Boolean favorited) {
        if (favorited) {
            setTint(ivFavoriteButton, R.color.inline_action_like);
            ivFavoriteButton.setImageResource(R.drawable.ic_vector_heart);
        } else {
            setTint(ivFavoriteButton, R.color.inline_action);
            ivFavoriteButton.setImageResource(R.drawable.ic_vector_heart_stroke);
        }
    }
}