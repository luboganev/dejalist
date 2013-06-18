package com.luboganev.dejalist.ui;

import com.luboganev.dejalist.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProductsGalleryFragment extends Fragment {
    public static final String ARG_CATEGORY_ID = "category_id";
    
    public ProductsGalleryFragment() {
        // Empty constructor required for fragment subclasses
    }
    
    public static ProductsGalleryFragment getInstance() {
    	return new ProductsGalleryFragment();
    }
    
    public static ProductsGalleryFragment getInstance(long categoryId) {
    	ProductsGalleryFragment fragment = new ProductsGalleryFragment();
    	Bundle bundle = new Bundle();
    	bundle.putLong(ARG_CATEGORY_ID, categoryId);
    	fragment.setArguments(bundle);
    	return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
        
        String category = null;
        if(getArguments().containsKey(ARG_CATEGORY_ID)) {
        	category = "CAT:"+getArguments().getLong(ARG_CATEGORY_ID);
        }
        else category = "All Products";
        
        ((TextView) rootView.findViewById(R.id.text)).setText(category);
        
        getActivity().setTitle(category);
        return rootView;
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	menu.add(Menu.NONE,  /** group ID.. not really needed unless you're working with groups **/
                1,         /** this is the items ID (get this in onOptionsItemSelected to determine what was clicked) **/
                Menu.NONE,
                getString(R.string.menu_categories_new))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    	menu.add(Menu.NONE,  /** group ID.. not really needed unless you're working with groups **/
    			2,         /** this is the items ID (get this in onOptionsItemSelected to determine what was clicked) **/
    			Menu.NONE,
    			getString(R.string.menu_categories_edit))
    			.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    	menu.add(Menu.NONE,  /** group ID.. not really needed unless you're working with groups **/
    			3,         /** this is the items ID (get this in onOptionsItemSelected to determine what was clicked) **/
    			Menu.NONE,
    			getString(R.string.menu_categories_delete))
    			.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    	super.onCreateOptionsMenu(menu, inflater);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case 1:
			// TODO: fix the ids
			// TODO: callback parent to show dialog if necessary
			return true;
		case 2:
			
			return true;
		case 3:
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
    }
}
