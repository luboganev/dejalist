package com.luboganev.dejalist.ui;

public interface ChecklistController {
	public void removeProducts(long[] productIds);
	public void clearCheckList(boolean onlyChecked);
	public void addCheckListProductsClicked();
	
	public void registerChecklistActionTaker(ChecklistActionTaker actionTaker);
	public void unregisterChecklistActionTaker();
}
