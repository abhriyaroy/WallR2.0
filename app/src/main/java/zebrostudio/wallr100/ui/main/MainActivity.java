package zebrostudio.wallr100.ui.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;
import com.zebrostudio.wallrcustoms.customtextview.WallrCustomTextView;
import java.util.ArrayList;
import zebrostudio.wallr100.R;

public class MainActivity extends AppCompatActivity {

  private Unbinder unBinder;
  private GuillotineAnimation guillotineMenuAnimation;
  private Boolean isGuillotineMenuOpen;

  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.content_hamburger) View hamburgerVerticalImageViewButton;
  @BindView(R.id.root_linear_layout_guillotine_menu) LinearLayout guillotineMenuRootLinearLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    initializeViews();
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

  private void initializeViews() {
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
        guillotineMenu,
        guillotineMenu.findViewById(R.id.hamburger_guillotine_menu),
        hamburgerVerticalImageViewButton)
        .setStartDelay(rippleDuration)
        .setActionBarViewForAnimation(toolbar)
        .setGuillotineListener(guillotineListener)
        .setClosedOnStart(true)
        .build();

    setUpGuillotineMenuItems();
  }

  private void setUpGuillotineMenuItems() {
    // Declare arraylists containing names and icon resources of guillotine menu items
    ArrayList<Pair<Integer, Integer>> menuItemDetails = new ArrayList<>();
    menuItemDetails.add(
        Pair.create(R.string.guillotine_explore_title, R.drawable.ic_explore_white));
    menuItemDetails.add(
        Pair.create(R.string.guillotine_toppicks_title, R.drawable.ic_toppicks_white));
    menuItemDetails.add(
        Pair.create(R.string.guillotine_categories_title, R.drawable.ic_categories_white));
    menuItemDetails.add(
        Pair.create(R.string.guillotine_minimal_title, R.drawable.ic_minimal_white));
    menuItemDetails.add(
        Pair.create(R.string.guillotine_collection_title, R.drawable.ic_collections_white));
    menuItemDetails.add(
        Pair.create(R.string.guillotine_feedback_title, R.drawable.ic_feedback_white));
    menuItemDetails.add(
        Pair.create(R.string.guillotine_buypro_title, R.drawable.ic_buypro_black));

    // Programmatically add guillotine menu items
    LayoutInflater layoutInflater = LayoutInflater.from(this);
    for (int i = 0; i < 7; i++) {
      View guillotineMenuItem = layoutInflater
          .inflate(R.layout.item_guillotine_menu, null);
      guillotineMenuRootLinearLayout.addView(guillotineMenuItem);
      guillotineMenuItem.setId(menuItemDetails.get(i).first);
      ImageView guillotineMenuItemImage =
          guillotineMenuItem.findViewById(R.id.imageview_guillotine_menu_item);
      WallrCustomTextView guillotineMenuItemText =
          guillotineMenuItem.findViewById(R.id.textview_guillotine_menu_item);
      guillotineMenuItemText.setText(getString(menuItemDetails.get(i).first));
      guillotineMenuItemImage.setImageDrawable(
          getResources().getDrawable(menuItemDetails.get(i).second));
      // Make the background white and text color black for the buy pro guillotine menu item
      if (i == 6) {
        guillotineMenuItem.setBackgroundColor(getResources().getColor(R.color.color_white));
        guillotineMenuItemText.setTextColor(getResources().getColor(R.color.color_black));
      }
    }
  }
}