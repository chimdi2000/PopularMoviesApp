package android.dindu.com.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;


public class MovieDetailsActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment implements GlideModule {

        String release_date, overview, title, backdrop;
        int vote_average;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            final int imageWidth = 400;
            final int imageHeight = 400;
            String releaseDatePrefix = "Release Date: ";
            String averageVotePrefix = "Average Vote: ";
            String overviewPrefix = "Plot Synopsis: \n";


            ImageView detailImageView = (ImageView) rootView.findViewById(R.id.detailImageView);

            Intent intent = getActivity().getIntent();
            if (intent != null) {

                title = intent.getStringExtra(MainActivityFragment.CONS_TITLE);
                backdrop = intent.getStringExtra(MainActivityFragment.CONS_BACKDROP);
                overview = intent.getStringExtra(MainActivityFragment.CONS_OVERVIEW);
                vote_average = intent.getIntExtra(MainActivityFragment.CONS_VOTE_AVERAGE, 0);
                release_date = intent.getStringExtra(MainActivityFragment.CONS_RELEASE_DATE);


                Glide.with(getActivity())
                        .load(backdrop)
                        .override(imageWidth, imageHeight)
                        .fitCenter()

                        .into(detailImageView);


                ((TextView) rootView.findViewById(R.id.movieTitleTextView))
                        .setText(title);
                ((TextView) rootView.findViewById(R.id.movieReleaseDateTextView))
                        .setText(releaseDatePrefix + release_date);
                ((TextView) rootView.findViewById(R.id.movieVoteAverageTextView))
                        .setText(averageVotePrefix + vote_average);
                ((TextView) rootView.findViewById(R.id.movieOverviewTextView))
                        .setText(overviewPrefix + overview);

            }
            return rootView;
        }

        @Override
        public void applyOptions(Context context, GlideBuilder builder) {
            builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
        }

        @Override
        public void registerComponents(Context context, Glide glide) {

        }
    }
}
