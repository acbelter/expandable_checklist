package com.acbelter.myexplist;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class MyContentProvider extends ContentProvider {
    private static final String AUTHORITY = "com.acbelter.myexplist.MyDatabase";
    private static final String MY_TABLE = "my_table";
    private static final String URL = "content://" + AUTHORITY + "/" + MY_TABLE;
    private static final Uri CONTENT_URI = Uri.parse(URL);

    private static final int MY_ITEMS = 1;
    private static final int MY_ITEM_ID = 2;

    private static final UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, MY_TABLE, MY_ITEMS);
        sUriMatcher.addURI(AUTHORITY, MY_TABLE + "/#", MY_ITEM_ID);
    }

    private MyDatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    public static Uri getContentUri() {
        return CONTENT_URI;
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new MyDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        mDatabase = mDatabaseHelper.getWritableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MY_TABLE);

        switch (sUriMatcher.match(uri)) {
            case MY_ITEMS: {
                break;
            }
            case MY_ITEM_ID: {
                queryBuilder.appendWhere("_id=" + uri.getLastPathSegment());
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor c = queryBuilder.query(mDatabase, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MY_ITEMS: {
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + MY_TABLE;
            }
            case MY_ITEM_ID: {
                return "vnd.android.cursor.item/vnd." + AUTHORITY + "." + MY_TABLE;
            }
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        mDatabase = mDatabaseHelper.getWritableDatabase();
        if (sUriMatcher.match(uri) != MY_ITEMS) {
            throw new IllegalArgumentException("Wrong URI " + uri);
        }

        long rowId = mDatabase.insert(MY_TABLE, null, contentValues);
        if (rowId > 0) {
            Uri resultUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(resultUri, null);
            return resultUri;
        }

        throw new SQLException("Failed to insert a record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        mDatabase = mDatabaseHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case MY_ITEMS: {
                break;
            }
            case MY_ITEM_ID: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = "_id=" + id;
                } else {
                    selection = selection + " AND _id=" + id;
                }
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        int count = mDatabase.delete(MY_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        mDatabase = mDatabaseHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case MY_ITEMS: {
                break;
            }
            case MY_ITEM_ID: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = "_id=" + id;
                } else {
                    selection = selection + " AND _id=" + id;
                }
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        int count = mDatabase.update(MY_TABLE, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
