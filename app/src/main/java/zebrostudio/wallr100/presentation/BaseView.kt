package zebrostudio.wallr100.presentation

import com.uber.autodispose.ScopeProvider

interface BaseView {
  fun getScope(): ScopeProvider
  fun internetAvailability(): Boolean
}