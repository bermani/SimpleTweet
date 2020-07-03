package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class User {

    // empty constructor for parceler
    public User() {}

    // all relevant user fields
    public String name;
    public String screenName;
    public String profileImageUrl;
    public String id_str;
    public String bio;
    public Integer followers;
    public Integer followings;

    // generate a User object given a User jsonObject
    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url_https");
        user.id_str = jsonObject.getString("id_str");
        user.bio = jsonObject.getString("description");
        user.followers = jsonObject.getInt("followers_count");
        user.followings = jsonObject.getInt("friends_count");
        return user;
    }

    // generate a list of User objects given a JSONArray of User jsonObjects
    public static List<User> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            users.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return users;
    }
}
