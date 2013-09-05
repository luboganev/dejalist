// Generated code from Butter Knife. Do not modify!
package com.luboganev.dejalist.ui;

import android.view.View;
import butterknife.Views.Finder;

public class ProductActivity$$ViewInjector {
  public static void inject(Finder finder, final com.luboganev.dejalist.ui.ProductActivity target, Object source) {
    View view;
    view = finder.findById(source, 2131427346);
    target.mChangeImage = (android.widget.ImageButton) view;
    view = finder.findById(source, 2131427350);
    target.mNewCategory = (android.widget.ImageButton) view;
    view = finder.findById(source, 2131427344);
    target.mProductImageHint = (android.widget.TextView) view;
    view = finder.findById(source, 2131427349);
    target.mCategory = (android.widget.Spinner) view;
    view = finder.findById(source, 2131427348);
    target.mName = (android.widget.EditText) view;
    view = finder.findById(source, 2131427347);
    target.mChangeCamera = (android.widget.ImageButton) view;
    view = finder.findById(source, 2131427345);
    target.mImage = (android.widget.ImageView) view;
  }

  public static void reset(com.luboganev.dejalist.ui.ProductActivity target) {
    target.mChangeImage = null;
    target.mNewCategory = null;
    target.mProductImageHint = null;
    target.mCategory = null;
    target.mName = null;
    target.mChangeCamera = null;
    target.mImage = null;
  }
}
