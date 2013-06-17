package com.luboganev.dejalist;

import com.luboganev.dejalist.data.DejalistContract;
import com.luboganev.dejalist.data.entities.Category;

import nl.qbusict.cupboard.CupboardFactory;
import android.content.Context;
import android.graphics.Color;

public class DummyDataGenerator {
	public static void populateDB(Context context) {
			Category category = new Category();
			category.color = Color.BLUE;
			category.name = "Drinks";
			CupboardFactory.cupboard().withContext(context).put(DejalistContract.Categories.CONTENT_URI, category);
			
			category = new Category();
			category.color = Color.GREEN;
			category.name = "Vegetables";
			CupboardFactory.cupboard().withContext(context).put(DejalistContract.Categories.CONTENT_URI, category);
			
			category = new Category();
			category.color = Color.MAGENTA;
			category.name = "Sweets";
			CupboardFactory.cupboard().withContext(context).put(DejalistContract.Categories.CONTENT_URI, category);
			
			category = new Category();
			category.color = Color.CYAN;
			category.name = "Fruits";
			
			CupboardFactory.cupboard().withContext(context).put(DejalistContract.Categories.CONTENT_URI, category);
			category = new Category();
			category.color = Color.RED;
			category.name = "Meat";
			CupboardFactory.cupboard().withContext(context).put(DejalistContract.Categories.CONTENT_URI, category);
			
			category = new Category();
			category.color = Color.YELLOW;
			category.name = "Fix";
			CupboardFactory.cupboard().withContext(context).put(DejalistContract.Categories.CONTENT_URI, category);
	}
}
