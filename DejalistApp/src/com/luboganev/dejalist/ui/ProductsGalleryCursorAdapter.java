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
import android.widget.ImageView;
import android.widget.TextView;

public class ProductsGalleryCursorAdapter extends CursorAdapter {
	private LayoutInflater mInflater;
	
	private HashMap<Long, Integer> mCategoriesColorsCache;
	
	private boolean mShowCategories;
	
	private static Cursor getEmptyCursor() {
		return new MatrixCursor(new String[]{
				Products._ID,
				Products.PRODUCT_NAME,
				Products.PRODUCT_CATEGORY_ID,
				Products.PRODUCT_URI,
				Products.PRODUCT_INLIST});
	}

	public ProductsGalleryCursorAdapter(Context context, int flags, boolean showCategories) {
		super(context, getEmptyCursor(), flags);
		mShowCategories = showCategories;
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
		if(product.categoryId >= 0 && mShowCategories) {
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
		
		Picasso.with(context).load(product.uri).resizeDimen(R.dimen.product_picture_small_width, R.dimen.product_picture_small_width).into(holder.image);
		if(product.inlist == 1) {
			holder.inList.setVisibility(View.VISIBLE);
		}
		else {
			holder.inList.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.grid_item_product, parent, false);
	    ViewHolder holder = new ViewHolder(view);
	    view.setTag(holder);
	    return view;
	}
	
	static class ViewHolder {
		@InjectView(R.id.grid_item_product_name) TextView name;
		@InjectView(R.id.grid_item_product_image) ImageView image;
		@InjectView(R.id.grid_item_product_category) View category;
		@InjectView(R.id.grid_item_inlist) ImageView inList;

		public ViewHolder(View view) {
			Views.inject(this, view);
		}
	}
}
