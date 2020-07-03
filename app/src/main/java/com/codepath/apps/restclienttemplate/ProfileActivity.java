package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityProfileBinding;
import com.codepath.apps.restclienttemplate.models.User;

import org.parceler.Parcels;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    TwitterClient client;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        client = TwitterApp.getRestClient(this);
        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        Glide.with(this).load(user.profileImageUrl).into(binding.ivProfileImage);
        binding.tvName.setText(user.screenName);
        binding.tvBio.setText(user.bio);
    }
}