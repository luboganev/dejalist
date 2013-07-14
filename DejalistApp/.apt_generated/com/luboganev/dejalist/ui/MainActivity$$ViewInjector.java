// Generated code from Butter Knife. Do not modify!
package com.luboganev.dejalist.ui;

import android.view.View;
import butterknife.Views.Finder;

public class MainActivity$$ViewInjector {
  public static void inject(Finder finder, com.luboganev.dejalist.ui.MainActivity target, Object source) {
    View view;
    view = finder.findById(source, 2131361805);
    target.mDrawerList = (android.widget.ListView) view;
    view = finder.findById(source, 2131361800);
    target.mDrawerLayout = (android.support.v4.widget.DrawerLayout) view;
  }

  public static void reset(com.luboganev.dejalist.ui.MainActivity target) {
    target.mDrawerList = null;
    target.mDrawerLayout = null;
  }
}
