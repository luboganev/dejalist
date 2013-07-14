// Generated code from Butter Knife. Do not modify!
package com.luboganev.dejalist.ui;

import android.view.View;
import butterknife.Views.Finder;

public class ProductsGalleryFragment$$ViewInjector {
  public static void inject(Finder finder, com.luboganev.dejalist.ui.ProductsGalleryFragment target, Object source) {
    View view;
    view = finder.findById(source, 2131361825);
    target.categoryColorHeader = (android.view.View) view;
    view = finder.findById(source, 2131361827);
    target.mEmptyText = (android.widget.TextView) view;
    view = finder.findById(source, 2131361828);
    target.mProducts = (android.widget.GridView) view;
    view = finder.findById(source, 2131361826);
    target.mEmptyImage = (android.widget.ImageView) view;
  }

  public static void reset(com.luboganev.dejalist.ui.ProductsGalleryFragment target) {
    target.categoryColorHeader = null;
    target.mEmptyText = null;
    target.mProducts = null;
    target.mEmptyImage = null;
  }
}
