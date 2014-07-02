package com.luboganev.dejalist.ui;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.luboganev.dejalist.R;
import com.luboganev.dejalist.data.DejalistContract.Categories;
import com.luboganev.dejalist.data.DejalistContract.Products;
import com.luboganev.dejalist.data.entities.Category;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NavigationCursorAdapter extends CursorAdapter {
	public static final int VIEW_TYPE_CHECKLIST = 0; 
	public static final int VIEW_TYPE_ALL_PRODUCTS = 1; 
	public static final int VIEW_TYPE_CATEGORY = 2; 
	public static final int VIEW_TYPE_ADD_CATEGORY = 3; 
	
	public static final long NAV_CHECKLIST_ITEM_ID = -101;
	public static final long NAV_ALL_PRODUCTS_ITEM_ID = -102;
	
	public static final long NAV_ADD_CATEGORY_ITEM_ID = -999;
	
	public static final int POSITION_CHECKLIST = 0;
	public static final int POSITION_ALL_PRODUCTS = 1;
	public static final int POSITION_NO_CATEGORY = 2;
	
    private LayoutInflater mInflater;
    private OnClickListener mAddCategoryClickListener;
	
	private static Cursor addMainNavigationItems(Cursor categories) {
		MatrixCursor mainNavigation = new MatrixCursor(new String[] {Categories._ID, Categories.CATEGORY_NAME, Categories.CATEGORY_COLOR});
		mainNavigation.addRow(new Object[]{NAV_CHECKLIST_ITEM_ID, "", 0});
		mainNavigation.addRow(new Object[]{NAV_ALL_PRODUCTS_ITEM_ID, "", 0});
		mainNavigation.addRow(new Object[]{Products.PRODUCT_CATEGORY_NONE_ID, "", 0});
		if(categories != null) return new MergeCursor(new Cursor[]{mainNavigation, categories});
		else return mainNavigation;
	}
	
	@Override
	public int getCount() {
		return super.getCount() + 1;
	}
	
	@Override
	public int getItemViewType(int position) {
		if(position == POSITION_CHECKLIST) return VIEW_TYPE_CHECKLIST;
		else if(position == POSITION_ALL_PRODUCTS) return VIEW_TYPE_ALL_PRODUCTS;
		else if(position == getCount() - 1) return VIEW_TYPE_ADD_CATEGORY;
		else return VIEW_TYPE_CATEGORY;
	}
	
	@Override
	public int getViewTypeCount() {
		return 4;
	}
	
	@Override
	public Object getItem(int position) {
		if(getItemViewType(position) == VIEW_TYPE_ADD_CATEGORY) {
			return null;
		}
		else {
			return super.getItem(position);
		}
	}
	
	@Override
	public long getItemId(int position) {
		if(getItemViewType(position) == VIEW_TYPE_ADD_CATEGORY) {
			return NAV_ADD_CATEGORY_ITEM_ID;
		}
		else {
			return super.getItemId(position);
		}
	}
	
	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}
	
	@Override
	public boolean isEnabled(int position) {
		return getItemViewType(position) != VIEW_TYPE_ADD_CATEGORY;
	}

	public NavigationCursorAdapter(Context context, int flags, OnClickListener addCategoryClickListener) {
		super(context, addMainNavigationItems(null), flags);
		mInflater = LayoutInflater.from(context);
		mAddCategoryClickListener = addCategoryClickListener;
	}
	
	@Override
	public Cursor swapCursor(Cursor categories) {
		//change cursor internally calls swap cursor, so extra 
		// values should be added only once during swap
		return super.swapCursor(addMainNavigationItems(categories));
	}
	
	/**
	 * @see android.widget.ListAdapter#getView(int, View, ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		int viewType = getItemViewType(position);
		if(viewType == VIEW_TYPE_CHECKLIST) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.navlist_item_checklist,
								parent, false);
			}
			convertView.setEnabled(isEnabled(position));
			return convertView;
		}
		else if(viewType == VIEW_TYPE_ALL_PRODUCTS) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.navlist_item_all_products,
						parent, false);
			}
			convertView.setEnabled(isEnabled(position));
			return convertView;
		}
		else if(viewType == VIEW_TYPE_CATEGORY) {
			return super.getView(position, convertView, parent);
		}
		else if(viewType == VIEW_TYPE_ADD_CATEGORY) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.navlist_item_category_add,
								parent, false);
			}
			convertView.setEnabled(isEnabled(position));
			convertView.findViewById(R.id.tv_add_category).setOnClickListener(mAddCategoryClickListener);
			return convertView;
		}
		else return null;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return null; // does not support dropdown items due to bug in Android with different types of dropdown items 
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		Category category = cupboard().withCursor(cursor).get(Category.class);
		
		if(category._id == Products.PRODUCT_CATEGORY_NONE_ID) {
			holder.catColor.setBackgroundResource(R.drawable.no_category_color);
			holder.name.setText(R.string.nav_products_no_category);
		}
		else {
			holder.catColor.setBackgroundColor(category.color);
			holder.name.setText(category.name);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
	    View view = LayoutInflater.from(context).inflate(R.layout.navlist_item_category, parent, false);
	    ViewHolder holder = new ViewHolder(view);
	    view.setTag(holder);
	    return view;
	}
	
	static class ViewHolder {
		@InjectView(R.id.tv_nav_name) TextView name;
		@InjectView(R.id.v_nav_cat_color) View catColor;

		public ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}
}
