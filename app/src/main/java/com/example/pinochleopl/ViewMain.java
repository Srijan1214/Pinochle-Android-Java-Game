package com.example.pinochleopl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class ViewMain extends View implements View.OnClickListener {

    private Bitmap mImage;
    private static final int IMAGE_WIDTH = 200;
    private static final int IMAGE_HEIGHT = (int)(1056/691 * IMAGE_WIDTH);

    public ViewMain(Context context) {
        super(context);
        init(null);
    }

    public ViewMain(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ViewMain(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ViewMain(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {
//        mImage = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.two_c), IMAGE_WIDTH, IMAGE_HEIGHT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Rect rect = new Rect(10,50,400,400);
        Paint paint = new Paint(Color.RED);
//        canvas.drawRect(rect, paint);

        canvas.drawBitmap(mImage, 0,500,null);
    }

    private Bitmap getResizedBitmap(Bitmap bitmap, int width, int height) {
        Matrix matrix = new Matrix();

        RectF src = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF dst = new RectF(0, 0, width, height);

        matrix.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);

        return  Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    public void onClick(View v) {
        System.out.println(v.getId());
    }
}
