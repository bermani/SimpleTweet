package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
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

        public void bind(Tweet tweet) {
            binding.tvBody.setText(tweet.body);
            binding.tvScreenName.setText(tweet.user.screenName);
            binding.tvRelativeTime.setText(tweet.relativeTime);
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
}
