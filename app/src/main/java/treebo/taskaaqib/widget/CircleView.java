package treebo.taskaaqib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import treebo.taskaaqib.util.AppUtil;

public class CircleView extends View {

    private int mSizeHalf;
    private Paint bgColor, outlineColor;

    public CircleView(Context context) {
        super(context);
        init();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        bgColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgColor.setStyle(Paint.Style.FILL);
        bgColor.setColor(Color.BLUE);

        outlineColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        outlineColor.setStyle(Paint.Style.STROKE);
        outlineColor.setColor(Color.WHITE);
        outlineColor.setStrokeWidth(AppUtil.dpToPx(getContext(), 2));
    }

    public void setColor(String color) {
        try {
            bgColor.setColor(Color.parseColor(color));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(mSizeHalf, mSizeHalf, mSizeHalf * 0.9f, bgColor);
        canvas.drawCircle(mSizeHalf, mSizeHalf, mSizeHalf * 0.9f, outlineColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 100;
        int desiredHeight = 100;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int w;
        int h;

        if (widthMode == MeasureSpec.EXACTLY) {
            w = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            w = Math.min(desiredWidth, widthSize);
        } else {
            w = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            h = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            h = Math.min(desiredHeight, heightSize);
        } else {
            h = desiredHeight;
        }

        int size = Math.max(w, h);
        mSizeHalf = size / 2;

        setMeasuredDimension(size, size);
    }

}
