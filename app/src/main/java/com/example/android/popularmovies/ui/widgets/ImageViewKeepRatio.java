/*
 * Copyright 2015 Nicolas Pintos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.popularmovies.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.example.android.popularmovies.R;

public class ImageViewKeepRatio extends ImageView {

    private float mWidthHeightRatio;

    public ImageViewKeepRatio(Context context) {
        super(context);
    }

    public ImageViewKeepRatio(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.ImageViewKeepRatio, 0, 0);
        try {
            mWidthHeightRatio = a.getFloat(
                    R.styleable.ImageViewKeepRatio_width_height_ratio, -1);
        } finally {
            a.recycle();
        }
    }

    public ImageViewKeepRatio(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = 0;
        if (mWidthHeightRatio > -1) {
            // Calculate height via attr
            height = (int) (width / mWidthHeightRatio);
        } else if (getDrawable() != null) {
            // Calculate height via image drawable
            height = width * getDrawable().getIntrinsicHeight()
                    / getDrawable().getIntrinsicWidth();
        }

        setMeasuredDimension(width, height);
    }
}