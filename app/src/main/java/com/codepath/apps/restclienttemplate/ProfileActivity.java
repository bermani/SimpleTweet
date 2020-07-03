package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityProfileBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class ProfileActivity extends AppCompatActivity {

    public static final String TAG = "ProfileActivity";

    ActivityProfileBinding binding;
    TwitterClient client;
    User user;
    List<User> users;
    UserAdapter adapter;
    MenuItem miActionProgressItem;

    int cursor;

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

        binding.tabLayout.getTabAt(0).setText(user.followers.toString() + " FOLLOWERS");
        binding.tabLayout.getTabAt(1).setText(user.followings.toString() + " FOLLOWING");

        users = new ArrayList<>();
        adapter = new UserAdapter(this, users, client);
        cursor = -1;

        LinearLayoutManager manager = new LinearLayoutManager(this);
        binding.rvUsers.setLayoutManager(manager);
        binding.rvUsers.setAdapter(adapter);
        binding.rvUsers.addOnScrollListener(new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (binding.tabLayout.getSelectedTabPosition() == 0) {
                    getFollowers();
                } else if (binding.tabLayout.getSelectedTabPosition() == 1) {
                    getFollowings();
                }
            }
        });

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                adapter.clear();
                cursor = -1;
                if (binding.tabLayout.getSelectedTabPosition() == 0) {
                    getFollowers();
                } else if (binding.tabLayout.getSelectedTabPosition() == 1) {
                    getFollowings();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        getFollowers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu to add the compose button
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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
        if (miActionProgressItem != null) {
            miActionProgressItem.setVisible(true);
        }
    }

    public void hideProgressBar() {
        // Hide progress item
        if (miActionProgressItem != null) {
            miActionProgressItem.setVisible(false);
        }
    }

    private void getFollowers() {
        if (cursor == 0) {
            return;
        }
        showProgressBar();
        client.getFollowers(user.id_str, cursor ,new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG,"onSuccess" + json.toString());
                hideProgressBar();
                try {
                    List<User> newUsers = User.fromJsonArray(json.jsonObject.getJSONArray("users"));
                    adapter.addAll(newUsers);
                    cursor = json.jsonObject.getInt("next_cursor");
                } catch (JSONException e) {
                    Log.e(TAG,"Json exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG,"onFailure" + response, throwable);
                hideProgressBar();
                Toast.makeText(getApplicationContext(), "Error: " + response, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getFollowings() {
        if (cursor == 0) {
            return;
        }
        showProgressBar();
        client.getFollowings(user.id_str, cursor ,new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG,"onSuccess" + json.toString());
                hideProgressBar();
                try {
                    List<User> newUsers = User.fromJsonArray(json.jsonObject.getJSONArray("users"));
                    adapter.addAll(newUsers);
                    cursor = json.jsonObject.getInt("next_cursor");
                } catch (JSONException e) {
                    Log.e(TAG,"Json exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG,"onFailure" + response, throwable);
                hideProgressBar();
                Toast.makeText(getApplicationContext(), "Error: " + response, Toast.LENGTH_LONG).show();
            }
        });
    }


}