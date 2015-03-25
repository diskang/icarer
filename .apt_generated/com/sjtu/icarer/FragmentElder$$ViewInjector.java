// Generated code from Butter Knife. Do not modify!
package com.sjtu.icarer;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class FragmentElder$$ViewInjector<T extends com.sjtu.icarer.FragmentElder> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131361879, "field 'elderView'");
    target.elderView = finder.castView(view, 2131361879, "field 'elderView'");
    view = finder.findRequiredView(source, 2131361881, "field 'SubmitButton'");
    target.SubmitButton = finder.castView(view, 2131361881, "field 'SubmitButton'");
    view = finder.findRequiredView(source, 2131361880, "field 'ItemView'");
    target.ItemView = finder.castView(view, 2131361880, "field 'ItemView'");
  }

  @Override public void reset(T target) {
    target.elderView = null;
    target.SubmitButton = null;
    target.ItemView = null;
  }
}
