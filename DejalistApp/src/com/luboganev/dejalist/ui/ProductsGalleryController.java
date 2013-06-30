package com.luboganev.dejalist.ui;

import com.luboganev.dejalist.data.entities.Category;
import com.luboganev.dejalist.data.entities.Product;

public interface ProductsGalleryController {
	public void editCategory(Category category);
	public void deleteCategory(Category category);
	
	public void newProduct(Category category);
	public void editProduct(Product product);
	public void setProductsCategory(long[] productIds);
	public void deleteProducts(long[] productIds);
	
	public void registerProductsGalleryActionTaker(ProductsGalleryActionTaker actionTaker);
	public void unregisterProductsGalleryActionTaker();
}
