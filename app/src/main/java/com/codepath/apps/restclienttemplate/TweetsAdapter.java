package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.sql.Time;
import java.util.List;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;
    TwitterClient client;

    public TweetsAdapter(Context context, List<Tweet> tweets, TwitterClient client) {
        this.context = context;
        this.tweets = tweets;
        this.client = client;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at position
        Tweet tweet = tweets.get(position);
        // Bind the tweet with view holder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Pass in the context and list of tweets

    // For each row, inflate the layout

    // Bind values based on the position of the element

    // Define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemTweetBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemTweetBinding.bind(itemView);
        }

        public void bind(final Tweet tweet) {
            binding.tvBody.setText(tweet.body);
            binding.tvScreenName.setText(tweet.user.screenName);
            binding.tvRelativeTime.setText(tweet.relativeTime);

            final TimelineActivity timelineActivity = (TimelineActivity) context;

            // reply button click should go to compose activity
            binding.ivReplyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ComposeActivity.class);
                    intent.putExtra("tweet", Parcels.wrap(tweet));
                    timelineActivity.startActivityForResult(intent, TimelineActivity.COMPOSE_CODE);
                }
            });

            // profile image should go to profile activity
            binding.ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("user",Parcels.wrap(tweet.user));
                    timelineActivity.startActivity(intent);
                }
            });

            // initialize the button colors
            setRetweetButtonColor(binding.ivRetweetButton,tweet.retweeted);
            setFavoriteButtonColor(binding.ivFavoriteButton, tweet.favorited);

            // declare a response handler that toggles the tweet's retweeted state
            final JsonHttpResponseHandler retweetHandler = new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    tweet.toggleRetweeted();
                    setRetweetButtonColor(binding.ivRetweetButton, tweet.retweeted);
                    timelineActivity.hideProgressBar();
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    timelineActivity.hideProgressBar();
                }
            };

            // declare a response handler that toggles the tweet's favorited state
            final JsonHttpResponseHandler favoriteHandler = new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    tweet.toggleFavorited();
                    setFavoriteButtonColor(binding.ivFavoriteButton, tweet.favorited);
                    timelineActivity.hideProgressBar();
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    timelineActivity.hideProgressBar();
                }
            };

            // Unretweet if tweet is retweeted, and retweet if not, make API call
            binding.ivRetweetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timelineActivity.showProgressBar();
                    if (tweet.retweeted) {
                        client.unretweetTweet(tweet.id_str,retweetHandler);
                    } else {
                        client.retweetTweet(tweet.id_str, retweetHandler);
                    }
                }
            });

            binding.ivFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timelineActivity.showProgressBar();
                    if (tweet.favorited) {
                        client.unfavoriteTweet(tweet.id_str, favoriteHandler);
                    } else {
                        client.favoriteTweet(tweet.id_str, favoriteHandler);
                    }
                }
            });

            // body on click should go to detail activity
            binding.tvBody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("tweet", Parcels.wrap(tweet));
                    intent.putExtra("position", getAdapterPosition());
                    timelineActivity.startActivityForResult(intent, TimelineActivity.DETAIL_CODE);
                }
            });

            // load tweet image
            if (tweet.media_url.isEmpty()) {
                binding.ivMediaImage.setVisibility(View.GONE);
            } else {
                binding.ivMediaImage.setVisibility(View.VISIBLE);
                Glide.with(context).load(tweet.media_url).override(tweet.media_width, tweet.media_height).into(binding.ivMediaImage);
            }

            Glide.with(context).load(tweet.user.profileImageUrl).into(binding.ivProfileImage);
        }
    }

    // Clear all tweets
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add all tweets from list
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    // helper function to set the color of an arbitrary ImageView
    private void setTint(ImageView iv,  @ColorRes int colorRes) {
        ImageViewCompat.setImageTintList(iv, ColorStateList.valueOf(ContextCompat.getColor(context, colorRes)));
    }

    // sets the color and style of a given retweet button based on a boolean retweeted status
    private void setRetweetButtonColor(ImageView ivRetweetButton, Boolean retweeted) {
        if (retweeted) {
            setTint(ivRetweetButton, R.color.inline_action_retweet);
            ivRetweetButton.setImageResource(R.drawable.ic_vector_retweet);
        } else {
            setTint(ivRetweetButton, R.color.inline_action);
            ivRetweetButton.setImageResource(R.drawable.ic_vector_retweet_stroke);
        }
    }

    // sets the color and style of a given favorite button based on a boolean favorited status
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
