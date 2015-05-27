package com.codepath.apps.MyTwitter.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Manunya on 5/23/2015.
 */
/*
[
    {
    "coordinates": null,
    "truncated": false,
    "created_at": "Tue Aug 28 21:16:23 +0000 2012",
    "favorited": false,
    "id_str": "240558470661799936",
    "in_reply_to_user_id_str": null,
    "entities": {
      "urls": [

      ],
      "hashtags": [

      ],
      "user_mentions": [

      ]
    },
    "text": "just another test",
    "contributors": null,
    "id": 240558470661799936,
    "retweet_count": 0,
    "in_reply_to_status_id_str": null,
    "geo": null,
    "retweeted": false,
    "in_reply_to_user_id": null,
    "place": null,
    "source": "<a href="//realitytechnicians.com\"" rel="\"nofollow\"">OAuth Dancer Reborn</a>",
    "user": {
      .......
    },
    "in_reply_to_screen_name": null,
    "in_reply_to_status_id": null
  },...
]

 */

// Parse the json and store the data, encapsulate state logic or display logic
@Table(name = "Tweets")
public class Tweet extends Model implements Parcelable{
    /*private String body;
    private long uid; // unique id for the tweet
    private User user; // embedded user obj
    private String createdAt;
    */

    @Column(name = "body")
    private String body;
    @Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long uid;
    @Column(name = "user", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private User user;
    @Column(name = "created_at", index = true)
    private String createdAt;
    @Column(name = "retweet_count")
    private int retweetCount;
    @Column(name = "favorite_count")
    private int favoriteCount;
    @Column(name = "media_url")
    private String mediaURL;



    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getRelativeCreatedAtShort() {
        return getRelativeTimeAgoShort(createdAt);
    }

    public String getDetailCreatedAt() {
        return getDetailDate(createdAt);
    }

    public User getUser() {
        return user;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public String getMediaURL() {
        return mediaURL;
    }

    // Make sure to have a default constructor for every ActiveAndroid model
    public Tweet() {
        super();

    }

    //deserialize json
    public static  Tweet fromJSON(JSONObject obj) {
        Tweet tweet = new Tweet();
        //extract value from json, store here
        try {
            tweet.body = obj.getString("text");
            tweet.uid = obj.getLong("id");
            tweet.createdAt = obj.getString("created_at");
            tweet.user = User.findOrCreateFromJson(obj.getJSONObject("user"));
            tweet.retweetCount = obj.getInt("retweet_count");
            tweet.favoriteCount = obj.getInt("favorite_count");
            JSONArray mediaArray = obj.getJSONObject("entities").getJSONArray("media");
            if (mediaArray != null) {
                tweet.mediaURL = mediaArray.getJSONObject(0).getString("media_url");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static Tweet fromJSONAndSave(JSONObject obj) {
        Tweet tweet = Tweet.fromJSON(obj);
        tweet.save();
        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray arr) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        for (int i=0; i < arr.length(); i++) {
            try {
                JSONObject obj = arr.getJSONObject(i);
                Tweet tweet = Tweet.fromJSONAndSave(obj);
                if (tweet != null) { //check if successfully parsed
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue; //even if one tweet doesn't work we still want to continue

            }
        }
        return tweets;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    private static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        PrettyTime p = new PrettyTime();
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            /*relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();*/
            //use prettytime instead
            relativeDate = p.format(new Date(dateMillis));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }

    //shorten version
    private String getRelativeTimeAgoShort(String rawJsonDate) {
        String tString = getRelativeTimeAgo(rawJsonDate);
        if (tString.contains("hour")) {
            return tString.split(" ")[0] + "h";
        } else if (tString.contains("minute")) {
            return tString.split(" ")[0] + "m";
        } else if (tString.contains("second")) {
            return tString.split(" ")[0] + "s";
        } else if (tString.contains("day")) {
            return tString.split(" ")[0] + "d";
        } else if (tString.contains("week")) {
            return tString.split(" ")[0] + "w";
        } else if (tString.contains("month")) {
            return tString.split(" ")[0] + "M";
        } else if (tString.contains("year")) {
            return tString.split(" ")[0] + "y";
        } else if (tString.contains("moments ago")) {
            return "just now";
        }
        return tString;
    }

    private static String getDetailDate(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String detailDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            detailDate = new SimpleDateFormat("M/d/yy, h:m a").format(new Date(dateMillis));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return detailDate;
    }

    //parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(body);
        dest.writeLong(uid);
        dest.writeParcelable(user, flags);
        dest.writeString(createdAt);
        dest.writeInt(retweetCount);
        dest.writeInt(favoriteCount);
        dest.writeString(mediaURL);
    }

    public static final Parcelable.Creator<Tweet> CREATOR = new Parcelable.Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel in) {
            return new Tweet(in);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };

    private Tweet(Parcel in) {
        body = in.readString();
        uid = in.readLong();
        user = in.readParcelable(User.class.getClassLoader());
        createdAt = in.readString();
        retweetCount = in.readInt();
        favoriteCount = in.readInt();
        mediaURL = in.readString();
    }
}
