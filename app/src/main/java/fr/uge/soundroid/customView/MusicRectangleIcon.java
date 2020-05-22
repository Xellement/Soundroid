package fr.uge.soundroid.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class MusicRectangleIcon  extends View {

    public MusicRectangleIcon(Context context) {
        super(context);
    }

    public MusicRectangleIcon(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MusicRectangleIcon(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected int computeDimension(int spec) {
        if (MeasureSpec.getMode(spec) == MeasureSpec.EXACTLY)
            return MeasureSpec.getSize(spec);
        else
            return (int)(MeasureSpec.getSize(spec) * 1.0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(computeDimension(700), computeDimension(700));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint p = new Paint();
        p.setColor(Color.rgb(45, 45, 107));
        Rect rect = new Rect(0, 0, this.getWidth(), this.getHeight());
        RectF rectF = new RectF(rect);
        canvas.drawRoundRect(rectF, 50, 50, p);

        p.setColor(Color.WHITE);
        canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2, (float) (this.getHeight() * 0.8) / 2, p);

        p.setColor(Color.rgb(45, 45, 107));
        canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2, (float) (this.getHeight() * 0.1) / 2, p);

        invalidate();
    }
}
