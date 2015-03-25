// Generated code from Butter Knife. Do not modify!
package com.sjtu.icarer.ui.elder;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class ElderAdapter$ViewHolder$$ViewInjector<T extends com.sjtu.icarer.ui.elder.ElderAdapter.ViewHolder> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131361873, "field 'elderImageImage'");
    target.elderImageImage = finder.castView(view, 2131361873, "field 'elderImageImage'");
    view = finder.findRequiredView(source, 2131361875, "field 'elderHintView'");
    target.elderHintView = finder.castView(view, 2131361875, "field 'elderHintView'");
    view = finder.findRequiredView(source, 2131361874, "field 'elderInfoView'");
    target.elderInfoView = finder.castView(view, 2131361874, "field 'elderInfoView'");
  }

  @Override public void reset(T target) {
    target.elderImageImage = null;
    target.elderHintView = null;
    target.elderInfoView = null;
  }
}
