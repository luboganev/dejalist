package com.luboganev.dejalist.ui;

import com.luboganev.dejalist.data.entities.Category;

public interface CategoryActionTaker {
	public void updateShownCategory(Category category);
	public void setOptionMenuItemsVisible(boolean visible);
}
