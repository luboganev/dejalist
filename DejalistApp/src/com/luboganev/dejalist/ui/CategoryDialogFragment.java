package com.luboganev.dejalist.ui;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import butterknife.InjectView;
import butterknife.Views;

import com.larswerkman.colorpicker.ColorPicker;
import com.larswerkman.colorpicker.SVBar;
import com.luboganev.dejalist.R;
import com.luboganev.dejalist.data.DejalistContract;
import com.luboganev.dejalist.data.entities.Category;

public class CategoryDialogFragment extends DialogFragment {
	@InjectView(R.id.category_picker) ColorPicker picker;
	@InjectView(R.id.category_svbar) SVBar svBar;
	@InjectView(R.id.et_category_name) EditText name;
	
	public static final String ARG_CATEGORY_ID = "category_id";
	
	public CategoryDialogFragment() {}
	
	public static CategoryDialogFragment getInstance(long categoryId) {
		CategoryDialogFragment fragment = new CategoryDialogFragment();
		Bundle arguments = new Bundle();
		arguments.putLong(ARG_CATEGORY_ID, categoryId);
		fragment.setArguments(arguments);
		return fragment;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		// Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    View v = inflater.inflate(R.layout.dialogfragment_category, null);
	    Views.inject(this, v);
	    picker.addSVBar(svBar);
	    
    	if(getArguments().containsKey(ARG_CATEGORY_ID)) {
    		Category category = cupboard().withContext(getActivity().getApplicationContext())
    				.get(DejalistContract.Categories.buildCategoryUri(getArguments().getLong(ARG_CATEGORY_ID)), 
    						Category.class);
    		name.setText(category.name);
    		picker.setColor(category.color);
    		picker.setOldCenterColor(category.color);
    	}
    	else {
    		//inflate controls with default
    	}
	    
	    builder.setView(v)
	    .setPositiveButton(R.string.dialogfragment_category_save, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				saveChanges();
				CategoryDialogFragment.this.getDialog().dismiss();
			}
		}).setNegativeButton(R.string.dialogfragment_category_cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				CategoryDialogFragment.this.getDialog().cancel();
			}
		});
	    
	    return builder.create();
	}
	
	private void saveChanges() {
		Category category = new Category();
		category.name = name.getText().length() <= 0 ? 
				getString(R.string.dialogfragment_category_utitled) 
				: name.getText().toString();
		category.color = picker.getColor();
		ContentValues categoryValues = cupboard().withEntity(Category.class).toContentValues(category);
		
		if(getArguments().containsKey(ARG_CATEGORY_ID)) {
			// if we edit an existing category
			getActivity().getContentResolver().update(
					DejalistContract.Categories.buildCategoryUri(getArguments().getLong(ARG_CATEGORY_ID)),
					categoryValues, null, null);
		}
		else {
			// if we create a new category
			getActivity().getContentResolver().insert(
					DejalistContract.Categories.CONTENT_URI, categoryValues);
		}
	}
}
