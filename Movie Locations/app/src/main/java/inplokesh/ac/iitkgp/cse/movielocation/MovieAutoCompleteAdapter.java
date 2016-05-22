package inplokesh.ac.iitkgp.cse.movielocation;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by lokeshponnada on 5/21/16.
 */
public class MovieAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private List<Movie> movieList;



    public MovieAutoCompleteAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public Movie getItem(int index) {
        return movieList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.autocomplete_title, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.title)).setText("Movie : " + getItem(position).getTitle());
        ((TextView) convertView.findViewById(R.id.actor)).setText("Main Actor : " + getItem(position).getActor1());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected Filter.FilterResults performFiltering(CharSequence constraint) {
                Filter.FilterResults filterResults = new Filter.FilterResults();
                if (constraint != null) {
                    if (movieList != null) {
                        movieList.clear();
                    }

                    String capitalizedConstraint = Utils.capitalize(constraint.toString());
                    RequestFuture<String> future = RequestFuture.newFuture();
                    StringRequest request = new StringRequest(Request.Method.GET, NetworkUtils.getAutoSuggestMovieUrl(capitalizedConstraint), future, future) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map headers = new HashMap<String, String>();
                            // Soda Api Token Todo Move to app. file
                            headers.put("X-App-Token", AppConstants.getAppToken());
                            return headers;
                        }
                    };

                    MovieLocation.getRequestQueue().add(request);

                    List<Movie> uniqueMovieList = new ArrayList<>();
                    try {
                        String response = future.get();
                        Log.d("LokTag", response.toString());
                        Gson gson = new Gson();
                        Type MovieListToken = new TypeToken<List<inplokesh.ac.iitkgp.cse.movielocation.Movie>>() {
                        }.getType();
                        movieList = gson.fromJson(response, MovieListToken);

                        // Removing the duplicate movie names
                        Set<String> titles = new HashSet<String>();
                        for (Movie item : movieList) {
                            if (titles.add(item.getTitle())) {
                                uniqueMovieList.add(item);
                            }
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Assign the data to the FilterResults
                    filterResults.values = uniqueMovieList;
                    filterResults.count = uniqueMovieList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults
                    results) {
                if (results != null && results.count > 0) {
                    movieList = (List<Movie>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        }

                ;
    }

}
