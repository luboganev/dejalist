// Generated code from Butter Knife. Do not modify!
package com.luboganev.dejalist.ui;

import android.view.View;
import butterknife.Views.Finder;

public class CategoryDialogFragment$$ViewInjector {
  public static void inject(Finder finder, com.luboganev.dejalist.ui.CategoryDialogFragment target, Object source) {
    View view;
    view = finder.findById(source, 2131296271);
    target.name = (android.widget.EditText) view;
    view = finder.findById(source, 2131296273);
    target.svBar = (com.larswerkman.colorpicker.SVBar) view;
    view = finder.findById(source, 2131296272);
    target.picker = (com.larswerkman.colorpicker.ColorPicker) view;
  }

  public static void reset(com.luboganev.dejalist.ui.CategoryDialogFragment target) {
    target.name = null;
    target.svBar = null;
    target.picker = null;
  }
}
