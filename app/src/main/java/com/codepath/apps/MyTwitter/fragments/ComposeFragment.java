package com.codepath.apps.MyTwitter.fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.MyTwitter.R;
import com.codepath.apps.MyTwitter.TwitterApplication;
import com.codepath.apps.MyTwitter.TwitterClient;
import com.codepath.apps.MyTwitter.models.Tweet;
import com.codepath.apps.MyTwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;


public class ComposeFragment extends DialogFragment {

    private Button btPost;
    private ImageButton btCancel;
    private TextView tvCount;
    private ImageView ivProfileImageCompose;
    private TextView tvUserNameCompose;
    private TextView tvScreenNameCompose;
    private EditText etTweetBody;
    private static int MAX_TWEET_LENGTH = 140;

    public interface ComposeFragmentListener {
        void onPostTweet(Tweet tweet);
    }

    public ComposeFragment() {
        // Required empty public constructor
    }

    public static ComposeFragment newInstance(User user) {
        ComposeFragment df = new ComposeFragment();
        df.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        df.setArguments(args);
        return df;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_compose, container, false);
        User user = getArguments().getParcelable("user");
        TwitterClient client = TwitterApplication.getRestClient();
        setupViews(view, user, client);
        return view;
    }

    private void setupViews(View view, User user, final TwitterClient client) {
        btPost = (Button) view.findViewById(R.id.btPost);
        btCancel = (ImageButton) view.findViewById(R.id.btCancel);
        ivProfileImageCompose = (ImageView) view.findViewById(R.id.ivProfileImage);
        tvUserNameCompose = (TextView) view.findViewById(R.id.tvUserName);
        tvScreenNameCompose = (TextView) view.findViewById(R.id.tvScreenName);
        etTweetBody = (EditText) view.findViewById(R.id.etTweetBody);
        tvCount = (TextView) view.findViewById(R.id.tvCount);

        tvCount.setText(Integer.toString(MAX_TWEET_LENGTH));
        btPost.setEnabled(false); //not enable until user type in tweet
        tvUserNameCompose.setText(user.getName());
        tvScreenNameCompose.setText("@" + user.getScreenName());
        Picasso.with(getActivity()).load(user.getProfileImageUrl()).into(ivProfileImageCompose);

        //buttons listeners
        btPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.postTweet(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Tweet newTweet = Tweet.fromJSONAndSave(response);
                        ComposeFragmentListener listener = (ComposeFragmentListener) getActivity();
                        listener.onPostTweet(newTweet);
                        dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("DEBUG", errorResponse.toString());
                    }
                }
                        , etTweetBody.getText().toString());
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //tweet count listener
        etTweetBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int count = MAX_TWEET_LENGTH - etTweetBody.length();
                if (count < 20) {
                    tvCount.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
                tvCount.setText(Integer.toString(count));
                btPost.setEnabled(count >= 0 && count < MAX_TWEET_LENGTH);
            }
        });
    }


}
