/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.android.colorpicker;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Creates a circular swatch of a specified color.  Adds a checkmark if marked as checked.
 */
public class ColorPickerSwatch extends FrameLayout implements View.OnClickListener {
    private int mColor;
    private boolean mChecked;
    private ImageView mSwatchImage;
    private ImageView mCheckmarkImage;
    private OnColorSelectedListener mOnColorSelectedListener;

    /**
     * Interface for a callback when a color square is selected.
     */
    public interface OnColorSelectedListener {

        /**
         * Called when a specific color square has been selected.
         */
        void onColorSelected(int color);
    }

    public ColorPickerSwatch(Context context) {
        super(context);
        init();
    }

    public ColorPickerSwatch(Context context, AttributeSet attrs) {
        super(context, attrs);
        processAttributes(attrs, 0);
        init();
    }

    public ColorPickerSwatch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        processAttributes(attrs, defStyle);
        init();
    }

    public ColorPickerSwatch(Context context, int color, boolean checked, OnColorSelectedListener listener) {
        super(context);
        mColor = color;
        mChecked = checked;
        mOnColorSelectedListener = listener;
        init();
    }

    private void processAttributes(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
                attrs, R.styleable.ColorPickerSwatch, defStyle, defStyle);

        try {
            mColor = typedArray.getColor(R.styleable.ColorPickerSwatch_color, mColor);
            mChecked = typedArray.getBoolean(R.styleable.ColorPickerSwatch_checked, false);
        } finally {
            typedArray.recycle();
        }
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.color_picker_swatch, this);
        mSwatchImage = findViewById(R.id.color_picker_swatch);
        mCheckmarkImage = findViewById(R.id.color_picker_checkmark);
        setColor(mColor);
        setChecked(mChecked);
        setOnClickListener(this);
    }

    public void setColor(int color) {
        Resources res = getContext().getResources();

        GradientDrawable swatch = (GradientDrawable) res.getDrawable(R.drawable.color_picker_swatch);

        // Set stroke to dark version of color
        int darkenedColor = Color.rgb(
                Color.red(color) * 192 / 256,
                Color.green(color) * 192 / 256,
                Color.blue(color) * 192 / 256);

        swatch.setColor(color);
        swatch.setStroke((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, res.getDisplayMetrics()), darkenedColor);
        mSwatchImage.setImageDrawable(swatch);
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
        if (mChecked) {
            mCheckmarkImage.setVisibility(View.VISIBLE);
        } else {
            mCheckmarkImage.setVisibility(View.GONE);
        }
    }

    public int getColor() {
        return mColor;
    }

    public boolean getChecked() {
        return mCheckmarkImage.getVisibility() == View.VISIBLE;
    }
    
    @Override
    public void onClick(View v) {
        if (mOnColorSelectedListener != null) {
            mOnColorSelectedListener.onColorSelected(mColor);
        }
    }
}
