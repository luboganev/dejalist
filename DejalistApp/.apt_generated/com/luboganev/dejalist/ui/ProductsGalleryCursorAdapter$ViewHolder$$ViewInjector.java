// Generated code from Butter Knife. Do not modify!
package com.luboganev.dejalist.ui;

import android.view.View;
import butterknife.Views.Finder;

public class ProductsGalleryCursorAdapter$ViewHolder$$ViewInjector {
  public static void inject(Finder finder, com.luboganev.dejalist.ui.ProductsGalleryCursorAdapter.ViewHolder target, Object source) {
    View view;
    view = finder.findById(source, 2131361815);
    target.image = (android.widget.ImageView) view;
    view = finder.findById(source, 2131361818);
    target.inList = (android.widget.ImageView) view;
    view = finder.findById(source, 2131361816);
    target.name = (android.widget.TextView) view;
    view = finder.findById(source, 2131361817);
    target.category = (android.view.View) view;
  }

  public static void reset(com.luboganev.dejalist.ui.ProductsGalleryCursorAdapter.ViewHolder target) {
    target.image = null;
    target.inList = null;
    target.name = null;
    target.category = null;
  }
}
