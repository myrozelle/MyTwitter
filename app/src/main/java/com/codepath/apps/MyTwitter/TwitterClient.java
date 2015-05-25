package com.codepath.apps.MyTwitter;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	//mine
	public static final String REST_CONSUMER_KEY = "3MSwdhtFvFumsjNeonnCaifS6";       // Change this
	public static final String REST_CONSUMER_SECRET = "VHeWDAYNnfma0EaIavrzxTR8WLW88zDXJWzLOw0EVTPREq0cIy"; // Change this
	//W
	//public static final String REST_CONSUMER_KEY = "uXhc8CFIQK9P72f0ffrgPBdRs";       // Change this
	//public static final String REST_CONSUMER_SECRET = "irkPNh4LkCIAfBwBWDHOttYBl6DVUieSQEagQVNeMIN2kf8cVR";
	public static final String REST_CALLBACK_URL = "oauth://MYR_FirstTweetApp1"; // Change this (here and in manifest)
	public static int LOAD_COUNT = 25;

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	// Method == Endpoint
	// HomeTimeLine - Gets us the home timeline
	/*
	statuses/home_timeline.json
	count=25
	since_id=1
	 */
	public void getHomeTimeLine(AsyncHttpResponseHandler handler, long max_id) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Specify the params
		RequestParams params = new RequestParams();
		params.put("count", LOAD_COUNT);
		params.put("since_id", 1);
		if (max_id > 0) {
			params.put("max_id", max_id);
		}
		// Execute the request
		getClient().get(apiUrl, params, handler);
	}

	// get account credentials
	public void getAccountCredentials(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		getClient().get(apiUrl, null, handler);
	}

	//Compose a tweet
	public void postTweet(AsyncHttpResponseHandler handler, String tweetBody) {
		String apiURL = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", tweetBody);
		getClient().post(apiURL, params, handler);
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}