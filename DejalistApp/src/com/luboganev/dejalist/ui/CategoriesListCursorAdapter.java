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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CategoriesListCursorAdapter extends CursorAdapter {
	public static final int VIEW_TYPE_CATEGORY = 0; 
	public static final int VIEW_TYPE_ADD = 1; 
	
	public static final long CATEGORY_NONE_ITEM_ID = -1L;
	public static final long CATEGORY_ADD_ITEM_ID = -101L;
	
    private Context mContext;
	
	private static Cursor addCategoryNone(Cursor categories) {
		MatrixCursor mainNavigation = new MatrixCursor(new String[] {Categories._ID, Categories.CATEGORY_NAME, Categories.CATEGORY_COLOR});
		mainNavigation.addRow(new Object[]{CATEGORY_NONE_ITEM_ID, "", -1});
		if(categories != null) return new MergeCursor(new Cursor[]{mainNavigation, categories});
		else return mainNavigation;
	}
	
	@Override
	public int getCount() {
		return super.getCount() + 1;
	}
	
	@Override
	public int getItemViewType(int position) {
		return position == (super.getCount()) ? VIEW_TYPE_ADD : VIEW_TYPE_CATEGORY;
	}
	
	@Override
	public Object getItem(int position) {
		if(position == super.getCount()) {
			return null;
		}
		else return super.getItem(position);
	}
	
	@Override
	public long getItemId(int position) {
		if(position == super.getCount()) {
			return CATEGORY_ADD_ITEM_ID;
		}
		else return super.getItemId(position);
	}
	
	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}
	
	@Override
	public boolean isEnabled(int position) {
		return position != super.getCount();
	}

	public CategoriesListCursorAdapter(Context context, int flags) {
		super(context, addCategoryNone(null), flags);
		mContext = context;
	}
	
	@Override
	public Cursor swapCursor(Cursor categories) {
		return super.swapCursor(addCategoryNone(categories));
	}
	
	/**
	 * @see android.widget.ListAdapter#getView(int, View, ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		switch (getItemViewType(position)) {
		case VIEW_TYPE_ADD: {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext)
						.inflate(R.layout.list_item_categories_new,
								parent, false);
			}
			convertView.setEnabled(isEnabled(position));
			return convertView;
		}
		case VIEW_TYPE_CATEGORY: {
			if (position > 2)
				position--;
			return super.getView(position, convertView, parent);
		}
		}

		return null;
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
	    View view = LayoutInflater.from(context).inflate(R.layout.list_item_category, parent, false);
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
