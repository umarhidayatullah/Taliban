package com.example.sus.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.os.IResultReceiver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.example.sus.provider.PlaceContract.PlaceEntry;

public class PlaceContentProvider extends ContentProvider {

    public static final int PLACES = 100;
    public static final int PLACE_WITH_ID = 101;

    private static final UriMatcher uri = buildUriMatcher();
    private static final String TAG = PlaceContentProvider.class.getName();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PlaceContract.AUTHORITY, PlaceContract.PATH_PLACES, PLACES);
        uriMatcher.addURI(PlaceContract.AUTHORITY, PlaceContract.PATH_PLACES + "/#", PLACE_WITH_ID);
        return uriMatcher;
    }

    private PlaceDbHelper placeDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        placeDbHelper = new PlaceDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = placeDbHelper.getReadableDatabase();
        int match = this.uri.match(uri);
        Cursor retCursor;

        switch(match) {
            case PLACES:
                retCursor = db.query(PlaceEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = placeDbHelper.getWritableDatabase();
        int match = this.uri.match(uri);
        Uri returnUri;
        switch(match) {
            case PLACES:
                long id = db.insert(PlaceEntry.TABLE_NAME, null, values);
                if(id > 0) {
                    returnUri = ContentUris.withAppendedId(PlaceEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "+ uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = placeDbHelper.getWritableDatabase();
        int match = this.uri.match(uri);
        int placesDeleted;
        switch(match) {
            case PLACE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                placesDeleted = db.delete(PlaceContract.PlaceEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "+ uri);
        }
        if(placesDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return placesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = placeDbHelper.getWritableDatabase();
        int match = this.uri.match(uri);
        int placesUpdated;

        switch(match) {
            case PLACE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                placesUpdated = db.update(PlaceContract.PlaceEntry.TABLE_NAME, values, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(placesUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return placesUpdated;
    }
}
