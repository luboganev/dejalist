package com.luboganev.dejalist.data;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;
import nl.qbusict.cupboard.EntityCompartment;

import com.luboganev.dejalist.data.DejalistContract.Categories;
import com.luboganev.dejalist.data.DejalistContract.Products;
import com.luboganev.dejalist.data.entities.Category;
import com.luboganev.dejalist.data.entities.Product;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
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
	
    public DejalistDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
	public DejalistDatabase(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
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
					Products.PRODUCT_LAST_USED + " INTEGER " +
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
				//TODO: alter Products table and move around the images
				
				db.execSQL("CREATE TABLE " + Tables.CATEGORIES + " (" +
					 	BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
						Categories.CATEGORY_NAME + " TEXT, " +
						Categories.CATEGORY_COLOR + " INTEGER " +
					");");
				
				insertSampleCategories(db);
			default:
				break;
		}
	}
	
	private void insertSampleCategories(SQLiteDatabase db) {
		EntityCompartment<Category> ec = cupboard().withEntity(Category.class);
		
		Category category = new Category();
		category.color = Color.BLUE;
		category.name = "Drinks";
		db.insert(Tables.CATEGORIES, null, ec.toContentValues(category));
		
		category = new Category();
		category.color = Color.YELLOW;
		category.name = "Fruits";
		db.insert(Tables.CATEGORIES, null, ec.toContentValues(category));
		
		category = new Category();
		category.color = Color.GREEN;
		category.name = "Vegetables";
		db.insert(Tables.CATEGORIES, null, ec.toContentValues(category));
		
		category = new Category();
		category.color = Color.MAGENTA;
		category.name = "Sweets";
		db.insert(Tables.CATEGORIES, null, ec.toContentValues(category));
	}
	
    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
}
