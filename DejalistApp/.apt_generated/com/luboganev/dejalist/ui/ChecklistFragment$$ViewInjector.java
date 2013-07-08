// Generated code from Butter Knife. Do not modify!
package com.luboganev.dejalist.ui;

import android.view.View;
import butterknife.Views.Finder;

public class ChecklistFragment$$ViewInjector {
  public static void inject(Finder finder, com.luboganev.dejalist.ui.ChecklistFragment target, Object source) {
    View view;
    view = finder.findById(source, 2131361820);
    target.mEmptyText = (android.widget.TextView) view;
    view = finder.findById(source, 2131361819);
    target.mEmptyImage = (android.widget.ImageView) view;
    view = finder.findById(source, 2131361821);
    target.mProducts = (android.widget.ListView) view;
  }

  public static void reset(com.luboganev.dejalist.ui.ChecklistFragment target) {
    target.mEmptyText = null;
    target.mEmptyImage = null;
    target.mProducts = null;
  }
}
