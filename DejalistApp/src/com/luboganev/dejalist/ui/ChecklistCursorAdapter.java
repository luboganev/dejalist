package com.luboganev.dejalist.ui;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

import java.util.HashMap;

import butterknife.InjectView;
import butterknife.Views;

import com.luboganev.dejalist.R;
import com.luboganev.dejalist.data.DejalistContract.Categories;
import com.luboganev.dejalist.data.DejalistContract.Products;
import com.luboganev.dejalist.data.entities.Category;
import com.luboganev.dejalist.data.entities.Product;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class ChecklistCursorAdapter extends CursorAdapter {
	private LayoutInflater mInflater;
	
	private HashMap<Long, Integer> mCategoriesColorsCache;
	
	private static Cursor getEmptyCursor() {
		return new MatrixCursor(new String[]{
				Products._ID,
				Products.PRODUCT_NAME,
				Products.PRODUCT_CATEGORY_ID,
				Products.PRODUCT_URI,
				Products.PRODUCT_CHECKED});
	}

	public ChecklistCursorAdapter(Context context, int flags) {
		super(context, getEmptyCursor(), flags);
		mInflater = LayoutInflater.from(context);
		mCategoriesColorsCache = new HashMap<Long, Integer>();
	}
	
	@Override
	public Cursor swapCursor(Cursor newCursor) {
		mCategoriesColorsCache.clear();
		return super.swapCursor(newCursor);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		Product product = cupboard().withCursor(cursor).get(Product.class);
		holder.usedCount = product.usedCount;

		if(product.categoryId >= 0) {
			Integer color = mCategoriesColorsCache.get(product.categoryId);
			if(color == null) {
				color = cupboard().withContext(context).get(Categories.buildCategoryUri(product.categoryId), Category.class).color;
				mCategoriesColorsCache.put(product.categoryId, color);
			}
			holder.category.setBackgroundColor(color);
			holder.category.setVisibility(View.VISIBLE);
		}
		else {
			holder.category.setVisibility(View.INVISIBLE);
		}
		
		holder.name.setText(product.name);
		
		holder.image.setImageResource(R.drawable.product_no_pic_small);
		
		if(product.uri != null) {
			Picasso.with(context).load(product.uri).resizeDimen(R.dimen.checklist_row_height, R.dimen.checklist_row_height).into(holder.image);
		}
		if(product.checked == 1) {
			holder.isChecked.setImageResource(R.drawable.btn_check_on_holo_light);
			holder.name.getPaint().setStrikeThruText(true);
			holder.bought = true;
		}
		else {
			holder.isChecked.setImageResource(R.drawable.btn_check_off_holo_light);
			holder.name.getPaint().setStrikeThruText(false);
			holder.bought = false;
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.checklist_item_product, parent, false);
	    ViewHolder holder = new ViewHolder(view);
	    view.setTag(holder);
	    return view;
	}
	
	static class ViewHolder {
		@InjectView(R.id.checklist_item_product_name) TextView name;
		@InjectView(R.id.checklist_item_product_image) ImageView image;
		@InjectView(R.id.checklist_item_product_category) View category;
		@InjectView(R.id.checklist_item_checked) ImageView isChecked;
		int usedCount = 0;
		boolean bought = false;

		public ViewHolder(View view) {
			Views.inject(this, view);
		}
	}
}
