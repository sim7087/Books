package com.booksapp071997.books;


public class BooksSearch {
    private String mBookName;
    private String mAuthorName;

    public BooksSearch(String bookName, String authorName) {
        mBookName = bookName;
        mAuthorName = authorName;
    }

    public String getAuthorName() {
        return mAuthorName;
    }

    public String getBookName() {
        return mBookName;
    }

}
