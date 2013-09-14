package com.luboganev.dejalist.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.luboganev.dejalist.R;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.BaseColumns;
import android.provider.MediaStore.Images;
import android.util.Log;

public class PublicProvider extends ContentProvider {
    public static final String CONTENT_AUTHORITY = "com.luboganev.dejalist.public";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    
    public static final String PATH_IMAGES = "images";
	
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    
    private static final int IMAGE_PRODUCT_ID = 100;
    
    /**
     * Build and return a {@link UriMatcher} that catches all {@link Uri}
     * variations supported by this {@link ContentProvider}.
     * 
     * <pre>
     * com.luboganev.dejalist.public/
     * 		images/*
     * </pre>
     * 
     * 
     */
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;
        
        matcher.addURI(authority, PATH_IMAGES + "/*", IMAGE_PRODUCT_ID);

        return matcher;
    }
    
    @Override
    public boolean onCreate() {
        return true;
    }
    
    @Override
	public String getType(Uri uri) {
    	Log.d("getType", uri.toString());
		final int match = sUriMatcher.match(uri);
        switch (match) {
        case IMAGE_PRODUCT_ID:
        	return "image/jpeg";
        default:
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
	}
	
	@Override
	public String[] getStreamTypes(Uri uri, String mimeTypeFilter) {
		Log.d("getStreamTypes", uri.toString());
		final int match = sUriMatcher.match(uri);
        switch (match) {
        case IMAGE_PRODUCT_ID:
        	if(mimeTypeFilter != null && mimeTypeFilter.length() > 0) {
        		if(mimeTypeFilter.equals("image/*") || 
        				mimeTypeFilter.equals("*\\/jpeg"))
        			return new String[]{"image/jpeg"};
        		else return null;
        	}
        	else return new String[]{"image/jpeg"};
        default:
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.d("insert", uri.toString());
		throw new RuntimeException("Operation not supported");
	}
	
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		Log.d("bulkInsert", uri.toString());
		throw new RuntimeException("Operation not supported");
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Log.d("query", uri.toString());
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case IMAGE_PRODUCT_ID:
			String filename = uri.getLastPathSegment();
			MatrixCursor cursor = new MatrixCursor(new String[]{
					BaseColumns._ID, 
					Images.Media.DATA, 
					Images.Media.ORIENTATION,
					Images.Media.MIME_TYPE,
					Images.Media.DATE_TAKEN,
					Images.Media.DISPLAY_NAME});
			File sharedPublicFile;
			try {
				sharedPublicFile = CacheManager.cacheData(getContext(), ProductImageFileHelper.getFile(getContext(), filename));
				cursor.addRow(new Object[]{0, sharedPublicFile.getPath(), 0, "image/jpeg", 0, getContext().getResources().getString(R.string.share_image_data_display_name)});
				return cursor;
			} catch (IOException e) {
				return null;
			}
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}
	
	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
		Log.d("openFile", uri.toString());
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case IMAGE_PRODUCT_ID:
			Log.d("test", uri.toString());
			String filename = uri.getLastPathSegment();
			Log.d("test", filename);
			Log.d("test", ProductImageFileHelper.getFile(getContext(), filename).getPath());
            // only read only mode is supported
            ParcelFileDescriptor pfd = ParcelFileDescriptor.open(
            		ProductImageFileHelper.getFile(getContext(), filename), ParcelFileDescriptor.MODE_READ_ONLY);
            return pfd;
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		Log.d("update", uri.toString());
		throw new RuntimeException("Operation not supported");
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Log.d("delete", uri.toString());
		throw new RuntimeException("Operation not supported");
	}
}
