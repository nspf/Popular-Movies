package com.example.android.popularmovies.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AspectRatioImageView extends ImageView {

    public AspectRatioImageView(Context context) {
        super(context);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int aspectRatioHeight = MeasureSpec.getSize(widthMeasureSpec) * 2 / 3;
        int aspectRatioHeightSpec = MeasureSpec.makeMeasureSpec(aspectRatioHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, aspectRatioHeightSpec);
    }

}