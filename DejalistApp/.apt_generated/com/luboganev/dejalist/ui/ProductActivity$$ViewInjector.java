// Generated code from Butter Knife. Do not modify!
package com.luboganev.dejalist.ui;

import android.view.View;
import butterknife.Views.Finder;

public class ProductActivity$$ViewInjector {
  public static void inject(Finder finder, com.luboganev.dejalist.ui.ProductActivity target, Object source) {
    View view;
    view = finder.findById(source, 2131296266);
    target.mImage = (android.widget.ImageView) view;
    view = finder.findById(source, 2131296269);
    target.mName = (android.widget.EditText) view;
    view = finder.findById(source, 2131296271);
    target.mNewCategory = (android.widget.ImageButton) view;
    view = finder.findById(source, 2131296270);
    target.mCategory = (android.widget.Spinner) view;
    view = finder.findById(source, 2131296267);
    target.mChangeImage = (android.widget.ImageButton) view;
    view = finder.findById(source, 2131296268);
    target.mChangeCamera = (android.widget.ImageButton) view;
  }

  public static void reset(com.luboganev.dejalist.ui.ProductActivity target) {
    target.mImage = null;
    target.mName = null;
    target.mNewCategory = null;
    target.mCategory = null;
    target.mChangeImage = null;
    target.mChangeCamera = null;
  }
}
