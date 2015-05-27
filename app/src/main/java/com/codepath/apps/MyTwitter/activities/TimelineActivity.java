package com.codepath.apps.MyTwitter.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.codepath.apps.MyTwitter.R;
import com.codepath.apps.MyTwitter.TwitterApplication;
import com.codepath.apps.MyTwitter.TwitterClient;
import com.codepath.apps.MyTwitter.adapters.TweetsArrayAdapter;
import com.codepath.apps.MyTwitter.fragments.ComposeFragment;
import com.codepath.apps.MyTwitter.listeners.EndlessScrollListener;
import com.codepath.apps.MyTwitter.models.Tweet;
import com.codepath.apps.MyTwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends ActionBarActivity implements ComposeFragment.ComposeFragmentListener{
    private TwitterClient client;
    private User thisUser;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;
    private SwipeRefreshLayout swipeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        setupViews();
        loadCache();
        //get the client
        client = TwitterApplication.getRestClient(); //singleton client
        getThisUser();
        populateTimeLine(0);
    }

    private void setupViews() {
        //add icon to action bar
        /*getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        */
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        lvTweets = (ListView) findViewById(R.id.lv_tweets);
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(this, tweets);
        lvTweets.setAdapter(aTweets);
        lvTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(TimelineActivity.this, "list item clicked", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(TimelineActivity.this, DetailActivity.class);
                i.putExtra("tweet", tweets.get(position));
                startActivity(i);
            }
        });

        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int totalItemsCount) {
                long max_id = aTweets.getItem(totalItemsCount - 1).getUid();
                populateTimeLine(max_id - 1);
            }
        });



        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeLine(0);
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void getThisUser() {
        client.getAccountCredentials(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                thisUser = User.fromJSON(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //notify user
                Toast.makeText(TimelineActivity.this, errorResponse.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    // send API request to get the timeline JSON
    // Fill the listview by creating the tweet obj from json
    // standard downloading and deserialization
    private void populateTimeLine(final long max_id) {
    client.getHomeTimeLine(new JsonHttpResponseHandler() {
        // Success
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            Log.d("DEBUG", response.toString());
            if (max_id <= 0) {
                deleteCache();
                aTweets.clear();// clear adapter if it's first page load
            }
            // deserialize json, create models and add them to the adapter, and load model data into listview
            aTweets.addAll(Tweet.fromJSONArray(response));
            swipeContainer.setRefreshing(false);
        }

        // Failure
        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d("DEBUG", errorResponse.toString());
        }
    }, max_id);
    }

    //caching methods
    private void loadCache() {
        try {
            List<Tweet> tweets = new Select().from(Tweet.class).orderBy("created_at").limit(50).execute();
            aTweets.addAll(tweets);
        } catch (SQLiteException e) {
        }
    }
    private void deleteCache() {
        try {
            new Delete().from(Tweet.class).execute();
            new Delete().from(User.class).execute();
        } catch (SQLiteException e) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.miCompose) {
            FragmentManager fm = getSupportFragmentManager();
            ComposeFragment composeDialogFragment = ComposeFragment.newInstance(thisUser);
            composeDialogFragment.show(fm, "fragment_compost");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostTweet(Tweet tweet) {
        aTweets.insert(tweet, 0);
    }
}
