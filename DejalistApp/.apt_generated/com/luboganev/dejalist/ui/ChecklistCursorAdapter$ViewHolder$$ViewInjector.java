// Generated code from Butter Knife. Do not modify!
package com.luboganev.dejalist.ui;

import android.view.View;
import butterknife.Views.Finder;

public class ChecklistCursorAdapter$ViewHolder$$ViewInjector {
  public static void inject(Finder finder, final com.luboganev.dejalist.ui.ChecklistCursorAdapter.ViewHolder target, Object source) {
    View view;
    view = finder.findById(source, 2131427354);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427354' for field 'isChecked' was not found. If this field binding is optional add '@Optional'.");
    }
    target.isChecked = (android.widget.ImageView) view;
    view = finder.findById(source, 2131427352);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427352' for field 'image' was not found. If this field binding is optional add '@Optional'.");
    }
    target.image = (android.widget.ImageView) view;
    view = finder.findById(source, 2131427351);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427351' for field 'category' was not found. If this field binding is optional add '@Optional'.");
    }
    target.category = (android.view.View) view;
    view = finder.findById(source, 2131427353);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427353' for field 'name' was not found. If this field binding is optional add '@Optional'.");
    }
    target.name = (android.widget.TextView) view;
  }

  public static void reset(com.luboganev.dejalist.ui.ChecklistCursorAdapter.ViewHolder target) {
    target.isChecked = null;
    target.image = null;
    target.category = null;
    target.name = null;
  }
}
