package zebrostudio.wallr100.ui.main;

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

  private Unbinder unBinder;
  private GuillotineAnimation guillotineMenuAnimation;
  private Boolean isGuillotineMenuOpen;

  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.content_hamburger) View hamburgerVerticalImageViewButton;
  @BindView(R.id.buypro_textview_guillotine_menu) WallrCustomTextView buyProTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final View guillotineMenu = LayoutInflater.from(this)
        .inflate(R.layout.guillotine_menu_layout, null);
    /**
     * Add guillotine menu layout to the activity before binding Butterknife to the activity
     * so that guillotine menu views can be referenced by Butterknife as well
     */
    FrameLayout rootFrameLayout = findViewById(R.id.root_frame_layout);
    rootFrameLayout.addView(guillotineMenu);

    unBinder = ButterKnife.bind(this);

    setSupportActionBar(toolbar);

    GuillotineListener guillotineListener = new GuillotineListener() {
      @Override
      public void onGuillotineOpened() {
        isGuillotineMenuOpen = true;
      }

      @Override
      public void onGuillotineClosed() {
        isGuillotineMenuOpen = false;
      }
    };

    final int rippleDuration = 250;
    guillotineMenuAnimation = new GuillotineAnimation.GuillotineBuilder(
        guillotineMenu, guillotineMenu.findViewById(R.id.hamburger_guillotine_menu),
        hamburgerVerticalImageViewButton)
        .setStartDelay(rippleDuration)
        .setActionBarViewForAnimation(toolbar)
        .setGuillotineListener(guillotineListener)
        .setClosedOnStart(true)
        .build();

    // Change text color of buy pro option to black from the default white color
    buyProTextView.setTextColor(getResources().getColor(R.color.color_black));
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (unBinder != null) {
      unBinder.unbind();
    }
  }

  @Override public void onBackPressed() {
    if (isGuillotineMenuOpen) {
      guillotineMenuAnimation.close();
    } else {
      super.onBackPressed();
    }
  }
}