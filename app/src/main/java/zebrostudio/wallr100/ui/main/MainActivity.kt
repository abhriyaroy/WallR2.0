package zebrostudio.wallr100.ui.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import zebrostudio.wallr100.R

class MainActivity : AppCompatActivity() {

  var isGuillotineMenuOpen = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }

}