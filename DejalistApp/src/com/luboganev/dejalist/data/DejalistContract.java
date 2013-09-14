package com.luboganev.dejalist.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DejalistContract {

    interface ProductColumns {
    	String PRODUCT_NAME = "name";
    	String PRODUCT_URI = "uri";
    	String PRODUCT_INLIST = "inlist";
    	String PRODUCT_CHECKED = "checked";
    	String PRODUCT_CATEGORY_ID = "categoryId";
    	String PRODUCT_USED_COUNT = "usedCount";
    	String PRODUCT_LAST_USED = "lastUsed";
    	String PRODUCT_DELETED = "deleted";
    }
    
    interface CategoryColumns {
    	String CATEGORY_NAME = "name";
    	String CATEGORY_COLOR = "color";
    }
    
    public static final String CONTENT_AUTHORITY = "com.luboganev.dejalist";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    
    public static final String PATH_PRODUCTS = "products";
    public static final String PATH_CATEGORIES = "categories";
    public static final String PATH_CATEGORY = "category";
    
    
    public static class Products implements BaseColumns, ProductColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCTS).build();
        
        public static final Uri CATEGORY_CONTENT_URI =
        		BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCTS).appendPath(PATH_CATEGORY).build();
        
    	// default value for no category
    	public static final long PRODUCT_CATEGORY_NONE_ID = -1L;
        
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.dejalist.product";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.dejalist.product";
        
        /** Build {@link Uri} for requested {@link #_ID}. */
        public static Uri buildProductUri(long productId) {
        	return ContentUris.withAppendedId(CONTENT_URI, productId);
        }
        
        /** Read {@link #_ID} built with {@link #buildProductUri(long)}. */
        public static long getProductId(Uri uri) {
        	return Long.parseLong(uri.getPathSegments().get(1));
        }
        
        /** Build {@link Uri} for all {@link Products} with the requested {@link #PRODUCT_CATEGORY_ID}. */
        public static Uri buildCategoryProductsUri(long categoryId) {
        	return ContentUris.withAppendedId(CATEGORY_CONTENT_URI, categoryId);
        }
        
        /** Read {@link #PRODUCT_CATEGORY_ID} built with {@link #buildCategoryProductsUri(long)}. */
        public static long getCategoryProductsId(Uri uri) {
        	return Long.parseLong(uri.getPathSegments().get(2));
        }
        
        /** Used to fetch {@link Products} that are contained in the shopping list */
        public static final String SELECTION_NO_CATEGORY = PRODUCT_CATEGORY_ID + " == " + PRODUCT_CATEGORY_NONE_ID;
        
        /** Used to fetch {@link Products} that are contained in the shopping list */
        public static final String SELECTION_IN_LIST = PRODUCT_INLIST + " == 1";
        
        /** Used to fetch {@link Products} that are <b>NOT</b> contained in the shopping list */
        public static final String SELECTION_NOT_IN_LIST = PRODUCT_INLIST + " == 0";
        
        /** Used to fetch {@link Products} that are checked in the shopping list */
        public static final String SELECTION_CHECKED = PRODUCT_CHECKED + " == 1";
        
        /** Used to fetch {@link Products} that are <b>NOT</b> checked in the shopping list */
        public static final String SELECTION_NOT_CHECKED = PRODUCT_CHECKED + " == 0";
        
        /** Used to fetch {@link Products} that are marked as deleted */
        public static final String SELECTION_DELETED = PRODUCT_DELETED + " == 1";
        
        /** Used to fetch {@link Products} that are <b>NOT</b> marked as deleted */
        public static final String SELECTION_NOT_DELETED = PRODUCT_DELETED + " == 0";
        
    	/** Used to fetch {@link Products} with a particular name */
        public static final String SELECTION_NAME = PRODUCT_NAME + " = ?";
        
        /** Builds a selection arguments for {@link #SELECTION_NAME} */
        public static final String[] buildNameSelectionArgs(String name) {
        	return new String[]{name};
        }
        
        /** Used to fetch {@link Products} with a particular category id */
        public static final String SELECTION_CATEGORY_ID = PRODUCT_CATEGORY_ID + " = ?";
        
        /** Builds a selection arguments for {@link #SELECTION_CATEGORY_ID} */
        public static final String[] buildCategoryIdSelectionArgs(long categoryId) {
        	return new String[]{String.valueOf(categoryId)};
        }
        
        /**
         * Builds a selection string of type WHERE _ID IN (...)
         * 
         * @param productIds
         * 		The ids of the products
         * @return
         * 		The selection string
         */
        public static final String buildSelectionIdIn(long[] productIds) {
        	if(productIds.length <= 0) return null;
        	StringBuilder selection = new StringBuilder();
        	selection.append(_ID).append(" IN (" + productIds[0]);
        	for (int i = 1; i < productIds.length; i++) {
        		selection.append(", ").append(productIds[i]);
			}
        	selection.append(")");
        	return selection.toString();
        }
        
        public static final String ORDER_NAME_ASC = PRODUCT_NAME;
        public static final String ORDER_LAST_USED_DESC = PRODUCT_LAST_USED +" DESC";
        public static final String ORDER_USEDCOUNT_DESC = PRODUCT_USED_COUNT +" DESC";
        
        public static final String ORDER_CATEGORY = PRODUCT_CATEGORY_ID;
        public static final String ORDER_CHECKED = PRODUCT_CHECKED;
    }
    
    public static class Categories implements BaseColumns, CategoryColumns {
    	public static final Uri CONTENT_URI =
    			BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORIES).build();
    	
    	public static final String CONTENT_TYPE =
    			"vnd.android.cursor.dir/vnd.dejalist.category";
    	public static final String CONTENT_ITEM_TYPE =
    			"vnd.android.cursor.item/vnd.dejalist.category";
    	
    	/** Build {@link Uri} for requested {@link #_ID}. */
    	public static Uri buildCategoryUri(long categoryId) {
    		return ContentUris.withAppendedId(CONTENT_URI, categoryId);
    	}
    	
    	/** Read {@link #_ID} from {@link Categories} {@link Uri}. */
    	public static long getCategoryId(Uri uri) {
    		return Long.parseLong(uri.getPathSegments().get(1));
    	}
    	
    	/** Used to fetch {@link Categories} with a particular name */
        public static final String SELECTION_NAME = CATEGORY_NAME + " = ?";
        
        /** Builds a selection arguments for {@link #SELECTION_NAME} */
        public static final String[] buildNameSelectionArgs(String name) {
        	return new String[]{name};
        }
        
        /** Used to fetch {@link Categories} with a particular color */
        public static final String SELECTION_COLOR = CATEGORY_COLOR + " = ?";
        
        /** Builds a selection arguments for {@link #SELECTION_COLOR} */
        public static final String[] buildColorSelectionArgs(int color) {
        	return new String[]{String.valueOf(color)};
        }
    }
    
//    private static final String DO_NOT_SYNC = "do_not_sync";
//    
//    public static Uri addDoNotSyncAdapterParameter(Uri uri) {
//        return uri.buildUpon().appendQueryParameter(
//        		DO_NOT_SYNC, "true").build();
//    }
//
//    public static boolean hasDoNotSyncAdapterParameter(Uri uri) {
//        return TextUtils.equals("true",
//                uri.getQueryParameter(DO_NOT_SYNC));
//    }
}
