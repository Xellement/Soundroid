package fr.uge.soundroid.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class PlaylistRectangleIcon extends View {

    public PlaylistRectangleIcon(Context context) {
        super(context);
    }

    public PlaylistRectangleIcon(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PlaylistRectangleIcon(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected int computeDimension(int spec) {
        if (MeasureSpec.getMode(spec) == MeasureSpec.EXACTLY)
            return MeasureSpec.getSize(spec); // we have no choice
        else
            return MeasureSpec.getSize(spec) * 1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(computeDimension(widthMeasureSpec), computeDimension(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint p = new Paint();
        p.setColor(Color.rgb(255, 87, 102));
        Rect rect = new Rect(0, 0, this.getWidth(), this.getHeight());
        RectF rectF = new RectF(rect);
        canvas.drawRoundRect(rectF, 50, 50, p);

        p.setColor(Color.WHITE);
        canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2, 130, p);

        p.setColor(Color.rgb(255, 87, 102));
        canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2, 20, p);

        invalidate();
    }
}
