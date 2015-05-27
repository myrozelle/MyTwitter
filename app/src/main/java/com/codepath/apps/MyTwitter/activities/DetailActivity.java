package com.codepath.apps.MyTwitter.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.MyTwitter.R;
import com.codepath.apps.MyTwitter.models.Tweet;
import com.squareup.picasso.Picasso;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupViews();
    }

    private void setupViews() {
        Tweet tweet = getIntent().getParcelableExtra("tweet");
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
        TextView tvScreeName = (TextView) findViewById(R.id.tvScreenName);
        TextView tvBody = (TextView) findViewById(R.id.tvBody);
        ImageView ivMedia = (ImageView) findViewById(R.id.ivMedia);
        TextView tvTimeStamp = (TextView) findViewById(R.id.tvTimeStamp);

        Spanned formattedUserName = Html.fromHtml("<b>" + tweet.getUser().getName() + "</b>");
        tvUserName.setText(formattedUserName);
        tvScreeName.setText("@" + tweet.getUser().getScreenName());
        ivProfileImage.setImageResource(android.R.color.transparent);
        Picasso.with(this).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);
        tvBody.setText(tweet.getBody());
        ivMedia.setImageResource(android.R.color.transparent);
        Picasso.with(this).load(tweet.getMediaURL()).into(ivMedia);
        tvTimeStamp.setText(tweet.getDetailCreatedAt());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
