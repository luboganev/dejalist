// Generated code from Butter Knife. Do not modify!
package com.luboganev.dejalist.ui;

import android.view.View;
import butterknife.Views.Finder;

public class ProductActivity$$ViewInjector {
  public static void inject(Finder finder, com.luboganev.dejalist.ui.ProductActivity target, Object source) {
    View view;
    view = finder.findById(source, 2131361808);
    target.mImage = (android.widget.ImageView) view;
    view = finder.findById(source, 2131361813);
    target.mNewCategory = (android.widget.ImageButton) view;
    view = finder.findById(source, 2131361812);
    target.mCategory = (android.widget.Spinner) view;
    view = finder.findById(source, 2131361807);
    target.mProductImageHint = (android.widget.TextView) view;
    view = finder.findById(source, 2131361811);
    target.mName = (android.widget.EditText) view;
    view = finder.findById(source, 2131361809);
    target.mChangeImage = (android.widget.ImageButton) view;
    view = finder.findById(source, 2131361810);
    target.mChangeCamera = (android.widget.ImageButton) view;
  }

  public static void reset(com.luboganev.dejalist.ui.ProductActivity target) {
    target.mImage = null;
    target.mNewCategory = null;
    target.mCategory = null;
    target.mProductImageHint = null;
    target.mName = null;
    target.mChangeImage = null;
    target.mChangeCamera = null;
  }
}
