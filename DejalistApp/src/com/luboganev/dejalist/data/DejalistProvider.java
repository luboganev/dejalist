package com.luboganev.dejalist.data;

import com.luboganev.dejalist.data.DejalistContract.Categories;
import com.luboganev.dejalist.data.DejalistContract.Products;
import com.luboganev.dejalist.data.DejalistDatabase.Tables;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class DejalistProvider extends ContentProvider {
	
	private DejalistDatabase mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    
    private static final int PRODUCTS = 100;
    private static final int PRODUCTS_ID = 101;
    private static final int PRODUCTS_CATEGORY_ID = 102;
    
    private static final int CATEGORIES = 200;
    private static final int CATEGORIES_ID = 201;
    
    /**
     * Build and return a {@link UriMatcher} that catches all {@link Uri}
     * variations supported by this {@link ContentProvider}.
     * 
     * <pre>
     * com.luboganev.dejalist/
     * 		products/
     * 		products/#
     * 		products/category/#
     * 
     * 		categories/
     * 		categories/#
     * </pre>
     * 
     * 
     */
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DejalistContract.CONTENT_AUTHORITY;
        
        matcher.addURI(authority, DejalistContract.PATH_PRODUCTS, PRODUCTS);
        matcher.addURI(authority, DejalistContract.PATH_PRODUCTS + "/#", PRODUCTS_ID);
        matcher.addURI(authority, DejalistContract.PATH_PRODUCTS + "/" + DejalistContract.PATH_CATEGORY + "/#", PRODUCTS_CATEGORY_ID);

        matcher.addURI(authority, DejalistContract.PATH_CATEGORIES, CATEGORIES);
        matcher.addURI(authority, DejalistContract.PATH_CATEGORIES + "/#", CATEGORIES_ID);

        return matcher;
    }
    
    @Override
    public boolean onCreate() {
        mOpenHelper = new DejalistDatabase(getContext());
        return true;
    }
    
    private void deleteDatabase() {
        mOpenHelper.close();
        Context context = getContext();
        DejalistDatabase.deleteDatabase(context);
        mOpenHelper = new DejalistDatabase(getContext());
    }

	@Override
	public String getType(Uri uri) {
		final int match = sUriMatcher.match(uri);
        switch (match) {
        case PRODUCTS:
        case PRODUCTS_CATEGORY_ID:
        	return Products.CONTENT_TYPE;
        case PRODUCTS_ID:
        	return Products.CONTENT_ITEM_TYPE;
        case CATEGORIES:
        	return Categories.CONTENT_TYPE;
        case CATEGORIES_ID:
        	return Categories.CONTENT_ITEM_TYPE;
        default:
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
	        case PRODUCTS:
	        	long insertedId = db.insertOrThrow(Tables.PRODUCTS, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Products.buildProductUri(insertedId);
	        case CATEGORIES:
	        	insertedId = db.insertOrThrow(Tables.CATEGORIES, null, values);
	        	getContext().getContentResolver().notifyChange(uri, null);
	        	return Categories.buildCategoryUri(insertedId);
	        default:
	            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
	}
	
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		switch(match) {
		case PRODUCTS:
			int totalInserted = 0;
			long insertedId;
			db.beginTransaction();
			try {
				for (ContentValues contentValues : values) {
					insertedId = db.insertOrThrow(Tables.PRODUCTS, null, contentValues);
					if(insertedId >= 0) totalInserted++;
				}
				db.setTransactionSuccessful();
				db.endTransaction();
			}
			catch (SQLException e){
				db.endTransaction();
				throw e;
			}
			getContext().getContentResolver().notifyChange(Products.CONTENT_URI, null);
			return totalInserted;
		case CATEGORIES:
			totalInserted = 0;
			db.beginTransaction();
			try {
				for (ContentValues contentValues : values) {
					insertedId = db.insertOrThrow(Tables.CATEGORIES, null, contentValues);
					if(insertedId >= 0) totalInserted++;
				}
				db.setTransactionSuccessful();
				db.endTransaction();
			}
			catch (SQLException e){
				db.endTransaction();
				throw e;
			}
			getContext().getContentResolver().notifyChange(Categories.CONTENT_URI, null);
			return totalInserted;
		default:
			throw new UnsupportedOperationException("uri not known: " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final SelectionBuilder builder = buildExpandedSelection(uri);
        return builder.where(selection, selectionArgs).query(db, projection, sortOrder);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).update(db, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return retVal;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (uri == DejalistContract.BASE_CONTENT_URI) {
            deleteDatabase();
            ProductImageFileHelper.deleteAllProductImageFiles(getContext());
            getContext().getContentResolver().notifyChange(uri, null, false);
            return 1;
        }
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        
        
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS: {
                builder.table(Tables.PRODUCTS);
                
                //delete product photos files of deleted products
                final SelectionBuilder deletedProductsBuilder = new SelectionBuilder();
                deletedProductsBuilder.table(Tables.PRODUCTS);
                String[] deletedProductsProjection = new String[] { Products.PRODUCT_URI };
                Cursor c = deletedProductsBuilder.where(selection, selectionArgs).query(db, deletedProductsProjection, null);
                if(c.moveToFirst()) {
                	do {
                		ProductImageFileHelper.deleteProductImageFile(Uri.parse(c.getString(0))); ;
					} while (c.moveToNext());
                }
                c.close();
                break;
            }
            case CATEGORIES_ID: {
            	final long categoryId = Categories.getCategoryId(uri);
            	builder.table(Tables.CATEGORIES)
            			.where(Categories._ID + "=?", String.valueOf(categoryId));
            	
            	// Reset products category of products from the deleted category
            	final SelectionBuilder resetProductsCategoryBuilder = new SelectionBuilder();
            	resetProductsCategoryBuilder.table(Tables.PRODUCTS)
            		.where(Products.PRODUCT_CATEGORY_ID + "=?", String.valueOf(categoryId));
            	ContentValues resetProductsCategoryValues = new ContentValues();
            	resetProductsCategoryValues.put(Products.PRODUCT_CATEGORY_ID, -1L);
            	resetProductsCategoryBuilder.update(db, resetProductsCategoryValues);
            	break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        
        int retVal = builder.where(selection, selectionArgs).delete(db);
        getContext().getContentResolver().notifyChange(uri, null);
        return retVal;
	}
	
	/**
     * Build a simple {@link SelectionBuilder} to match the requested
     * {@link Uri}.
     */
    private SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS: {
                return builder.table(Tables.PRODUCTS);
            }
            case PRODUCTS_ID: {
                final long productId = Products.getProductId(uri);
                return builder.table(Tables.PRODUCTS)
                        .where(Products._ID + "=?", String.valueOf(productId));
            }
            case PRODUCTS_CATEGORY_ID: {
            	final long categoryId = Products.getCategoryProductsId(uri);
            	return builder.table(Tables.PRODUCTS)
            			.where(Products.PRODUCT_CATEGORY_ID + "=?", String.valueOf(categoryId));
            }
            case CATEGORIES: {
            	return builder.table(Tables.CATEGORIES);
            }
            case CATEGORIES_ID: {
            	final long categoryId = Categories.getCategoryId(uri);
            	return builder.table(Tables.CATEGORIES)
            			.where(Categories._ID + "=?", String.valueOf(categoryId));
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }
    
    /**
     * Build an advanced {@link SelectionBuilder} to match the requested
     * {@link Uri}. This is usually only used by {@link #query}, since it
     * performs table joins useful for {@link Cursor} data.
     */
    private SelectionBuilder buildExpandedSelection(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
//            case CONTACT_REQUESTS_INCOMING: {
//            	SelectionBuilder builder = new SelectionBuilder();
//                return builder
//                        .table(Tables.CONTACT_REQUESTS_JOIN_USERS)
//                        .mapToTable(ContactRequests._ID, Tables.CONTACT_REQUESTS)
//                        .mapToTable(ContactRequests.CONTACT_REQUEST_USER_SERVER_ID, Tables.CONTACT_REQUESTS)
//                        .mapToTable(ContactRequests.CONTACT_REQUEST_TYPE, Tables.CONTACT_REQUESTS)
//                        .mapToTable(ContactRequests.CONTACT_REQUEST_STATUS, Tables.CONTACT_REQUESTS)
//                        .mapToTable(ContactRequests.LAST_MODIFIED, Tables.CONTACT_REQUESTS)
//                        .where(ContactRequests.CONTACT_REQUEST_TYPE + "=?", String.valueOf(ContactRequests.TYPE_INCOMING));
//            }
            default: {
                return buildSimpleSelection(uri);
            }
        }
    }
}
