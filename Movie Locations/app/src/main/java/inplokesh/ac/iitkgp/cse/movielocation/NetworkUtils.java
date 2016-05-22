package inplokesh.ac.iitkgp.cse.movielocation;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by lokeshponnada on 5/21/16.
 */
public class NetworkUtils {

    public static String getAutoSuggestMovieUrl(String movieTitle) {
        String url = AppUrls.Autocomplete_Movie;
        try {
            url = url + "$where=starts_with" + URLEncoder.encode("(title," + "'" + movieTitle + "')", "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getMovieInfoByTitle(String movieTitle) {
        String url = AppUrls.Movieinfo_By_Title;
        try {
            url = url + URLEncoder.encode(movieTitle, "utf-8");
        } catch (UnsupportedEncodingException e) {
            // Todo handle
            e.printStackTrace();
        }
        return url;
    }


    public static String getGeoCodeByLocation(String location) {
        String url = AppUrls.Geocode_From_Location;
        try {
            url = url + URLEncoder.encode(location + ", San Francisco", "utf-8");
        } catch (UnsupportedEncodingException e) {
            // Todo handle
            e.printStackTrace();
        }
        return url;
    }
}
