package com.luboganev.dejalist.ui;

import com.luboganev.dejalist.data.entities.Category;

public interface CategoriesController {
	public void onCategoryEdited(Category category);
	
	public void onCategoryNewAction();
	public void onCategoryEditAction(Category category);
	public void onCategoryDeleteAction(Category category);
	
    /** Register an object that can take an action. */
    void registerCategories(CategoriesActionTaker categoriesDelegate);
    /** Unregister a previous register object. */
    void unregisterCategories();
}