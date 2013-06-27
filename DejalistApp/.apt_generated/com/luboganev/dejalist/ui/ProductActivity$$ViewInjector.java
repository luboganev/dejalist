// Generated code from Butter Knife. Do not modify!
package com.luboganev.dejalist.ui;

import android.view.View;
import butterknife.Views.Finder;

public class ProductActivity$$ViewInjector {
  public static void inject(Finder finder, com.luboganev.dejalist.ui.ProductActivity target, Object source) {
    View view;
    view = finder.findById(source, 2131361805);
    target.mName = (android.widget.EditText) view;
    view = finder.findById(source, 2131361802);
    target.mImage = (android.widget.ImageView) view;
    view = finder.findById(source, 2131361807);
    target.mNewCategory = (android.widget.ImageButton) view;
    view = finder.findById(source, 2131361803);
    target.mChangeImage = (android.widget.ImageButton) view;
    view = finder.findById(source, 2131361804);
    target.mChangeCamera = (android.widget.ImageButton) view;
    view = finder.findById(source, 2131361806);
    target.mCategory = (android.widget.Spinner) view;
  }

  public static void reset(com.luboganev.dejalist.ui.ProductActivity target) {
    target.mName = null;
    target.mImage = null;
    target.mNewCategory = null;
    target.mChangeImage = null;
    target.mChangeCamera = null;
    target.mCategory = null;
  }
}
