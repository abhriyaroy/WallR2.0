package zebrostudio.wallr100.ui.main;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;
import com.zebrostudio.wallrcustoms.customtextview.WallrCustomTextView;
import zebrostudio.wallr100.R;

public class MainActivity extends AppCompatActivity {

  private final int rippleDuration = 250;
  private Unbinder unBinder;
  private GuillotineAnimation guillotineAnimation;
  private Boolean guillotineMenuOpened;
  private GuillotineListener guillotineListener;

  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.content_hamburger) View hamburgerVerticalImageViewButton;
  @BindView(R.id.buypro_textview_guillotine_menu) WallrCustomTextView buyProTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    final View guillotineMenu = LayoutInflater.from(this)
        .inflate(R.layout.guillotine_menu_layout, null);
    FrameLayout rootFrameLayout = findViewById(R.id.root_frame_layout);
    rootFrameLayout.addView(guillotineMenu);
    unBinder = ButterKnife.bind(this);
    if (toolbar != null) {
      setSupportActionBar(toolbar);
    }
    guillotineListener = new GuillotineListener() {
      @Override
      public void onGuillotineOpened() {
        guillotineMenuOpened = true;
      }

      @Override
      public void onGuillotineClosed() {
        guillotineMenuOpened = false;
      }
    };
    guillotineAnimation = new GuillotineAnimation.GuillotineBuilder(
        guillotineMenu, guillotineMenu.findViewById(R.id.hamburger_guillotine_menu),
        hamburgerVerticalImageViewButton)
        .setStartDelay(rippleDuration)
        .setActionBarViewForAnimation(toolbar)
        .setGuillotineListener(guillotineListener)
        .setClosedOnStart(true)
        .build();
    buyProTextView.setTextColor(getResources().getColor(R.color.color_black));
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (unBinder != null) {
      unBinder.unbind();
    }
  }

  @Override public void onBackPressed() {
    if (guillotineMenuOpened) {
      guillotineAnimation.close();
    } else {
      super.onBackPressed();
    }
  }
}