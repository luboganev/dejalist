// Generated code from Butter Knife. Do not modify!
package com.luboganev.dejalist.ui;

import android.view.View;
import butterknife.Views.Finder;

public class MainActivity$$ViewInjector {
  public static void inject(Finder finder, com.luboganev.dejalist.ui.MainActivity target, Object source) {
    View view;
    view = finder.findById(source, 2131165184);
    target.mDrawerLayout = (android.support.v4.widget.DrawerLayout) view;
    view = finder.findById(source, 2131165186);
    target.mDrawerList = (android.widget.ListView) view;
  }

  public static void reset(com.luboganev.dejalist.ui.MainActivity target) {
    target.mDrawerLayout = null;
    target.mDrawerList = null;
  }
}
