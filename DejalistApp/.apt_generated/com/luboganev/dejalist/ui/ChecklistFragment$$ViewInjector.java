// Generated code from Butter Knife. Do not modify!
package com.luboganev.dejalist.ui;

import android.view.View;
import butterknife.Views.Finder;

public class ChecklistFragment$$ViewInjector {
  public static void inject(Finder finder, final com.luboganev.dejalist.ui.ChecklistFragment target, Object source) {
    View view;
    view = finder.findById(source, 2131492896);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131492896' for field 'mEmptyText' was not found. If this field binding is optional add '@Optional'.");
    }
    target.mEmptyText = (android.widget.TextView) view;
    view = finder.findById(source, 2131492897);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131492897' for field 'mProducts' was not found. If this field binding is optional add '@Optional'.");
    }
    target.mProducts = (android.widget.ListView) view;
    view = finder.findById(source, 2131492898);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131492898' for field 'mAddProducts' was not found. If this field binding is optional add '@Optional'.");
    }
    target.mAddProducts = (android.widget.ImageButton) view;
    view = finder.findById(source, 2131492895);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131492895' for field 'mEmptyImage' was not found. If this field binding is optional add '@Optional'.");
    }
    target.mEmptyImage = (android.widget.ImageView) view;
  }

  public static void reset(com.luboganev.dejalist.ui.ChecklistFragment target) {
    target.mEmptyText = null;
    target.mProducts = null;
    target.mAddProducts = null;
    target.mEmptyImage = null;
  }
}
