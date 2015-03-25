// Generated code from Butter Knife. Do not modify!
package com.sjtu.icarer.ui.elder;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class ElderItemsFragment$$ViewInjector<T extends com.sjtu.icarer.ui.elder.ElderItemsFragment> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131361883, "field 'lvElderItemsView'");
    target.lvElderItemsView = finder.castView(view, 2131361883, "field 'lvElderItemsView'");
    view = finder.findRequiredView(source, 2131361882, "field 'lvEldersView'");
    target.lvEldersView = finder.castView(view, 2131361882, "field 'lvEldersView'");
  }

  @Override public void reset(T target) {
    target.lvElderItemsView = null;
    target.lvEldersView = null;
  }
}
