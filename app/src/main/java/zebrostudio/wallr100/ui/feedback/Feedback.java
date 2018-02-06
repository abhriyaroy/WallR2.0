package zebrostudio.wallr100.ui.feedback;


import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import javax.inject.Inject;

import zebrostudio.wallr100.ui.main.MainActivity;
import zebrostudio.wallr100.utils.Toasty;

public class Feedback {

    private MainActivity mMainActivity;

    @Inject
    Feedback(MainActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    public void getFeedback() {
        Runnable giveFeedback = new Runnable() {
            @Override
            public void run() {
                String emailHeader = "Debug-infos:";
                emailHeader += "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
                emailHeader += "\n OS API Level: " + android.os.Build.VERSION.SDK_INT;
                emailHeader += "\n Device: " + android.os.Build.DEVICE;
                emailHeader += "\n Model (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")";
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                String emailID = "studio.zebro@gmail.com";
                emailIntent.putExtra(Intent.EXTRA_EMAIL, emailID);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback/Report about WallR" + "  " + emailHeader);
                try {
                    mMainActivity.startActivityForResult(Intent.createChooser(emailIntent, "Contact using"), 0);
                } catch (android.content.ActivityNotFoundException ex) {
                    try {
                        Toasty.error(mMainActivity, "There is no email client installed.", Toast.LENGTH_SHORT, true).show();
                    } catch (NullPointerException npe) {
                        Crashlytics.logException(npe);
                    }
                    Crashlytics.logException(ex);
                }
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(giveFeedback, 100);
    }
}
