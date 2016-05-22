package inplokesh.ac.iitkgp.cse.movielocation;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lokeshponnada on 5/21/16.
 */
public class GeoCodeCache {
    private static Map<String, LatLng> geoCodeCache = new HashMap<String, LatLng>();

    public static LatLng getCoordinatesFromCache(String location) {
        return geoCodeCache.get(location);
    }

    public static void setCoordinatesInCache(String location, LatLng latLng) {
        geoCodeCache.put(location, latLng);
    }
}
