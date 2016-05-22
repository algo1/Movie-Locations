package inplokesh.ac.iitkgp.cse.movielocation;

/**
 * Created by lokeshponnada on 5/21/16.
 */
public class AppConstants {

    public static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 255;


    public static final String APP_TAG = "MovieLocation";
    public static final String API_TAG = "API_ERROR";


    private static String APP_TOKEN = "Q477C4kQwAP3BM30NGpAgi5ZW";
    private static String MAPS_API_TOKEN = "AIzaSyDPCfRVSfM6fkvdr3ZhvU910-Co0LOSyss";


    public static String getAppToken() {
        return APP_TOKEN;
    }

    public static String getMapsApiToken() {
        return MAPS_API_TOKEN;
    }

}
