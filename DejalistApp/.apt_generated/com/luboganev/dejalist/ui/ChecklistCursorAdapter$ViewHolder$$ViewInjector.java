// Generated code from Butter Knife. Do not modify!
package com.luboganev.dejalist.ui;

import android.view.View;
import butterknife.Views.Finder;

public class ChecklistCursorAdapter$ViewHolder$$ViewInjector {
  public static void inject(Finder finder, final com.luboganev.dejalist.ui.ChecklistCursorAdapter.ViewHolder target, Object source) {
    View view;
    view = finder.findById(source, 2131492888);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131492888' for field 'image' was not found. If this field binding is optional add '@Optional'.");
    }
    target.image = (android.widget.ImageView) view;
    view = finder.findById(source, 2131492890);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131492890' for field 'isChecked' was not found. If this field binding is optional add '@Optional'.");
    }
    target.isChecked = (android.widget.ImageView) view;
    view = finder.findById(source, 2131492889);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131492889' for field 'name' was not found. If this field binding is optional add '@Optional'.");
    }
    target.name = (android.widget.TextView) view;
    view = finder.findById(source, 2131492887);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131492887' for field 'category' was not found. If this field binding is optional add '@Optional'.");
    }
    target.category = (android.view.View) view;
  }

  public static void reset(com.luboganev.dejalist.ui.ChecklistCursorAdapter.ViewHolder target) {
    target.image = null;
    target.isChecked = null;
    target.name = null;
    target.category = null;
  }
}
