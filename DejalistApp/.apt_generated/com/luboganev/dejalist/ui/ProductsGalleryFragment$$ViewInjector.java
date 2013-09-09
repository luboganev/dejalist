// Generated code from Butter Knife. Do not modify!
package com.luboganev.dejalist.ui;

import android.view.View;
import butterknife.Views.Finder;

public class ProductsGalleryFragment$$ViewInjector {
  public static void inject(Finder finder, final com.luboganev.dejalist.ui.ProductsGalleryFragment target, Object source) {
    View view;
    view = finder.findById(source, 2131427364);
    target.mEmptyText = (android.widget.TextView) view;
    view = finder.findById(source, 2131427365);
    target.mProducts = (android.widget.GridView) view;
    view = finder.findById(source, 2131427362);
    target.categoryColorHeader = (android.view.View) view;
    view = finder.findById(source, 2131427363);
    target.mEmptyImage = (android.widget.ImageView) view;
  }

  public static void reset(com.luboganev.dejalist.ui.ProductsGalleryFragment target) {
    target.mEmptyText = null;
    target.mProducts = null;
    target.categoryColorHeader = null;
    target.mEmptyImage = null;
  }
}
