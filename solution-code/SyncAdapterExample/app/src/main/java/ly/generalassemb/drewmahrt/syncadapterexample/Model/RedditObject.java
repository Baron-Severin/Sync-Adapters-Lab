package ly.generalassemb.drewmahrt.syncadapterexample.Model;

/**
 * Created by erikrudie on 8/18/16.
 */
public class RedditObject {

    int score;
    String subreddit;
    String url;
    String title;

    public RedditObject(int score, String subreddit, String url, String title) {
        this.score = score;
        this.subreddit = subreddit;
        this.url = url;
        this.title = title;
    }
}
