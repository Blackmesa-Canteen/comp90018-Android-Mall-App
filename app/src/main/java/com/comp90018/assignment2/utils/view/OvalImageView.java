package com.comp90018.assignment2.utils.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author xiaotian
 * this is a imageview with oval edge
 */
public class OvalImageView extends androidx.appcompat.widget.AppCompatImageView {
    /* The radius of the rounded corner is the top left xy radius, top right, bottom right, bottom left */
    private float[] rids = {30.0f, 30.0f, 30.0f, 30.0f, 30.0f, 30.0f, 30.0f, 30.0f,};


    public OvalImageView(Context context) {
        super(context);
    }


    public OvalImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public OvalImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        int w = this.getWidth();
        int h = this.getHeight();
        /* Adds a rounded rectangle to the path. The Radii array defines the x,y
        * radii of the four rounded corners of a rounded rectangle. The length of radii must be 8
         */
        path.addRoundRect(new RectF(0, 0, w, h), rids, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}
