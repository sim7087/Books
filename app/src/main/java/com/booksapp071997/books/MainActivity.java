package com.booksapp071997.books;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView searchButton;
    static TextView mEmptyStateTextView;
    static ImageView mEmptyStateImageView;
    static ProgressBar mProgressBar;
    static BookListAdapter mAdapter;
    BooksAsyncTask task;
    private ListView list;
    private static final String BOOKS_URL = "https://www.googleapis.com/books/v1/volumes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText searchText = findViewById(R.id.edit_text);
        mEmptyStateImageView = findViewById(R.id.imagess);
        list = findViewById(R.id.list);
        mProgressBar = findViewById(R.id.loading_spinner);
        mEmptyStateTextView = findViewById(R.id.empty_view);
        mAdapter = new BookListAdapter(this, new ArrayList<BooksSearch>());
        searchButton = (ImageView) findViewById(R.id.search_view);
        if (!isConnect()) {
            mEmptyStateTextView.setText(R.string.no_internet);
            mProgressBar.setVisibility(View.GONE);
            mEmptyStateImageView.setVisibility(View.GONE);
        } else {
            list.setVisibility(View.VISIBLE);
            mEmptyStateImageView.setVisibility(View.GONE);
            mEmptyStateTextView.setVisibility(View.GONE);
            list.setAdapter(mAdapter);
            task = new BooksAsyncTask();
            task.execute(BOOKS_URL + "?q=search+terms");
        }
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnect()) {
                    list.setVisibility(View.GONE);
                    mEmptyStateTextView.setVisibility(View.VISIBLE);
                    mEmptyStateTextView.setText(R.string.no_internet);
                    mProgressBar.setVisibility(View.GONE);
                    mEmptyStateImageView.setVisibility(View.GONE);
                } else {
                    list.setVisibility(View.VISIBLE);
                    list.setAdapter(mAdapter);
                    task = new BooksAsyncTask();
                    task.execute(BOOKS_URL + "?q=" + searchText.getText().toString());
                }
            }

        });
    }

    private static class BooksAsyncTask extends AsyncTask<String, Void, List<BooksSearch>> {

        @Override
        protected List<BooksSearch> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            List<BooksSearch> result = QueryJson.fetchBooksData(urls[0]);
            return result;
        }

        @Override
        protected void onPostExecute(List<BooksSearch> data) {
            // Clear the adapter of previous earthquake data
            mAdapter.clear();
            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            } else {
                mEmptyStateTextView.setVisibility(View.VISIBLE);
                mEmptyStateTextView.setText(R.string.valid_book);
                mProgressBar.setVisibility(View.GONE);
                mEmptyStateImageView.setVisibility(View.VISIBLE);
            }
        }

    }

    private boolean isConnect() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
}
