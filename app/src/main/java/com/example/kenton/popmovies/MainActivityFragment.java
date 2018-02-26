package com.example.kenton.popmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivityFragment extends Fragment {

    private static final String TAG = MainActivityFragment.class.getSimpleName();
    private static final String STATE_MOVIE_LIST = "state_movie_list";

    private ImageAdapter mImageAdapter;
    private ArrayList<Movie> mMovieList;
    private boolean mUpdateFlag;


    public MainActivityFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            Log.i(TAG, "Restoring saved state ...");
            mMovieList = savedInstanceState.getParcelableArrayList(STATE_MOVIE_LIST);
            mImageAdapter = new ImageAdapter(getActivity(), getMoviePostersFromList(mMovieList));
            mUpdateFlag = false;
        } else {
            mMovieList = new ArrayList<>();
            mImageAdapter = new ImageAdapter(getActivity(), new String[]{});
            mUpdateFlag = true;
        }

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView mMovieGridView = (GridView) rootView.findViewById(R.id.grid_movies);
        mMovieGridView.setAdapter(mImageAdapter);
        mMovieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                Movie movie = mMovieList.get(position);
                intent.putExtra(Movie.EXTRA_MOVIE, movie);
                startActivity(intent);
            }
        });


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mUpdateFlag) {
            updateMovies();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(STATE_MOVIE_LIST, mMovieList);
        super.onSaveInstanceState(outState);
    }

    private void updateMovies() {
        new FetchMovieDataTask().execute(
                PreferenceManager.getDefaultSharedPreferences(getContext())
        );
    }

    private String[] getMoviePostersFromList(ArrayList<Movie> movieList) {
        String[] results = new String[movieList.size()];
        int i = 0;
        for (Movie movie : movieList) {
            results[i] = movie.getPosterUrl();
            i++;
        }
        return results;
    }

    public class FetchMovieDataTask extends AsyncTask<SharedPreferences, Void, String[]> {

        private final String TAG = FetchMovieDataTask.class.getSimpleName();

        // These are constants used in building the url.
        private final String URL_SCHEME = "https";
        private final String URL_AUTHORITY = "api.themoviedb.org";
        private final String URL_BASE_PATH = "/3/movie";
        private final String API_KEY_PARAM = "api_key";
        private final String API_KEY = "Your API goes here";

        @Override
        protected String[] doInBackground(SharedPreferences... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String rawJsonStr;

            try {
                // Get query path from SharedPref
                String query_path = params[0].getString(
                        getString(R.string.pref_key_sort),
                        getString(R.string.pref_default_value_sort)
                );

                // Construct the URL for the api
                Uri.Builder uri = new Uri.Builder();
                uri.scheme(URL_SCHEME).authority(URL_AUTHORITY).path(URL_BASE_PATH);
                uri.appendPath(query_path);
                uri.appendQueryParameter(API_KEY_PARAM, API_KEY);
                uri.build();
                Log.i(TAG, "Attempting to query the api ...");
                Log.i(TAG, "Querying: " + uri.toString());
                URL url = new URL(uri.toString());

                // Create the request and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a lot easier if you print out the completed
                    // buffer for debugging
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }

                rawJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(TAG, "Error", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(rawJsonStr);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] posterURLS) {
            super.onPostExecute(posterURLS);
            if (posterURLS != null) {
                Log.i(TAG, "Loading posters ...");
                mImageAdapter.setImageUrls(posterURLS);
            }
        }

        private String[] getMovieDataFromJson(String rawJson)
            throws JSONException {
            // These are the names of the JSON objects that need to be extracted.
            final String ARR_RESULTS = "results";
            final String KEY_POSTER_PATH = "poster_path";
            final String KEY_TITLE = "original_title";
            final String KEY_OVERVIEW = "overview";
            final String KEY_VOTE_AVG = "vote_average";
            final String KEY_RELEASE_DATE = "release_date";

            // These are constants used in building an image url.
            final String URL_SCHEME = "https";
            final String URL_AUTHORITY = "image.tmdb.org";
            final String URL_PATH = "/t/p/w185";

            Uri.Builder uri = new Uri.Builder();
            uri.scheme(URL_SCHEME).authority(URL_AUTHORITY).path(URL_PATH);
            uri.build();

            String img_url_base = uri.toString();

            JSONObject responseObj = new JSONObject(rawJson);
            JSONArray movieArray = responseObj.getJSONArray(ARR_RESULTS);

            String[] resultString = new String[movieArray.length()];
            mMovieList.clear();
            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                String poster_path = movie.getString(KEY_POSTER_PATH);
                String title = movie.getString(KEY_TITLE);
                String overview = movie.getString(KEY_OVERVIEW);
                double rating = movie.getDouble(KEY_VOTE_AVG);
                String release_date = movie.getString(KEY_RELEASE_DATE);
                String poster_url = img_url_base + poster_path;

                Movie currMovie = new Movie(title,
                        poster_url,
                        overview,
                        rating,
                        release_date);
                resultString[i] = poster_url;
                mMovieList.add(i, currMovie);
            }

            return resultString;
        }
    }
}
