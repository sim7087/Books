package com.booksapp071997.books;


import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryJson {
    public static final String LOG_TAG = QueryJson.class.getSimpleName();

    private QueryJson() {

    }

    private static List<BooksSearch> extractBooksData(String bookJSON) {
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }
        List<BooksSearch> booksData = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(bookJSON);
            JSONArray booksArray = root.getJSONArray("items");
            int totalItems = root.getInt("totalItems");
            for (int i = 0; i < booksArray.length(); i++) {
                JSONObject array = booksArray.getJSONObject(i);
                JSONObject volume = array.getJSONObject("volumeInfo");
                String bookTitle = volume.getString("title");
                if (volume.has("authors") && totalItems != 0) {
                    JSONArray authorArray = volume.getJSONArray("authors");
                    String author = "";
                    for (int j = 0; j < authorArray.length(); j++) {
                        if (j == authorArray.length() - 1)
                            author = author + authorArray.getString(j) + "";
                        else
                            author = author + authorArray.getString(j) + ", ";
                    }
                    BooksSearch bookList = new BooksSearch(bookTitle, author);
                    booksData.add(bookList);
                } else {
                    BooksSearch bookList = new BooksSearch(bookTitle, "No Author");
                    booksData.add(bookList);
                }
            }
        } catch (JSONException e) {
            Log.e("json", "Problem in parsing JSON");
        }
        return booksData;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the books JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static List<BooksSearch> fetchBooksData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Earthquake}s
        List<BooksSearch> booksList = extractBooksData(jsonResponse);

        // Return the list of {@link Earthquake}s
        return booksList;
    }
}
