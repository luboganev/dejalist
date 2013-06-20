package com.luboganev.dejalist.ui;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;
import butterknife.InjectView;
import butterknife.Views;

import com.luboganev.dejalist.R;
import com.luboganev.dejalist.data.DejalistContract.Categories;
import com.luboganev.dejalist.data.entities.Category;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CategoriesListCursorAdapter extends CursorAdapter {
	public static final long CATEGORY_NONE_ITEM_ID = -1L;
	
    private LayoutInflater mInflater;
	
	private static Cursor addCategoryNone(Cursor categories) {
		MatrixCursor mainNavigation = new MatrixCursor(new String[] {Categories._ID, Categories.CATEGORY_NAME, Categories.CATEGORY_COLOR});
		mainNavigation.addRow(new Object[]{CATEGORY_NONE_ITEM_ID, "", -1});
		if(categories != null) return new MergeCursor(new Cursor[]{mainNavigation, categories});
		else return mainNavigation;
	}
	
	public CategoriesListCursorAdapter(Context context, int flags) {
		super(context, addCategoryNone(null), flags);
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public Cursor swapCursor(Cursor categories) {
		//change cursor internally calls swap cursor, so extra 
		// values should be added only once during swap
		return super.swapCursor(addCategoryNone(categories));
	}
	
    	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		Category category = cupboard().withCursor(cursor).get(Category.class);
		
		if(category._id == CATEGORY_NONE_ITEM_ID) {
			holder.catColor.setVisibility(View.INVISIBLE);
			holder.name.setText(R.string.menu_categories_none);
		}
		else {
			holder.catColor.setVisibility(View.VISIBLE);
			holder.catColor.setBackgroundColor(category.color);
			holder.name.setText(category.name);
		}
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
	    View view = mInflater.inflate(R.layout.list_item_category, parent, false);
	    ViewHolder holder = new ViewHolder(view);
	    view.setTag(holder);
	    return view;
	}
	
	static class ViewHolder {
		@InjectView(R.id.tv_category_name) TextView name;
		@InjectView(R.id.v_category_color) View catColor;

		public ViewHolder(View view) {
			Views.inject(this, view);
		}
	}

}
