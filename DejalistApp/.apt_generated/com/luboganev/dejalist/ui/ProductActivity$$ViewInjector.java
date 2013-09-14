// Generated code from Butter Knife. Do not modify!
package com.luboganev.dejalist.ui;

import android.view.View;
import butterknife.Views.Finder;

public class ProductActivity$$ViewInjector {
  public static void inject(Finder finder, final com.luboganev.dejalist.ui.ProductActivity target, Object source) {
    View view;
    view = finder.findById(source, 2131427348);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427348' for field 'mName' was not found. If this field binding is optional add '@Optional'.");
    }
    target.mName = (android.widget.EditText) view;
    view = finder.findById(source, 2131427345);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427345' for field 'mImage' was not found. If this field binding is optional add '@Optional'.");
    }
    target.mImage = (android.widget.ImageView) view;
    view = finder.findById(source, 2131427344);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427344' for field 'mProductImageHint' was not found. If this field binding is optional add '@Optional'.");
    }
    target.mProductImageHint = (android.widget.TextView) view;
    view = finder.findById(source, 2131427350);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427350' for field 'mNewCategory' was not found. If this field binding is optional add '@Optional'.");
    }
    target.mNewCategory = (android.widget.ImageButton) view;
    view = finder.findById(source, 2131427346);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427346' for field 'mChangeImage' was not found. If this field binding is optional add '@Optional'.");
    }
    target.mChangeImage = (android.widget.ImageButton) view;
    view = finder.findById(source, 2131427349);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427349' for field 'mCategory' was not found. If this field binding is optional add '@Optional'.");
    }
    target.mCategory = (android.widget.Spinner) view;
    view = finder.findById(source, 2131427347);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427347' for field 'mChangeCamera' was not found. If this field binding is optional add '@Optional'.");
    }
    target.mChangeCamera = (android.widget.ImageButton) view;
  }

  public static void reset(com.luboganev.dejalist.ui.ProductActivity target) {
    target.mName = null;
    target.mImage = null;
    target.mProductImageHint = null;
    target.mNewCategory = null;
    target.mChangeImage = null;
    target.mCategory = null;
    target.mChangeCamera = null;
  }
}
