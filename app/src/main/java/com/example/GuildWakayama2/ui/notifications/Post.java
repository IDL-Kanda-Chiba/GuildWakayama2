package com.example.GuildWakayama2.ui.notifications;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Post {
    public List<Double> location;
    public double latitude;
    public double longitude;
    public String user_name;
    public String user_id;
    public String title;
    public String body;
    public String genre;
    public String difficulty;

    public Post(){

    }

    // 位置情報を後で記す
    public Post(String user_name, String user_id, String title, String body, String genre, String difficulty){
        //this.location = Arrays.asList(latitude, longitude);
        this.user_name = user_name;
        this.user_id = user_id;
        this.title = title;
        this.body = body;
        this.genre = genre;
        this.difficulty = difficulty;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        //result.put("location", location);
        result.put("user_name", user_name);
        result.put("user_id", user_id);
        result.put("title", title);
        result.put("body", body);
        result.put("genre", genre);
        result.put("difficulty", difficulty);

        return result;
    }

}
