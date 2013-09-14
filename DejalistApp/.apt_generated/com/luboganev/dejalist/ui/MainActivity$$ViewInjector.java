// Generated code from Butter Knife. Do not modify!
package com.luboganev.dejalist.ui;

import android.view.View;
import butterknife.Views.Finder;

public class MainActivity$$ViewInjector {
  public static void inject(Finder finder, final com.luboganev.dejalist.ui.MainActivity target, Object source) {
    View view;
    view = finder.findById(source, 2131427337);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427337' for field 'mDrawerLayout' was not found. If this field binding is optional add '@Optional'.");
    }
    target.mDrawerLayout = (android.support.v4.widget.DrawerLayout) view;
    view = finder.findById(source, 2131427342);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131427342' for field 'mDrawerList' was not found. If this field binding is optional add '@Optional'.");
    }
    target.mDrawerList = (android.widget.ListView) view;
  }

  public static void reset(com.luboganev.dejalist.ui.MainActivity target) {
    target.mDrawerLayout = null;
    target.mDrawerList = null;
  }
}
