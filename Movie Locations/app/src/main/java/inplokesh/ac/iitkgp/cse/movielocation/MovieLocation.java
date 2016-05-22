package inplokesh.ac.iitkgp.cse.movielocation;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by lokeshponnada on 5/21/16.
 */
public class MovieLocation extends Application {

    private static final String TAG = MovieLocation.class.getCanonicalName();
    public static Context mContext = null;
    public static final String Version = "1.0";
    public static final String AppName = "MovieLocation";
    private static RequestQueue mRequestQueue;


    @Override
    public void onCreate() {
        super.onCreate();
        initContext(this.getApplicationContext());
        mRequestQueue = Volley.newRequestQueue(this.getApplicationContext());
        if (mRequestQueue == null) {
            Log.d(TAG, "Null Queue");
        }

    }

    public static Context getContext() {
        return mContext;
    }

    private void initContext(Context context) {
        mContext = context;
    }


    public static RequestQueue getRequestQueue() {
        return mRequestQueue;
    }


    public static String getAppName() {
        return AppName;
    }

    public static String getVersion() {
        return Version;
    }

}
