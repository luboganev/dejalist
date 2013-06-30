package com.luboganev.dejalist.ui;

import com.luboganev.dejalist.data.entities.Category;

public interface ProductsGalleryActionTaker {
	public void updateShownCategory(Category category);
	public void setOptionMenuItemsVisible(boolean visible);
	public void closeActionMode();
}
