package com.luboganev.dejalist.ui;

import butterknife.InjectView;
import butterknife.Views;

import com.luboganev.dejalist.R;
import com.luboganev.dejalist.data.entities.Category;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ProductsGalleryFragment extends Fragment implements CategoriesActionTaker {
    public static final String ARG_CATEGORY = "category";
    
    @InjectView(R.id.v_category_colorheader) View categoryColorHeader;
    
    private Category mSelectedCategory;
    
    public ProductsGalleryFragment() {
        // Empty constructor required for fragment subclasses
    }
    
    CategoriesController mController;
    
    public static ProductsGalleryFragment getInstance() {
    	ProductsGalleryFragment fragment = new ProductsGalleryFragment();
    	fragment.setArguments(new Bundle());
    	return fragment;
    }
    
    public static ProductsGalleryFragment getInstance(Category category) {
    	ProductsGalleryFragment fragment = new ProductsGalleryFragment();
    	Bundle bundle = new Bundle();
    	bundle.putParcelable(ARG_CATEGORY, category);
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
        View rootView = inflater.inflate(R.layout.fragment_productsgallery, container, false);
        Views.inject(this, rootView);
        
        if(getArguments().containsKey(ARG_CATEGORY)) {
        	mSelectedCategory = getArguments().getParcelable(ARG_CATEGORY);
        	categoryColorHeader.setBackgroundColor(mSelectedCategory.color);
        	getActivity().setTitle(mSelectedCategory.name);
        } else {
        	mSelectedCategory = null;
        	categoryColorHeader.setVisibility(View.GONE);
        	getActivity().setTitle(R.string.nav_all_products);
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_products_sort_az:
        	if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
            Toast.makeText(getActivity(), "Clicked: sort az", Toast.LENGTH_SHORT).show();
            return true;
        case R.id.menu_products_sort_recent:
        	if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
            Toast.makeText(getActivity(), "Clicked: sort recent", Toast.LENGTH_SHORT).show();
            return true;   
        case R.id.menu_products_sort_usage:
            if (item.isChecked()) item.setChecked(false);
            else item.setChecked(true);
            Toast.makeText(getActivity(), "Clicked: sort usage", Toast.LENGTH_SHORT).show();
            return true;
        case R.id.menu_new_product:
            Toast.makeText(getActivity(), "Clicked: new product", Toast.LENGTH_SHORT).show();
            return true;  
        case R.id.menu_categories_new:
        	if(mController != null) mController.onCategoryNewAction();
            return true;
        case R.id.menu_categories_edit:
        	if(mController != null) mController.onCategoryEditAction(mSelectedCategory);
            return true;   
        case R.id.menu_categories_delete:
        	if(mController != null) mController.onCategoryDeleteAction(mSelectedCategory);
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
            // Instantiate the NoticeDialogListener so we can send events to the host
        	mController = (CategoriesController) activity;
        	mController.registerCategories(this);
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement CategoriesController");
        }
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
}
