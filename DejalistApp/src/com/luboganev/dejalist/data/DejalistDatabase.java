package com.luboganev.dejalist.data;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

import nl.qbusict.cupboard.EntityCompartment;

import com.luboganev.dejalist.R;
import com.luboganev.dejalist.data.DejalistContract.Categories;
import com.luboganev.dejalist.data.DejalistContract.Products;
import com.luboganev.dejalist.data.entities.Category;
import com.luboganev.dejalist.data.entities.Product;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

public class DejalistDatabase extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "dejalist_db";
	private static final int DATABASE_VERSION = 2;
	
	interface Tables {
		String PRODUCTS = "products";
		String CATEGORIES = "categories";
	}
	
	static {
		cupboard().register(Product.class);
		cupboard().register(Category.class);
	}
	
	private String[] mSampleCategoriesNames;
	
	private Context mContext;
	
    public DejalistDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mSampleCategoriesNames = context.getResources().getStringArray(R.array.sample_categories);
        mContext = context;
    }
    
	public DejalistDatabase(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		mSampleCategoriesNames = context.getResources().getStringArray(R.array.sample_categories);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		 db.execSQL("CREATE TABLE " + Tables.PRODUCTS + " (" +
				 	BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					Products.PRODUCT_NAME + " TEXT, " +
					Products.PRODUCT_URI + " TEXT, " +
					Products.PRODUCT_INLIST + " INTEGER, " +
					Products.PRODUCT_CHECKED + " INTEGER, " +
					Products.PRODUCT_CATEGORY_ID + " INTEGER, " +
					Products.PRODUCT_USED_COUNT + " INTEGER, " +
					Products.PRODUCT_LAST_USED + " INTEGER, " +
					Products.PRODUCT_DELETED + " INTEGER " +
				");");
		 
		 db.execSQL("CREATE TABLE " + Tables.CATEGORIES + " (" +
				 	BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					Categories.CATEGORY_NAME + " TEXT, " +
					Categories.CATEGORY_COLOR + " INTEGER " +
				");");
		 
		 insertSampleCategories(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch(oldVersion) {
			case 1:
				// add new columns
                db.execSQL("ALTER TABLE " + Tables.PRODUCTS + " ADD COLUMN "
                		+ Products.PRODUCT_CATEGORY_ID + " INTEGER DEFAULT " + Products.PRODUCT_CATEGORY_NONE_ID);
                
                db.execSQL("ALTER TABLE " + Tables.PRODUCTS + " ADD COLUMN "
                		+ Products.PRODUCT_USED_COUNT + " INTEGER DEFAULT 0");
                
                db.execSQL("ALTER TABLE " + Tables.PRODUCTS + " ADD COLUMN "
                		+ Products.PRODUCT_LAST_USED + " INTEGER DEFAULT 0");
                
                db.execSQL("ALTER TABLE " + Tables.PRODUCTS + " ADD COLUMN "
                		+ Products.PRODUCT_DELETED + " INTEGER DEFAULT 0");
                
                // move the pictures from the external storage
                Cursor c = db.query(
                		Tables.PRODUCTS, 
        				new String[]{Products._ID, Products.PRODUCT_URI}, 
        				null,
        				null,
        				null, 
        				null, 
        				null);
        		
                if(c.getCount() > 0) {
                	c.moveToFirst();
                	long[] productIds = new long[c.getCount()];
                	String[] productUris = new String[c.getCount()];
                	
                	int i=0;
                	do {
                		productIds[i] = c.getLong(0);
                		productUris[i] = c.getString(1);
                		i++;
					} while (c.moveToNext());
                	
                	for (i = 0; i < productIds.length; i++) {
                		// copy the product picture
                		Uri productFileUri = ProductImageFileHelper.copyToANewProductImageFile(mContext, Uri.parse(productUris[i]));
                		// update product uri
                		ContentValues values = new ContentValues();
                		values.put(Products.PRODUCT_URI, productFileUri.toString());
                		db.update(Tables.PRODUCTS, values, Products._ID + " = ?", new String[] {String.valueOf(productIds[i])});
                		// delete old picture
                		ProductImageFileHelper.deleteProductImageFile(Uri.parse(productUris[i]));
					}
                }
                c.close();
				
                // add the new table for categories
				db.execSQL("CREATE TABLE " + Tables.CATEGORIES + " (" +
					 	BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
						Categories.CATEGORY_NAME + " TEXT, " +
						Categories.CATEGORY_COLOR + " INTEGER " +
					");");
				
				// add the sample categories
				insertSampleCategories(db);
			default:
				break;
		}
	}
	
	private void insertSampleCategories(SQLiteDatabase db) {
		EntityCompartment<Category> ec = cupboard().withEntity(Category.class);
		
		Category category = new Category();
		category.color = 0xFFE5E539;
		category.name = mSampleCategoriesNames[0];
		db.insert(Tables.CATEGORIES, null, ec.toContentValues(category));
		
		category = new Category();
		category.color = 0xFF5ADD45;
		category.name = mSampleCategoriesNames[1];
		db.insert(Tables.CATEGORIES, null, ec.toContentValues(category));
		
		category = new Category();
		category.color = 0xFFE55CE5;
		category.name = mSampleCategoriesNames[2];
		db.insert(Tables.CATEGORIES, null, ec.toContentValues(category));
		
		category = new Category();
		category.color = 0xFF4040FF;
		category.name = mSampleCategoriesNames[3];
		db.insert(Tables.CATEGORIES, null, ec.toContentValues(category));
	}
	
    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
}
