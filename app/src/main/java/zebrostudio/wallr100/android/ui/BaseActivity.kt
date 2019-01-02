package zebrostudio.wallr100.android.ui

import android.arch.lifecycle.Lifecycle
import android.support.v7.app.AppCompatActivity
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import zebrostudio.wallr100.presentation.BaseView

abstract class BaseActivity : AppCompatActivity(), BaseView {

  override fun getScope(): ScopeProvider {
    return AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)
  }

}