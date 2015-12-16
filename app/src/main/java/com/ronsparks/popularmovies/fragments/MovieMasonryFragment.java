package com.ronsparks.popularmovies.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ronsparks.popularmovies.R;
import com.ronsparks.popularmovies.adapters.MovieMasonryRecyclerViewAdapter;
import com.ronsparks.popularmovies.data.MovieContent;
import com.ronsparks.popularmovies.fragments.dummy.DummyContent;
import com.ronsparks.popularmovies.fragments.dummy.DummyContent.DummyItem;
import com.ronsparks.popularmovies.helpers.MovieOperations;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MovieMasonryFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    //thi is a comment.
    private boolean mShowPopularSortMenuItem = true;
    private int mColumnCount;
    private MovieContent mMovieContent = new MovieContent();
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieMasonryFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MovieMasonryFragment newInstance(int columnCount) {
        MovieMasonryFragment fragment = new MovieMasonryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        MovieOperations movieOps = new MovieOperations();
        String discoverUrl = movieOps.buildDiscoverMoviesUrl(getContext(), null);
        new AsyncMovieFragmentRunner().execute(discoverUrl);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moviemasonry_list, container, false);

        int columnCount = getContext().getResources().getInteger(R.integer.masonry_columns);
        mColumnCount = columnCount > 0 ? columnCount : 2;

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MovieMasonryRecyclerViewAdapter(DummyContent.ITEMS, mListener));
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.moviemasonry_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        boolean blnTest = mShowPopularSortMenuItem;

        if(mShowPopularSortMenuItem) {
            menu.findItem(R.id.action_popularity_sort).setVisible(!mShowPopularSortMenuItem);
            menu.findItem(R.id.action_vote_count_sort).setVisible(mShowPopularSortMenuItem);
        } else {
            menu.findItem(R.id.action_popularity_sort).setVisible(!mShowPopularSortMenuItem);
            menu.findItem(R.id.action_vote_count_sort).setVisible(mShowPopularSortMenuItem);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        MovieOperations movieOps = new MovieOperations();
        Context ctx = getContext();
        String discoverUrl;

        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_refresh:
                discoverUrl = movieOps.buildDiscoverMoviesUrl(ctx, mShowPopularSortMenuItem? ctx.getString(R.string.movie_db_popularity_sort) : ctx.getString(R.string.movie_db_vote_count_sort));
                new AsyncMovieFragmentRunner().execute(discoverUrl);
                return true;
            case R.id.action_popularity_sort:
                //change the menu option to show sort by ratings
                mShowPopularSortMenuItem = !mShowPopularSortMenuItem;
                getActivity().invalidateOptionsMenu();

                //create new call for API and call the movieDB api
                discoverUrl = movieOps.buildDiscoverMoviesUrl(ctx, ctx.getString(R.string.movie_db_popularity_sort));
                new AsyncMovieFragmentRunner().execute(discoverUrl);
                return true;
            case R.id.action_vote_count_sort:
                //change the menu option to show sort by popularity
                mShowPopularSortMenuItem = !mShowPopularSortMenuItem;
                getActivity().invalidateOptionsMenu();

                //create new call for API and call the movieDB api
                discoverUrl = movieOps.buildDiscoverMoviesUrl(ctx, ctx.getString(R.string.movie_db_vote_count_sort));
                new AsyncMovieFragmentRunner().execute(discoverUrl);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class AsyncMovieFragmentRunner extends AsyncTask<String, Void, MovieContent>{

        private final String LOG_TAG = AsyncMovieFragmentRunner.class.getSimpleName();

        @Override
        protected void onPostExecute(MovieContent movieContent) {
            //super.onPostExecute(movieContent);
            mMovieContent = movieContent;
        }

        @Override
        protected MovieContent doInBackground(String... params) {
            MovieOperations movieOps = new MovieOperations();
            MovieContent movieContent = movieOps.fetchPopularMovies(params[0]);

            return movieContent;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}