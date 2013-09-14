// Generated code from Butter Knife. Do not modify!
package com.luboganev.dejalist.ui;

import android.view.View;
import butterknife.Views.Finder;

public class NavigationCursorAdapter$ViewHolder$$ViewInjector {
  public static void inject(Finder finder, final com.luboganev.dejalist.ui.NavigationCursorAdapter.ViewHolder target, Object source) {
    View view;
    view = finder.findById(source, 2131427374);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427374' for field 'name' was not found. If this field binding is optional add '@Optional'.");
    }
    target.name = (android.widget.TextView) view;
    view = finder.findById(source, 2131427375);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427375' for field 'catColor' was not found. If this field binding is optional add '@Optional'.");
    }
    target.catColor = (android.view.View) view;
  }

  public static void reset(com.luboganev.dejalist.ui.NavigationCursorAdapter.ViewHolder target) {
    target.name = null;
    target.catColor = null;
  }
}
