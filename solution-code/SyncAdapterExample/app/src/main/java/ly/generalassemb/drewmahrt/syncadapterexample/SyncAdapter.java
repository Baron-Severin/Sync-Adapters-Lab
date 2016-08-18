package ly.generalassemb.drewmahrt.syncadapterexample;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ly.generalassemb.drewmahrt.syncadapterexample.Model.RedditObject;

/**
 * Created by drewmahrt on 3/2/16.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static String TAG = SyncAdapter.class.getCanonicalName();

    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }


    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        mContentResolver.delete(NewsContentProvider.CONTENT_URI,null,null);

        String data ="";
        try {
            URL url = new URL("https://www.reddit.com/r/random/.json?limit=20");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream inStream = connection.getInputStream();
            data = getInputData(inStream);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
//        SearchResult result = gson.fromJson(data,SearchResult.class);
        JsonObject jsonObject = gson.fromJson(data, JsonObject.class);
        JsonArray redditArray = jsonObject.getAsJsonObject("data").getAsJsonArray("children");

//        for (int i = 0; i < result.getResults().size(); i++) {
//            ContentValues values = new ContentValues();
//            values.put("title",result.getResults().get(i).getTitle());
//            mContentResolver.insert(NewsContentProvider.CONTENT_URI,values);
//            Log.d(TAG,"Latest story: "+result.getResults().get(i).getTitle());
//        }
        for (int i = 0; i < redditArray.size(); i++) {
            ContentValues values = new ContentValues();
            JsonObject redditData = redditArray.get(i).getAsJsonObject().getAsJsonObject("data");

            int score = redditData.getAsJsonPrimitive("score").getAsInt();
            String subreddit = redditData.getAsJsonPrimitive("subreddit").getAsString();
            String url = redditData.getAsJsonPrimitive("url").getAsString();
            String title = redditData.getAsJsonPrimitive("title").getAsString();

            values.put("score", score);
            values.put("subreddit", subreddit);
            values.put("url", url);
            values.put("title", title);

            mContentResolver.insert(NewsContentProvider.CONTENT_URI,values);
//            Log.d(TAG,"Latest story: "+redditArray.get(i).getAsString());
        }
    }

    private String getInputData(InputStream inStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));

        String data = null;

        while ((data = reader.readLine()) != null){
            builder.append(data);
        }

        reader.close();

        return builder.toString();
    }
}
