// Generated code from Butter Knife. Do not modify!
package com.luboganev.dejalist.ui;

import android.view.View;
import butterknife.Views.Finder;

public class ChecklistCursorAdapter$ViewHolder$$ViewInjector {
  public static void inject(Finder finder, com.luboganev.dejalist.ui.ChecklistCursorAdapter.ViewHolder target, Object source) {
    View view;
    view = finder.findById(source, 2131361814);
    target.name = (android.widget.TextView) view;
    view = finder.findById(source, 2131361813);
    target.category = (android.view.View) view;
    view = finder.findById(source, 2131361816);
    target.image = (android.widget.ImageView) view;
    view = finder.findById(source, 2131361815);
    target.isChecked = (android.widget.ImageView) view;
  }

  public static void reset(com.luboganev.dejalist.ui.ChecklistCursorAdapter.ViewHolder target) {
    target.name = null;
    target.category = null;
    target.image = null;
    target.isChecked = null;
  }
}
