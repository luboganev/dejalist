// Generated code from Butter Knife. Do not modify!
package com.luboganev.dejalist.ui;

import android.view.View;
import butterknife.Views.Finder;

public class CategoriesListCursorAdapter$ViewHolder$$ViewInjector {
  public static void inject(Finder finder, final com.luboganev.dejalist.ui.CategoriesListCursorAdapter.ViewHolder target, Object source) {
    View view;
    view = finder.findById(source, 2131492908);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131492908' for field 'catColor' was not found. If this field binding is optional add '@Optional'.");
    }
    target.catColor = (android.view.View) view;
    view = finder.findById(source, 2131492909);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131492909' for field 'name' was not found. If this field binding is optional add '@Optional'.");
    }
    target.name = (android.widget.TextView) view;
  }

  public static void reset(com.luboganev.dejalist.ui.CategoriesListCursorAdapter.ViewHolder target) {
    target.catColor = null;
    target.name = null;
  }
}
