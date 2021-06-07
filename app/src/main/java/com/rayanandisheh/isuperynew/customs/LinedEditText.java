package com.rayanandisheh.isuperynew.customs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.rayanandisheh.isuperynew.R;

public class LinedEditText extends androidx.appcompat.widget.AppCompatEditText {
    private Rect mRect;
    private Paint mPaint =  new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
    private int mW;

    public LinedEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        create();
    }

    public LinedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        create();
    }

    public LinedEditText(Context context) {
        super(context);
        create();

    }

    @SuppressLint("ResourceAsColor")
    private void create() {

        mPaint.setColor(R.color.notebookLineColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mW = w;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int count = getLineCount();
        Paint paint = mPaint;
        Rect r = new Rect();

        for (int i = 0; i < count; i++) {
            int baseline = getLineBounds(i, r);

            canvas.drawLine(0, baseline + 1, mW, baseline + 1, paint);
        }

        super.onDraw(canvas);
    }



}