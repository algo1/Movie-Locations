package inplokesh.ac.iitkgp.cse.movielocation;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private LocationRequest mLocationRequest;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Double userLat, userLong;
    private List<Movie> locationList = new ArrayList<Movie>();

    private int updateWatcher = -1;      // This variable is for tracking the no. of network Requests processed

    private String NETWORK_TAG = "MainActivity_API";
    private String MAP_TAG = "Map";


    public Double getUserLat() {
        return userLat;
    }

    public void setUserLat(Double userLat) {
        this.userLat = userLat;
    }

    public Double getUserLong() {
        return userLong;
    }

    public void setUserLong(Double userLong) {
        this.userLong = userLong;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Utils.isConnectedToInternet()) {
            Toast.makeText(this, "Please make sure that device is connected to internet and try again", Toast.LENGTH_LONG).show();
            finish();
        }

        setUpAndCustomizeAppBar();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        prepareGoogleApiLocationClient();

        prepareAutoCompleteTextView();

        registerLocationRequest();

    }

    public void registerLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    public void prepareAutoCompleteTextView() {

        final DelayAutoCompleteTextView movieTitle = (DelayAutoCompleteTextView) findViewById(R.id.movieTitle);
        movieTitle.setThreshold(2); // min 2 chars for suggestions
        movieTitle.setAdapter(new MovieAutoCompleteAdapter(this));
        ProgressBar pb = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        pb.getIndeterminateDrawable().setColorFilter(
                Color.parseColor("#007fbf"), android.graphics.PorterDuff.Mode.SRC_IN);
        movieTitle.setDropDownBackgroundResource(R.color.wallet_holo_blue_light);

        movieTitle.setLoadingIndicator(pb);
        movieTitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Movie movie = (Movie) adapterView.getItemAtPosition(position);
                movieTitle.setText(movie.getTitle());

                // UI
                movieTitle.clearFocus();
                hideSoftKeyBoard();

                fetchMovieLocations(movie.getTitle());

            }
        });
    }


    public void setUpAndCustomizeAppBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.WHITE);

    }


    public void showProgressbar() {
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.VISIBLE);
    }

    public void hideProgressbar() {
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.GONE);
    }

    public void hideSoftKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public void addLocation(String location, LatLng latLng) {
        // Store in cache
        GeoCodeCache.setCoordinatesInCache(location, latLng);

        markLocationOnMap(latLng.latitude, latLng.longitude);

    }


    /* This method modifies the updateWatcher counter as per the no of requests serviced*/
    public void informWatcher() {
        updateWatcher--;
        if (updateWatcher == 0) {
            // All location requests response obtained
            hideProgressbar();
        }
    }

    public void setUpdateWatcher(int count) {
        updateWatcher = count;
    }


     /* This method fetches geocodes  for all locations received in its argument
      * All reqs are asynchronous and are tracked by updateWatcher variable */

    public void updateMapWithLocations(List<Movie> locationList) {

        if (locationList == null || locationList.size() == 0) {
            hideProgressbar();
            Toast.makeText(this, "Error fetching details", Toast.LENGTH_LONG).show();
            return;
        }

        clearMap();
        setUpdateWatcher(locationList.size());

        // get the lat lng for each Location of the movie
        for (int i = 0; i < locationList.size(); i++) {
            String location = locationList.get(i).getLocations();

            // Search for location in cache
            LatLng latLng = GeoCodeCache.getCoordinatesFromCache(location);

            // Cache Hit
            if (latLng != null) {
                Log.d(NETWORK_TAG, "Cache Hit - Coordinates  for the place : " + location + "  are - " + String.valueOf(latLng.latitude) + " , " + String.valueOf(latLng.longitude));
                markLocationOnMap(latLng.latitude, latLng.longitude);
                informWatcher();
            } else {
                // Cache Miss , Fetching from network
                final String dummyLocation = location;
                StringRequest geoCodeRequest = new StringRequest(Request.Method.GET, NetworkUtils.getGeoCodeByLocation(location) + "&key=" + AppConstants.getMapsApiToken(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        GeoCodeResponse geoCodeResponse = gson.fromJson(response, GeoCodeResponse.class);

                        List<GeoCodeResponse.Result> result = geoCodeResponse.getResults();
                        if (result != null && result.size() != 0) {
                            LatLng dummyLatLng = new LatLng(Double.valueOf(geoCodeResponse.getResults().get(0).getGeometry().getLocation().getLat()), Double.valueOf(geoCodeResponse.getResults().get(0).getGeometry().getLocation().getLng()));
                            addLocation(dummyLocation, dummyLatLng);
                            informWatcher();
                            Log.d(NETWORK_TAG, "Cache Miss - Coordinates  for the place  " + dummyLocation + "  are - " + String.valueOf(dummyLatLng.latitude) + " , " + String.valueOf(dummyLatLng.longitude));
                        } else {
                            informWatcher();
                            Log.d(NETWORK_TAG, "Error From Maps Api - Coordinates not available for the place : " + dummyLocation);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        informWatcher();
                        Log.d(NETWORK_TAG, "Google Map API Error");
                        error.printStackTrace();
                    }
                });

                MovieLocation.getRequestQueue().add(geoCodeRequest);
            }

        }
    }





    /* This method fetches all locations for a particular movie ,
    then calls the google maps geocoding api to fetch lat long info for each location */

    public void fetchMovieLocations(final String movieTitle) {

        showProgressbar();

        setSanFranciscotoFocus();

        String url = NetworkUtils.getMovieInfoByTitle(movieTitle);

        StringRequest movieLocationsRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                Type MovieListToken = new TypeToken<List<Movie>>() {
                }.getType();

                locationList.clear();
                locationList = gson.fromJson(response, MovieListToken);

                updateMapWithLocations(locationList);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Unable to fetch locations , Remove Progressbar and show error msg
                hideProgressbar();
                Log.d(NETWORK_TAG, "Error With API");
                Toast.makeText(MainActivity.this, "Error fetching movie locations", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map headers = new HashMap<String, String>();
                headers.put("X-App-Token", AppConstants.getAppToken());
                return headers;
            }

        };

        // Todo Add timeout , caching stuff , priority
        MovieLocation.getRequestQueue().add(movieLocationsRequest);
    }


    public void prepareGoogleApiLocationClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }


    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        int permissionCheck = ContextCompat.checkSelfPermission(MovieLocation.getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    AppConstants.MY_PERMISSIONS_REQUEST_FINE_LOCATION);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case AppConstants.MY_PERMISSIONS_REQUEST_FINE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                } else {
                    // Todo Explain user about the need for location
                }
                return;

        }


    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public void setUserCurrentLocation(Location lastLocation) {
        if (lastLocation != null) {
            Log.d(MAP_TAG, "Setting User current location");
            setUserLat(lastLocation.getLatitude());
            setUserLong(lastLocation.getLongitude());
            LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.addMarker(new MarkerOptions().position(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 400, null);
        } else {
            Log.d(MAP_TAG, "Error Setting User current location");
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            //Todo Handle GPS not working
        }
        hideProgressbar();
    }

    public void setSanFranciscotoFocus() {
        // Setting "Sanfrancisco" city  to view
        LatLng latLng = new LatLng(37.7749, -122.4194);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 400, null);
    }

    public void markLocationOnMap(Double lat, Double lon) {
        Log.d(MAP_TAG, "Marking Location : " + String.valueOf(lat) + " , " + String.valueOf(lon));
        LatLng userLocation = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(userLocation));
    }

    public void clearMap() {
        // Do any work before clearing here
        mMap.clear();
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(MAP_TAG, "Location found : " + String.valueOf(location.getLatitude()) + " , " + String.valueOf(location.getLongitude()));
        stopLocationUpdates();
        setUserCurrentLocation(location);
    }


    // method called only if permissions are granted , ignore warning
    @TargetApi(23)
    protected void startLocationUpdates() {
        Log.d(MAP_TAG, "Starting Location Updates");
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        Log.d(MAP_TAG, "Stopping Location Updates");
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }
}
