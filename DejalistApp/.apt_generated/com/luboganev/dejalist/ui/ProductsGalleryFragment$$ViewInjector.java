// Generated code from Butter Knife. Do not modify!
package com.luboganev.dejalist.ui;

import android.view.View;
import butterknife.Views.Finder;

public class ProductsGalleryFragment$$ViewInjector {
  public static void inject(Finder finder, com.luboganev.dejalist.ui.ProductsGalleryFragment target, Object source) {
    View view;
    view = finder.findById(source, 2131361816);
    target.categoryColorHeader = (android.view.View) view;
    view = finder.findById(source, 2131361817);
    target.mProducts = (android.widget.GridView) view;
  }

  public static void reset(com.luboganev.dejalist.ui.ProductsGalleryFragment target) {
    target.categoryColorHeader = null;
    target.mProducts = null;
  }
}
