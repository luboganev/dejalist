package com.luboganev.dejalist.ui;

import com.luboganev.dejalist.data.entities.Category;

public interface ProductsGalleryController {
	public void editCategory(Category category);
	public void deleteCategory(Category category);
	
	public void newProduct(Category category);
	
	public void registerProductsGalleryActionTaker(ProductsGalleryActionTaker actionTaker);
	public void unregisterProductsGalleryActionTaker();
}
