package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;

public class ComposeActivity extends AppCompatActivity {

    ActivityComposeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityComposeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    public void onButtonClick(View v) {
        String tweetContent = binding.etCompose.getText().toString();
        if (tweetContent.isEmpty()) {
            Toast.makeText(ComposeActivity.this, "Sorry, your tweet cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }
        if (tweetContent.length() > 280) {
            Toast.makeText(ComposeActivity.this, "Sorry, your tweet is too long", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_LONG).show();
        // Make an API call to Twitter to publish the tweet
    }
}