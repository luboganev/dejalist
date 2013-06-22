package com.luboganev.dejalist.ui;

import com.luboganev.dejalist.data.entities.Category;

public interface CategoryController {
	public void newCategory();
	public void editCategory(Category category);
	public void deleteCategory(Category category);
	
	public void registerCategoryActionTaker(CategoryActionTaker actionTaker);
	public void unregisterCategoryActionTaker();
}
