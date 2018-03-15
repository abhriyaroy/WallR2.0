package zebrostudio.wallr100;

import android.app.Application;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;

public class WallrApplication extends Application {

  private static final String CANARO_EXTRA_BOLD_PATH = "font/canaro_extra_bold.otf";
  public static Typeface canaroExtraBold;

  @Override
  public void onCreate() {
    super.onCreate();
    initTypeface();
  }

  private void initTypeface() {
    canaroExtraBold = ResourcesCompat.getFont(this, R.font.canaro_extra_bold);
  }
}
