package com.luboganev.dejalist.ui;

public interface ChecklistController {
	public void removeProducts(long[] productIds);
	public void clearCheckList();
	
	public void registerChecklistActionTaker(ChecklistActionTaker actionTaker);
	public void unregisterChecklistActionTaker();
}
