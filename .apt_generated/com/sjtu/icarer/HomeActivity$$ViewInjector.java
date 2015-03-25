// Generated code from Butter Knife. Do not modify!
package com.sjtu.icarer;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class HomeActivity$$ViewInjector<T extends com.sjtu.icarer.HomeActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131361857, "field 'carerItemLayout'");
    target.carerItemLayout = finder.castView(view, 2131361857, "field 'carerItemLayout'");
    view = finder.findRequiredView(source, 2131361900, "field 'carerView'");
    target.carerView = finder.castView(view, 2131361900, "field 'carerView'");
    view = finder.findRequiredView(source, 2131361855, "field 'toolbar'");
    target.toolbar = finder.castView(view, 2131361855, "field 'toolbar'");
    view = finder.findRequiredView(source, 2131361861, "field 'roomNumView'");
    target.roomNumView = finder.castView(view, 2131361861, "field 'roomNumView'");
  }

  @Override public void reset(T target) {
    target.carerItemLayout = null;
    target.carerView = null;
    target.toolbar = null;
    target.roomNumView = null;
  }
}
