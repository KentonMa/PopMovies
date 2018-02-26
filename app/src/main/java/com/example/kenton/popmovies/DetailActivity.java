package com.example.kenton.popmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private final String TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Movie.EXTRA_MOVIE)) {
            Movie movie = intent.getParcelableExtra(Movie.EXTRA_MOVIE);

            TextView textViewTitle = (TextView) findViewById(R.id.movie_title);
            textViewTitle.setText(movie.getTitle());

            ImageView imageViewPoster = (ImageView) findViewById(R.id.movie_poster);
            Picasso.with(this)
                    .load(movie.getPosterUrl())
                    .into(imageViewPoster);

            TextView textViewOverview = (TextView) findViewById(R.id.movie_overview);
            textViewOverview.setText(movie.getOverview());

            String rating = "Rating: " + String.valueOf(movie.getRating()) + " / 10";
            TextView textViewRating = (TextView) findViewById(R.id.movie_rating);
            textViewRating.setText(rating);

            String release = "Release date: " + movie.getReleaseDate();
            TextView textViewRelease = (TextView) findViewById(R.id.movie_release_date);
            textViewRelease.setText(release);
        }
    }
}
