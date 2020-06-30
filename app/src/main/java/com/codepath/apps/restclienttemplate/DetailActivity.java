package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.databinding.ActivityDetailBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding binding;

    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        binding.item.tvBody.setText(tweet.body);
        binding.item.tvScreenName.setText(tweet.user.screenName);
        binding.item.tvRelativeTime.setText(tweet.relativeTime);
        binding.item.ivReplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, ComposeActivity.class);
                intent.putExtra("tweet", Parcels.wrap(tweet));
                startActivityForResult(intent, TimelineActivity.REQUEST_CODE);
            }
        });
        if (tweet.media_url.isEmpty()) {
            binding.item.ivMediaImage.setVisibility(View.GONE);
        } else {
            binding.item.ivMediaImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(tweet.media_url).override(tweet.media_width, tweet.media_height).into(binding.item.ivMediaImage);
        }
        Glide.with(this).load(tweet.user.profileImageUrl).into(binding.item.ivProfileImage);
    }
}