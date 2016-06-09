package treebo.taskaaqib.util;

import android.content.Context;
import android.util.TypedValue;

public class AppUtil {

    public static float dpToPx(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
