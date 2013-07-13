package com.luboganev.dejalist.ui;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;
import butterknife.InjectView;
import butterknife.Views;

import com.luboganev.dejalist.R;
import com.luboganev.dejalist.data.DejalistContract;
import com.luboganev.dejalist.data.DejalistContract.Products;
import com.luboganev.dejalist.data.SelectionBuilder;
import com.luboganev.dejalist.data.entities.Category;
import com.luboganev.dejalist.data.entities.Product;

import android.app.Activity;
import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class ProductsGalleryFragment extends Fragment implements ProductsGalleryActionTaker, LoaderCallbacks<Cursor>, OnItemClickListener, MultiChoiceModeListener {
    public static final String ARG_CATEGORY = "category";
    public static final String ARG_ALL_PRODUCTS = "all_products";
    public static final String ARG_NO_CATEGORY = "no_category";
    
    @InjectView(R.id.v_category_colorheader) View categoryColorHeader;
    @InjectView(R.id.grdv_products) GridView mProducts;
	@InjectView(R.id.iv_gallery_empty) ImageView mEmptyImage;	
	@InjectView(R.id.tv_gallery_empty) TextView mEmptyText;	
    
    private Category mSelectedCategory;
    
    private ProductsGalleryCursorAdapter mAdapter;
    
    private static final int LOADER_PRODUCTS_ID = 2;
    
    private static final String STATE_OPTIONMENUITEMSVISIBLE = "state_optionmenuitemsvisible"; 
    private static final String STATE_CHECKEDPRODUCTPOS = "state_checkedproductpos"; 
    private static final String STATE_SORTBY = "state_sortby"; 
    private boolean mOptionMenuItemsVisible; 
    private int mSortBy;
    private static final int SORT_AZ = 0;
    private static final int SORT_RECENT = 1;
    private static final int SORT_MOST = 2;
    private static final int SORT_CATEGORY = 3;
    private int[] mCheckedItemPos;
    
    public ProductsGalleryFragment() {
        // Empty constructor required for fragment subclasses
    }
    
    ProductsGalleryController mProductsGalleryController;
    
    public static ProductsGalleryFragment getInstanceAllProducts() {
    	ProductsGalleryFragment fragment = new ProductsGalleryFragment();
    	Bundle bundle = new Bundle();
    	bundle.putBoolean(ARG_ALL_PRODUCTS, true);
    	fragment.setArguments(bundle);
    	return fragment;
    }
    
    public static ProductsGalleryFragment getInstanceCategoryProducts(Category category) {
    	ProductsGalleryFragment fragment = new ProductsGalleryFragment();
    	Bundle bundle = new Bundle();
    	bundle.putParcelable(ARG_CATEGORY, category);
    	fragment.setArguments(bundle);
    	return fragment;
    }
    
    public static ProductsGalleryFragment getInstanceNoCategoryProducts() {
    	ProductsGalleryFragment fragment = new ProductsGalleryFragment();
    	Bundle bundle = new Bundle();
    	bundle.putBoolean(ARG_NO_CATEGORY, true);
    	fragment.setArguments(bundle);
    	return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	if(savedInstanceState != null) {
    		mOptionMenuItemsVisible = savedInstanceState.getBoolean(STATE_OPTIONMENUITEMSVISIBLE, true);
    		mSortBy = savedInstanceState.getInt(STATE_SORTBY, SORT_AZ);
    		mCheckedItemPos = savedInstanceState.getIntArray(STATE_CHECKEDPRODUCTPOS);
    	}
    	else {
    		mOptionMenuItemsVisible = true;	
    		mSortBy = SORT_AZ;
    		mCheckedItemPos = null;
    	}
    	setHasOptionsMenu(true);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putBoolean(STATE_OPTIONMENUITEMSVISIBLE, mOptionMenuItemsVisible);
    	outState.putInt(STATE_SORTBY, mSortBy);
    	if(mProducts.getCheckedItemCount() > 0) {
    		int[] checkedItemsPositions = new int[mProducts.getCheckedItemCount()];
    		SparseBooleanArray array = mProducts.getCheckedItemPositions();
    		int j=0;
			for (int i = 0; i < array.size(); i++) {
				if(array.valueAt(i)) {
					checkedItemsPositions[j] = array.keyAt(i);
					j++;
				}
			}   		
    		outState.putIntArray(STATE_CHECKEDPRODUCTPOS, checkedItemsPositions);
    	}
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	if(mProductsGalleryController != null) {
    		mProductsGalleryController.unregisterProductsGalleryActionTaker();
    	}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_productsgallery, container, false);
        Views.inject(this, rootView);
        
        if(getArguments().containsKey(ARG_CATEGORY)) {
        	mSelectedCategory = getArguments().getParcelable(ARG_CATEGORY);
        	categoryColorHeader.setBackgroundColor(mSelectedCategory.color);
        	getActivity().setTitle(mSelectedCategory.name);
        } else if(getArguments().containsKey(ARG_ALL_PRODUCTS)) {
        	mSelectedCategory = null;
        	categoryColorHeader.setVisibility(View.GONE);
        	getActivity().setTitle(R.string.nav_all_products);
        } else {
        	mSelectedCategory = null;
        	categoryColorHeader.setVisibility(View.GONE);
        	getActivity().setTitle(R.string.nav_products_no_category);
        }
        
        mProducts.setVisibility(View.INVISIBLE);
		mEmptyImage.setVisibility(View.INVISIBLE);
		mEmptyText.setVisibility(View.INVISIBLE);
        
        return rootView;
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	inflater.inflate( 
    			(getArguments().containsKey(ARG_CATEGORY) ? 
    					R.menu.menu_category : 
    						R.menu.menu_all_products), menu);
    	super.onCreateOptionsMenu(menu, inflater);
    }
    
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
    	super.onPrepareOptionsMenu(menu);
		menu.findItem(R.id.menu_products_sort).setVisible(mOptionMenuItemsVisible);
		menu.findItem(R.id.menu_products_sort).setEnabled(mOptionMenuItemsVisible);
		
		if(mOptionMenuItemsVisible) {
			switch (mSortBy) {
			default:
			case SORT_AZ:
				menu.findItem(R.id.menu_products_sort_az).setChecked(true);
				break;
			case SORT_RECENT:
				menu.findItem(R.id.menu_products_sort_recent).setChecked(true);
				break;
			case SORT_MOST:
				menu.findItem(R.id.menu_products_sort_usage).setChecked(true);
				break;
			case SORT_CATEGORY:
				menu.findItem(R.id.menu_products_sort_category).setChecked(true);
				break;
			}
		}
		
		menu.findItem(R.id.menu_new_product).setVisible(mOptionMenuItemsVisible);
		menu.findItem(R.id.menu_new_product).setEnabled(mOptionMenuItemsVisible);
		
		if(getArguments().containsKey(ARG_CATEGORY)) {
			menu.findItem(R.id.menu_categories_edit).setVisible(mOptionMenuItemsVisible);
			menu.findItem(R.id.menu_categories_edit).setEnabled(mOptionMenuItemsVisible);
			menu.findItem(R.id.menu_categories_delete).setVisible(mOptionMenuItemsVisible);
			menu.findItem(R.id.menu_categories_delete).setEnabled(mOptionMenuItemsVisible);
		}
		else if(getArguments().containsKey(ARG_NO_CATEGORY)) {
			menu.findItem(R.id.menu_products_sort_category).setVisible(false);
			menu.findItem(R.id.menu_products_sort_category).setEnabled(false);
		}
		else {
			menu.findItem(R.id.menu_products_sort_category).setVisible(mOptionMenuItemsVisible);
			menu.findItem(R.id.menu_products_sort_category).setEnabled(mOptionMenuItemsVisible);
		}
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_products_sort_az:
        	if (!item.isChecked()) {
        		item.setChecked(true);
        		mSortBy = SORT_AZ;
        		reloadProducts();
        	}
            return true;
        case R.id.menu_products_sort_recent:
        	if (!item.isChecked()) {
        		item.setChecked(true);
        		mSortBy = SORT_RECENT;
        		reloadProducts();
        	}
            return true;   
        case R.id.menu_products_sort_usage:
            if (!item.isChecked()) {
            	item.setChecked(true);
            	mSortBy = SORT_MOST;
            	reloadProducts();
            }
            return true;
        case R.id.menu_products_sort_category:
        	if (!item.isChecked()) {
        		item.setChecked(true);
        		mSortBy = SORT_CATEGORY;
        		reloadProducts();
        	}
        	return true;
        case R.id.menu_new_product:
            if(mProductsGalleryController != null) mProductsGalleryController.newProduct(mSelectedCategory);
            return true;  
        case R.id.menu_categories_edit:
        	if(mProductsGalleryController != null) mProductsGalleryController.editCategory(mSelectedCategory);
            return true;   
        case R.id.menu_categories_delete:
        	if(mProductsGalleryController != null) mProductsGalleryController.deleteCategory(mSelectedCategory);
            return true;  
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
        	mProductsGalleryController = (ProductsGalleryController) activity;
        	mProductsGalleryController.registerProductsGalleryActionTaker(this);
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ProductsGalleryController");
        }
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	
        mAdapter = new ProductsGalleryCursorAdapter(getActivity().getApplicationContext(), 
        		CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, mSelectedCategory == null);
        mProducts.setAdapter(mAdapter);
        mProducts.setOnItemClickListener(this);
        mProducts.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        mProducts.setMultiChoiceModeListener(this);
    	
        reloadProducts();
    }
   
    private static final String LOADER_EXTRA_SORT = "sort";
    
    public void reloadProducts() {
    	Bundle loaderExtras = new Bundle();
    	switch(mSortBy) {
		case SORT_AZ:
			loaderExtras.putString(LOADER_EXTRA_SORT, DejalistContract.Products.ORDER_NAME_ASC);
			break;
		case SORT_MOST:
			loaderExtras.putString(LOADER_EXTRA_SORT, DejalistContract.Products.ORDER_USEDCOUNT_DESC + " , " + DejalistContract.Products.ORDER_NAME_ASC);
			break;
		case SORT_RECENT:
			loaderExtras.putString(LOADER_EXTRA_SORT, DejalistContract.Products.ORDER_LAST_USED_DESC + " , " + DejalistContract.Products.ORDER_NAME_ASC);
			break;
		case SORT_CATEGORY:
			loaderExtras.putString(LOADER_EXTRA_SORT, DejalistContract.Products.ORDER_CATEGORY + " , " + DejalistContract.Products.ORDER_NAME_ASC);
			break;
		}
    	if(getActivity().getSupportLoaderManager().getLoader(LOADER_PRODUCTS_ID) != null) {
        	getActivity().getSupportLoaderManager().restartLoader(LOADER_PRODUCTS_ID, loaderExtras, this);
        }
        else getActivity().getSupportLoaderManager().initLoader(LOADER_PRODUCTS_ID, loaderExtras, this);
    }

	@Override
	public void updateShownCategory(Category category) {
		if(mSelectedCategory == null) return;
		if(mSelectedCategory._id == category._id) {
			mSelectedCategory = category;
			categoryColorHeader.setBackgroundColor(mSelectedCategory.color);
			getActivity().setTitle(mSelectedCategory.name);
		}
	}

	@Override
	public void setOptionMenuItemsVisible(boolean visible) {
		mOptionMenuItemsVisible = visible;
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if(id == LOADER_PRODUCTS_ID) {
			if(mSelectedCategory != null) {
				return new CursorLoader(getActivity().getApplicationContext(), 
						DejalistContract.Products.buildCategoryProductsUri(mSelectedCategory._id), null, Products.SELECTION_NOT_DELETED, null, args.getString(LOADER_EXTRA_SORT));
			}
			else if(getArguments().containsKey(ARG_NO_CATEGORY)) {
				return new CursorLoader(getActivity().getApplicationContext(), 
						DejalistContract.Products.CONTENT_URI, null, 
						new SelectionBuilder().where(Products.SELECTION_NO_CATEGORY, (String[])null)
						.where(Products.SELECTION_NOT_DELETED, (String[])null).getSelection()
						, null, args.getString(LOADER_EXTRA_SORT));
			}
			else {
				return new CursorLoader(getActivity().getApplicationContext(), 
						DejalistContract.Products.CONTENT_URI, null, Products.SELECTION_NOT_DELETED, null, args.getString(LOADER_EXTRA_SORT));
			}
		}
		return null;
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if(loader.getId() == LOADER_PRODUCTS_ID) {
			//data.setNotificationUri(getActivity().getContentResolver(), DejalistContract.Products.CONTENT_URI);
			mAdapter.changeCursor(data);
			if(mCheckedItemPos != null) {
				for (int itemPos : mCheckedItemPos) {
					mProducts.setItemChecked(itemPos, true);
				}
				mCheckedItemPos = null;
			}
			if(data.getCount() == 0) {
				mProducts.setVisibility(View.INVISIBLE);
				mEmptyImage.setVisibility(View.VISIBLE);
				mEmptyText.setVisibility(View.VISIBLE);
			}
			else {
				mProducts.setVisibility(View.VISIBLE);
				mEmptyImage.setVisibility(View.INVISIBLE);
				mEmptyText.setVisibility(View.INVISIBLE);
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if(loader.getId() == LOADER_PRODUCTS_ID) {
			mAdapter.changeCursor(null);
			mProducts.setVisibility(View.INVISIBLE);
			mEmptyImage.setVisibility(View.VISIBLE);
			mEmptyText.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ProductsGalleryCursorAdapter.ViewHolder holder = (ProductsGalleryCursorAdapter.ViewHolder)view.getTag();
		ContentValues values = new ContentValues();
		if(holder.inList.getVisibility() == View.VISIBLE) {
			values.put(Products.PRODUCT_INLIST, 0);
			values.put(Products.PRODUCT_CHECKED, 0);
			holder.inList.setVisibility(View.INVISIBLE);
		}
		else {
			values.put(Products.PRODUCT_INLIST, 1);
			holder.inList.setVisibility(View.VISIBLE);
		}
		getActivity().getContentResolver().update(Products.buildProductUri(id), values, null, null);
		reloadProducts();
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		// Respond to clicks on the actions in the CAB
        switch (item.getItemId()) {
            case R.id.menu_cab_products_edit:
            	if(mProductsGalleryController != null) {
            		SparseBooleanArray checkedItems = mProducts.getCheckedItemPositions();
            		for (int i = 0; i < checkedItems.size(); i++) {
						if(checkedItems.valueAt(i)) {
							Cursor c = mAdapter.getCursor();
							c.moveToPosition(checkedItems.keyAt(i));
							Product product = cupboard().withCursor(c).get(Product.class);
							mProductsGalleryController.editProduct(product);
							break;
						}
					}
            	}
                mode.finish(); // Action picked, so close the CAB
                return true;
            case R.id.menu_cab_products_set_category:
            	if(mProductsGalleryController != null) {
            		mProductsGalleryController.setProductsCategory(mProducts.getCheckedItemIds());
            	}
                mode.finish(); // Action picked, so close the CAB
                return true;
            case R.id.menu_cab_products_delete:
            	if(mProductsGalleryController != null) {
            		long[] deletedItemIds = mProducts.getCheckedItemIds();
            		mProductsGalleryController.deleteProducts(deletedItemIds);
            	}
                mode.finish(); // Action picked, so close the CAB
                return true;
            default:
                return false;
        }
	}
	
	private ActionMode mActionMode;

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		mActionMode = mode;
		// Inflate the menu for the CAB
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_cab_products, menu);
        return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		mActionMode = null;
		 // Here you can make any necessary updates to the activity when
        // the CAB is removed. By default, selected items are deselected/unchecked.
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		// Here you can perform updates to the CAB due to
        // an invalidate() request
		MenuItem editItem = mode.getMenu().findItem(R.id.menu_cab_products_edit);
		MenuItem setCategoryItem = mode.getMenu().findItem(R.id.menu_cab_products_set_category);
    	if(mProducts.getCheckedItemCount() == 1) {
    		editItem.setVisible(true);
    		editItem.setEnabled(true);
    		setCategoryItem.setVisible(false);
    		setCategoryItem.setEnabled(false);
    	}
    	else {
    		editItem.setVisible(false);
    		editItem.setEnabled(false);
    		setCategoryItem.setVisible(true);
    		setCategoryItem.setEnabled(true);
    	}
        return true;
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position,
			long id, boolean checked) {
		// Here you can do something when items are selected/de-selected,
        // such as update the title in the CAB
		int count = mProducts.getCheckedItemCount();
		if(checked && count == 2) mode.invalidate();
		else if (count == 1) mode.invalidate();
		Resources res = getResources();
    	String text = String.format(res.getString(R.string.menu_cab_products_title), mProducts.getCheckedItemCount());
    	mode.setTitle(text);
	}

	@Override
	public void closeActionMode() {
		if(mActionMode != null) mActionMode.finish();
	}
}
