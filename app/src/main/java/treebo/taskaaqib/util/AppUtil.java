package treebo.taskaaqib.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * Helper utility class
 */
public class AppUtil {

    /**
     * Convert dimensions in dip to pixels
     *
     * @param context Activity context
     * @param dp The value to convert(in dip)
     * @return The converted value in pixels
     */
    public static float dpToPx(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
