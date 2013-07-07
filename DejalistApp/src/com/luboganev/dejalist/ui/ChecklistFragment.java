package com.luboganev.dejalist.ui;

import butterknife.InjectView;
import butterknife.Views;

import com.luboganev.dejalist.R;
import com.luboganev.dejalist.Utils;
import com.luboganev.dejalist.data.DejalistContract;
import com.luboganev.dejalist.data.DejalistContract.Products;

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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.OnItemClickListener;

public class ChecklistFragment extends Fragment implements ChecklistActionTaker, LoaderCallbacks<Cursor>, OnItemClickListener, MultiChoiceModeListener {
	@InjectView(R.id.lv_checklist) ListView mProducts;	
	
	private ChecklistCursorAdapter mAdapter;
	
	private static final int LOADER_CHECKLIST_ID = 3;
	
	private static final String STATE_OPTIONMENUITEMSVISIBLE = "state_optionmenuitemsvisible"; 
    private static final String STATE_CHECKEDPRODUCTPOS = "state_checkedproductpos"; 
    private static final String STATE_SORTBY = "state_sortby"; 
    private boolean mOptionMenuItemsVisible; 
    private int mSortBy;
    private static final int SORT_AZ = 0;
    private static final int SORT_CATEGORY = 1;
    private static final int SORT_CHECKED = 2;
    private int[] mCheckedItemPos;
	
    public ChecklistFragment() {
        // Empty constructor required for fragment subclasses
    }
    
    private ChecklistController mChecklistController;
    
    public static ChecklistFragment getInstance() {
    	return new ChecklistFragment();
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
    	if(mChecklistController != null) {
    		mChecklistController.unregisterChecklistActionTaker();
    	}
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_checklist, container, false);
        Views.inject(this, rootView);
    	getActivity().setTitle(R.string.nav_checklist);
        return rootView;
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	inflater.inflate(R.menu.menu_checklist, menu);
    	super.onCreateOptionsMenu(menu, inflater);
    }
    
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
    	super.onPrepareOptionsMenu(menu);
		menu.findItem(R.id.menu_checklist_sort).setVisible(mOptionMenuItemsVisible);
		menu.findItem(R.id.menu_checklist_sort).setEnabled(mOptionMenuItemsVisible);
		
		if(mOptionMenuItemsVisible) {
			switch (mSortBy) {
			default:
			case SORT_AZ:
				menu.findItem(R.id.menu_checklist_sort_az).setChecked(true);
				break;
			case SORT_CATEGORY:
				menu.findItem(R.id.menu_checklist_sort_category).setChecked(true);
				break;
			case SORT_CHECKED:
				menu.findItem(R.id.menu_checklist_sort_checked).setChecked(true);
				break;
			}
		}
		
		menu.findItem(R.id.menu_checklist_clear).setVisible(mOptionMenuItemsVisible);
		menu.findItem(R.id.menu_checklist_clear).setEnabled(mOptionMenuItemsVisible);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_checklist_sort_az:
        	if (!item.isChecked()) {
        		item.setChecked(true);
        		mSortBy = SORT_AZ;
        		reloadProducts();
        	}
            return true;
        case R.id.menu_checklist_sort_category:
        	if (!item.isChecked()) {
        		item.setChecked(true);
        		mSortBy = SORT_CATEGORY;
        		reloadProducts();
        	}
            return true;   
        case R.id.menu_checklist_sort_checked:
            if (!item.isChecked()) {
            	item.setChecked(true);
            	mSortBy = SORT_CHECKED;
            	reloadProducts();
            }
            return true;
        case R.id.menu_checklist_clear:
            if(mChecklistController != null) mChecklistController.clearCheckList();
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
        	mChecklistController = (ChecklistController) activity;
        	mChecklistController.registerChecklistActionTaker(this);
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ChecklistController");
        }
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	
        mAdapter = new ChecklistCursorAdapter(getActivity().getApplicationContext(), 
        		CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
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
		case SORT_CATEGORY:
			loaderExtras.putString(LOADER_EXTRA_SORT, DejalistContract.Products.ORDER_CATEGORY + " , " + DejalistContract.Products.ORDER_NAME_ASC);
			break;
		case SORT_CHECKED:
			loaderExtras.putString(LOADER_EXTRA_SORT, DejalistContract.Products.ORDER_CHECKED + " , " + DejalistContract.Products.ORDER_NAME_ASC);
			break;
		}
    	if(getActivity().getSupportLoaderManager().getLoader(LOADER_CHECKLIST_ID) != null) {
        	getActivity().getSupportLoaderManager().restartLoader(LOADER_CHECKLIST_ID, loaderExtras, this);
        }
        else getActivity().getSupportLoaderManager().initLoader(LOADER_CHECKLIST_ID, loaderExtras, this);
    }

	@Override
	public void setOptionMenuItemsVisible(boolean visible) {
		mOptionMenuItemsVisible = visible;
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if(id == LOADER_CHECKLIST_ID) {
			return new CursorLoader(getActivity().getApplicationContext(), 
					DejalistContract.Products.CONTENT_URI, null,
					Products.SELECTION_NOT_DELETED + " AND " + Products.SELECTION_IN_LIST,
					null, args.getString(LOADER_EXTRA_SORT));
		}
		return null;	
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if(loader.getId() == LOADER_CHECKLIST_ID) {
			//data.setNotificationUri(getActivity().getContentResolver(), DejalistContract.Products.CONTENT_URI);
			mAdapter.changeCursor(data);
			if(mCheckedItemPos != null) {
				for (int itemPos : mCheckedItemPos) {
					mProducts.setItemChecked(itemPos, true);
				}
				mCheckedItemPos = null;
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if(loader.getId() == LOADER_CHECKLIST_ID) {
			mAdapter.changeCursor(null);
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ChecklistCursorAdapter.ViewHolder holder = (ChecklistCursorAdapter.ViewHolder)view.getTag();
		ContentValues values = new ContentValues();
		if(holder.isChecked.getVisibility() == View.VISIBLE) {
			values.put(Products.PRODUCT_CHECKED, 0);
			holder.isChecked.setVisibility(View.INVISIBLE);
			holder.name.getPaint().setStrikeThruText(false);
			holder.name.invalidate();
		}
		else {
			values.put(Products.PRODUCT_CHECKED, 1);
			values.put(Products.PRODUCT_LAST_USED, Utils.currentTimestampInSeconds());
			values.put(Products.PRODUCT_USED_COUNT, holder.usedCount + 1);
			holder.isChecked.setVisibility(View.VISIBLE);
			holder.name.getPaint().setStrikeThruText(true);
			holder.name.invalidate();
		}
		getActivity().getContentResolver().update(Products.buildProductUri(id), values, null, null);
		reloadProducts();
	}
	
	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		// Respond to clicks on the actions in the CAB
        switch (item.getItemId()) {
            case R.id.menu_cab_checklist_remove:
            	if(mChecklistController != null) {
            		long[] removedItemIds = mProducts.getCheckedItemIds();
            		mChecklistController.removeProducts(removedItemIds);
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
        inflater.inflate(R.menu.menu_cab_checklist, menu);
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
        return false;
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
    	String text = String.format(res.getString(R.string.menu_cab_checklist_title), mProducts.getCheckedItemCount());
    	mode.setTitle(text);
	}
	
	@Override
	public void closeActionMode() {
		if(mActionMode != null) mActionMode.finish();
	}
}
