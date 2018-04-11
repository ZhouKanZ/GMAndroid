package com.jms.cleanse.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zhoukan on 2018/4/4.
 *
 * @desc:
 */

public class MatrixView extends View {

    Rect rect = new Rect();
    Paint mPaint;
    Matrix matrix = new Matrix();

    public MatrixView(Context context) {
        this(context, null);
    }

    public MatrixView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLUE);

    }

    public MatrixView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        rect.left = 0;
        rect.top = 0;
        rect.right = 100;
        rect.bottom = 100;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
//        matrix.reset();
        // 放大5倍没问题
//        matrix.preScale(5,5);
        matrix.preScale(5,5);
        matrix.preTranslate(50,50);

        Matrix m = new Matrix();
        m.preRotate(30);
        matrix.preConcat(m);
        canvas.concat(matrix);
        canvas.drawRect(rect,mPaint);
        canvas.restore();
    }
}
