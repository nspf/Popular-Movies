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
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.popularmovies.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EmptyStateView extends LinearLayout {

    @Bind(R.id.widget_empty_state_view_text) TextView mMessageText;
    @Bind(R.id.widget_empty_state_view_description) TextView mMessageDescription;
    @Bind(R.id.widget_empty_state_view_icon) ImageView mMessageIcon;


    private EmptyStateView(Context context) {
        this(context, null, 0);
    }

    public EmptyStateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private EmptyStateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        View mView = LayoutInflater.from(context).inflate(R.layout.widget_empty_state_view, this, true);
        ButterKnife.bind(this, mView);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EmptyStateView, defStyle, 0);

        String text = a.getString(R.styleable.EmptyStateView_messageText);
        String description = a.getString(R.styleable.EmptyStateView_messageDescription);
        Drawable image = a.getDrawable(R.styleable.EmptyStateView_messageIcon);

        mMessageText.setText(text);
        mMessageDescription.setText(description);
        mMessageIcon.setImageDrawable(image);

        a.recycle();
    }

    public void setMessageText(CharSequence text) {
        mMessageText.setText(text);
    }

    public void setMessageDescription(CharSequence text) {
        mMessageDescription.setText(text);
    }

    public void setMessageIcon(Drawable drawable) {
        mMessageIcon.setImageDrawable(drawable);
    }

}