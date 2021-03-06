package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {

    // empty constructor for parceler
    public Tweet() {}

    // all important fields of the Tweet object
    public String body;
    public String createdAt;
    public User user;
    public String relativeTime;
    public String media_url;
    public Integer media_height;
    public Integer media_width;
    public String id_str;
    public Boolean favorited;
    public Boolean retweeted;


    // generate a tweet from a tweet JSONObject
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.relativeTime = getRelativeTimeAgo(jsonObject.getString("created_at"));
        tweet.id_str = jsonObject.getString("id_str");
        tweet.favorited = jsonObject.getBoolean("favorited");
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        JSONObject entities = jsonObject.getJSONObject("entities");
        if (entities.has("media")) {
            JSONObject media = entities.getJSONArray("media").getJSONObject(0);
            tweet.media_url = media.getString("media_url_https");
            JSONObject sizes = media.getJSONObject("sizes").getJSONObject("small");
            tweet.media_height = sizes.getInt("h");
            tweet.media_width = sizes.getInt("w");
        } else {
            tweet.media_url = "";
            tweet.media_height = 0;
            tweet.media_width = 0;
        }
        return tweet;
    }

    // generate a list of tweets from a JSONArray of Tweet objects
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    // get the relative time ago in a nicely formatted string given a timestamp
    private static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public void toggleRetweeted() {
        retweeted = !retweeted;
    }

    public void toggleFavorited() {
        favorited = !favorited;
    }
}
