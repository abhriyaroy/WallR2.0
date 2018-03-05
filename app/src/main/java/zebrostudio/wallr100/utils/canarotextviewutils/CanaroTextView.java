package zebrostudio.wallr100.utils.canarotextviewutils;

import android.content.Context;
import android.util.AttributeSet;

public class CanaroTextView extends android.support.v7.widget.AppCompatTextView {

    public CanaroTextView(Context context) {
        this(context, null);
    }

    public CanaroTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanaroTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(AbstractApp.canaroExtraBold);
    }

}