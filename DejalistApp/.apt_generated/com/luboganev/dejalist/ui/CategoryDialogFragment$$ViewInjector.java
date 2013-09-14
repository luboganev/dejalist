// Generated code from Butter Knife. Do not modify!
package com.luboganev.dejalist.ui;

import android.view.View;
import butterknife.Views.Finder;

public class CategoryDialogFragment$$ViewInjector {
  public static void inject(Finder finder, final com.luboganev.dejalist.ui.CategoryDialogFragment target, Object source) {
    View view;
    view = finder.findById(source, 2131427355);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427355' for field 'name' was not found. If this field binding is optional add '@Optional'.");
    }
    target.name = (android.widget.EditText) view;
    view = finder.findById(source, 2131427357);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427357' for field 'valueBar' was not found. If this field binding is optional add '@Optional'.");
    }
    target.valueBar = (com.larswerkman.colorpicker.ValueBar) view;
    view = finder.findById(source, 2131427356);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427356' for field 'picker' was not found. If this field binding is optional add '@Optional'.");
    }
    target.picker = (com.larswerkman.colorpicker.ColorPicker) view;
    view = finder.findById(source, 2131427358);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427358' for field 'saturationBar' was not found. If this field binding is optional add '@Optional'.");
    }
    target.saturationBar = (com.larswerkman.colorpicker.SaturationBar) view;
  }

  public static void reset(com.luboganev.dejalist.ui.CategoryDialogFragment target) {
    target.name = null;
    target.valueBar = null;
    target.picker = null;
    target.saturationBar = null;
  }
}
