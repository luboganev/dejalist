// Generated code from Butter Knife. Do not modify!
package com.luboganev.dejalist.ui;

import android.view.View;
import butterknife.Views.Finder;

public class ProductsGalleryCursorAdapter$ViewHolder$$ViewInjector {
  public static void inject(Finder finder, final com.luboganev.dejalist.ui.ProductsGalleryCursorAdapter.ViewHolder target, Object source) {
    View view;
    view = finder.findById(source, 2131427369);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427369' for field 'category' was not found. If this field binding is optional add '@Optional'.");
    }
    target.category = (android.view.View) view;
    view = finder.findById(source, 2131427367);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427367' for field 'image' was not found. If this field binding is optional add '@Optional'.");
    }
    target.image = (android.widget.ImageView) view;
    view = finder.findById(source, 2131427370);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427370' for field 'inList' was not found. If this field binding is optional add '@Optional'.");
    }
    target.inList = (android.widget.ImageView) view;
    view = finder.findById(source, 2131427368);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427368' for field 'name' was not found. If this field binding is optional add '@Optional'.");
    }
    target.name = (android.widget.TextView) view;
  }

  public static void reset(com.luboganev.dejalist.ui.ProductsGalleryCursorAdapter.ViewHolder target) {
    target.category = null;
    target.image = null;
    target.inList = null;
    target.name = null;
  }
}
