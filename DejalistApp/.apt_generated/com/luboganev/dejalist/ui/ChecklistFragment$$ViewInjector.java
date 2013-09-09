// Generated code from Butter Knife. Do not modify!
package com.luboganev.dejalist.ui;

import android.view.View;
import butterknife.Views.Finder;

public class ChecklistFragment$$ViewInjector {
  public static void inject(Finder finder, final com.luboganev.dejalist.ui.ChecklistFragment target, Object source) {
    View view;
    view = finder.findById(source, 2131427358);
    target.mEmptyImage = (android.widget.ImageView) view;
    view = finder.findById(source, 2131427359);
    target.mEmptyText = (android.widget.TextView) view;
    view = finder.findById(source, 2131427361);
    target.mAddProducts = (android.widget.ImageButton) view;
    view = finder.findById(source, 2131427360);
    target.mProducts = (android.widget.ListView) view;
  }

  public static void reset(com.luboganev.dejalist.ui.ChecklistFragment target) {
    target.mEmptyImage = null;
    target.mEmptyText = null;
    target.mAddProducts = null;
    target.mProducts = null;
  }
}
