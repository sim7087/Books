package com.booksapp071997.books;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class BookListAdapter extends ArrayAdapter<BooksSearch> {
    public BookListAdapter(Activity context, ArrayList<BooksSearch> book) {
        super(context, 0, book);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BooksSearch list = getItem(position);
        View listItemView = convertView;
        if (listItemView == null)
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.booklists, parent, false);
        TextView bookName = (TextView) listItemView.findViewById(R.id.book_name);
        bookName.setText(list.getBookName());
        TextView authorName = (TextView) listItemView.findViewById(R.id.author_name);
        authorName.setText(list.getAuthorName());
        return listItemView;
    }
}
