// Generated code from Butter Knife. Do not modify!
package com.luboganev.dejalist.ui;

import android.view.View;
import butterknife.Views.Finder;

public class ProductsGalleryFragment$$ViewInjector {
  public static void inject(Finder finder, final com.luboganev.dejalist.ui.ProductsGalleryFragment target, Object source) {
    View view;
    view = finder.findById(source, 2131427363);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427363' for field 'categoryColorHeader' was not found. If this field binding is optional add '@Optional'.");
    }
    target.categoryColorHeader = (android.view.View) view;
    view = finder.findById(source, 2131427365);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427365' for field 'mEmptyText' was not found. If this field binding is optional add '@Optional'.");
    }
    target.mEmptyText = (android.widget.TextView) view;
    view = finder.findById(source, 2131427366);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427366' for field 'mProducts' was not found. If this field binding is optional add '@Optional'.");
    }
    target.mProducts = (android.widget.GridView) view;
    view = finder.findById(source, 2131427364);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427364' for field 'mEmptyImage' was not found. If this field binding is optional add '@Optional'.");
    }
    target.mEmptyImage = (android.widget.ImageView) view;
  }

  public static void reset(com.luboganev.dejalist.ui.ProductsGalleryFragment target) {
    target.categoryColorHeader = null;
    target.mEmptyText = null;
    target.mProducts = null;
    target.mEmptyImage = null;
  }
}
