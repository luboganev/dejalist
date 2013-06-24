package com.luboganev.dejalist.ui;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Views;

import com.luboganev.dejalist.R;
import com.luboganev.dejalist.data.DejalistContract.Categories;
import com.luboganev.dejalist.data.entities.Category;

public class NavigationCursorAdapter extends CursorAdapter {
	public static final int VIEW_TYPE_NAVIGATION = 0; 
	public static final int VIEW_TYPE_HEADER = 1; 
	
	public static final long NAV_CHECKLIST_ITEM_ID = -101;
	public static final long NAV_MY_ITEMS_ITEM_ID = -102;
	
    private LayoutInflater mInflater;
	
	private static Cursor addMainNavigationItems(Cursor categories) {
		MatrixCursor mainNavigation = new MatrixCursor(new String[] {Categories._ID, Categories.CATEGORY_NAME, Categories.CATEGORY_COLOR});
		mainNavigation.addRow(new Object[]{NAV_CHECKLIST_ITEM_ID, "", 0});
		mainNavigation.addRow(new Object[]{NAV_MY_ITEMS_ITEM_ID, "", 0});
		if(categories != null) return new MergeCursor(new Cursor[]{mainNavigation, categories});
		else return mainNavigation;
	}
	
	@Override
	public int getCount() {
		return super.getCount() + 1;
	}
	
	@Override
	public int getItemViewType(int position) {
		return position == 2 ? VIEW_TYPE_HEADER : VIEW_TYPE_NAVIGATION;
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public Object getItem(int position) {
		if(position == 2) {
			return null;
		}
		else {
			if(position > 2) return super.getItem(position - 1);
			else return super.getItem(position);
		}
	}
	
	@Override
	public long getItemId(int position) {
		if(position == 2) {
			return -1;
		}
		else {
			if(position > 2) return super.getItemId(position - 1);
			else return super.getItemId(position);
		}
	}
	
	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}
	
	@Override
	public boolean isEnabled(int position) {
		return position != 2;
	}

	public NavigationCursorAdapter(Context context, int flags) {
		super(context, addMainNavigationItems(null), flags);
		mInflater = LayoutInflater.from(context);
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
		if(viewType == VIEW_TYPE_HEADER) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item_categories_header,
								parent, false);
			}
			convertView.setEnabled(isEnabled(position));
			return convertView;
		}
		else if(viewType == VIEW_TYPE_NAVIGATION) {
			if (position > 2) position--;
			return super.getView(position, convertView, parent);
		}
		else return null;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		int viewType = getItemViewType(position);
		if(viewType == VIEW_TYPE_HEADER) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item_categories_header,
								parent, false);
			}
			convertView.setEnabled(isEnabled(position));
			return convertView;
		}
		else if(viewType == VIEW_TYPE_NAVIGATION) {
			if (position > 2) position--;
			return super.getDropDownView(position, convertView, parent);
		}
		else return null;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		Category category = cupboard().withCursor(cursor).get(Category.class);
		
		if(category._id == NAV_CHECKLIST_ITEM_ID) {
			holder.name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_nav_list, 0, 0, 0);
			holder.catColor.setVisibility(View.GONE);
			holder.name.setText(R.string.nav_checklist);
		}
		else if(category._id == NAV_MY_ITEMS_ITEM_ID){
			holder.name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_nav_items, 0, 0, 0);
			holder.catColor.setVisibility(View.GONE);
			holder.name.setText(R.string.nav_my_products);
		}
		else {
			holder.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			holder.catColor.setVisibility(View.VISIBLE);
			holder.catColor.setBackgroundColor(category.color);
			holder.name.setText(category.name);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
	    View view = LayoutInflater.from(context).inflate(R.layout.list_item_navigation, parent, false);
	    ViewHolder holder = new ViewHolder(view);
	    view.setTag(holder);
	    return view;
	}
	
	static class ViewHolder {
		@InjectView(R.id.tv_nav_name) TextView name;
		@InjectView(R.id.v_nav_cat_color) View catColor;

		public ViewHolder(View view) {
			Views.inject(this, view);
			name.setCompoundDrawablePadding(8);
		}
	}
}
