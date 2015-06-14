package android.dindu.com.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * A placeholder fragment containing for my MainFragment screen.
 */
public class MainActivityFragment extends Fragment implements GlideModule {
    GridImageAdapter mm;
    GridView gridView;
    static String release_dates[], overviews[], titles[], backdrops[];
    static int vote_averages[];
    static final String CONS_TITLE = "title";
    static final String CONS_BACKDROP = "backdrop";
    static final String CONS_OVERVIEW = "overview";
    static final String CONS_RELEASE_DATE = "release_date";
    static final String CONS_VOTE_AVERAGE = "vote_average";

    final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
    final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";

    final String SORT_PARAM = "sort_by";
    final String API_KEY_PARAM = "api_key";

    final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();

    final String HTTP_REQUEST_METHOD = "GET";


    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) rootView.findViewById(R.id.gridview);
        mm = new GridImageAdapter(getActivity());

        gridView.setAdapter(new GridImageAdapter(getActivity()));
        mm.notifyDataSetChanged();


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class)
                        .putExtra(CONS_TITLE, titles[position])
                        .putExtra(CONS_BACKDROP, backdrops[position])
                        .putExtra(CONS_OVERVIEW, overviews[position])
                        .putExtra(CONS_VOTE_AVERAGE, vote_averages[position])
                        .putExtra(CONS_RELEASE_DATE, release_dates[position]);
                startActivity(intent);
            }
        });
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }


    public void updateMovies() {

        FetchMovieDataTask fetchMovie = new FetchMovieDataTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String queryString = prefs.getString(getString(R.string.pref_movie_key), getString(R.string.pref_movie_selection_popular_value));
        fetchMovie.execute(queryString);
    }

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }


    public class FetchMovieDataTask extends AsyncTask<String, Void, String[]> {


        @Override
        protected void onPostExecute(String[] returnedSting) {
            super.onPostExecute(returnedSting);

            if (returnedSting != null) {
                GridImageAdapter.movieUrls.clear();
                GridImageAdapter.movieUrls.addAll(Arrays.asList(returnedSting));
                GridImageAdapter.movieTitles.clear();
                GridImageAdapter.movieTitles.addAll(Arrays.asList(titles));
                gridView.setAdapter(new GridImageAdapter(getActivity()));
                mm.notifyDataSetChanged();


            }


        }

        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            try {


                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, params[0])
                        .appendQueryParameter(API_KEY_PARAM, getString(R.string.movie_api_key))
                        .build();

                URL url = new URL(builtUri.toString());


                // Create the request to MovieAPI site, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(HTTP_REQUEST_METHOD);
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                movieJsonStr = buffer.toString();

            } catch (IOException e) {
                //Log.e(LOG_TAG, "Error", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.

                return null;

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                       // Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {


                return getMovieJsonData(movieJsonStr);
            } catch (JSONException e) {
                //Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;

        }

        public String[] getMovieJsonData(String movieJSonString) throws JSONException {

            final String TMDB_RESULTS = "results";

            final String TMDB_ORIGINAL_TITLE = "original_title";
            final String TMDB_BACKDROP_PATH = "backdrop_path";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_VOTE_AVERAGE = "vote_average";
            final String TMDB_RELEASE_DATE = "release_date";


            JSONObject movieJson = new JSONObject(movieJSonString);
            JSONArray movieResultArray = movieJson.getJSONArray(TMDB_RESULTS);
            backdrops = new String[movieResultArray.length()];
            vote_averages = new int[movieResultArray.length()];
            titles = new String[movieResultArray.length()];

            release_dates = new String[movieResultArray.length()];
            overviews = new String[movieResultArray.length()];

            for (int c = 0; c < movieResultArray.length(); c++) {

                int vote_average;
                String title, backdrop, overview, release_date;

                JSONObject resultObject = movieResultArray.getJSONObject(c);

                title = resultObject.getString(TMDB_ORIGINAL_TITLE);
                backdrop = resultObject.getString(TMDB_BACKDROP_PATH);
                overview = resultObject.getString(TMDB_OVERVIEW);
                vote_average = resultObject.getInt(TMDB_VOTE_AVERAGE);
                release_date = resultObject.getString(TMDB_RELEASE_DATE);

                backdrops[c] = IMAGE_BASE_URL + backdrop;
                titles[c] = title;
                vote_averages[c] = vote_average;
                release_dates[c] = release_date;
                overviews[c] = overview;


            }
            return backdrops;

        }
    }
}