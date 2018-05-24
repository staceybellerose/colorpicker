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
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.android.colorpicker.ColorPickerSwatch.OnColorSelectedListener;

/**
 * A color picker custom view which creates an grid of color squares.  The number of squares per
 * row (and the padding between the squares) is determined by the user.
 */
public class ColorPickerPalette extends TableLayout {

    public OnColorSelectedListener mOnColorSelectedListener;

    private String mDescription;
    private String mDescriptionSelected;

    private int mSwatchLength;
    private int mMarginSize;
    private int mNumColumns;

    public ColorPickerPalette(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorPickerPalette(Context context) {
        super(context);
    }

    /**
     * Initialize the size, columns, and listener.  Size should be a pre-defined size (SIZE_LARGE
     * or SIZE_SMALL) from ColorPickerDialogFragment.
     */
    public void init(int size, int columns, OnColorSelectedListener listener) {
        mNumColumns = columns;
        Resources res = getResources();
        if (size == ColorPickerDialog.SIZE_LARGE) {
            mSwatchLength = res.getDimensionPixelSize(R.dimen.color_swatch_large);
            mMarginSize = res.getDimensionPixelSize(R.dimen.color_swatch_margins_large);
        } else {
            mSwatchLength = res.getDimensionPixelSize(R.dimen.color_swatch_small);
            mMarginSize = res.getDimensionPixelSize(R.dimen.color_swatch_margins_small);
        }
        mOnColorSelectedListener = listener;

        mDescription = res.getString(R.string.color_swatch_description);
        mDescriptionSelected = res.getString(R.string.color_swatch_description_selected);
    }

    private TableRow createTableRow() {
        TableRow row = new TableRow(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(params);
        return row;
    }

    /**
     * Adds swatches to table.
     */
    public void drawPalette(int[] colors, int selectedColor) {
        drawPalette(colors, selectedColor, null);
    }

    /**
     * Adds swatches to table.
     */
    public void drawPalette(int[] colors, int selectedColor, String[] colorContentDescriptions) {
        if (colors == null) {
            return;
        }

        this.removeAllViews();
        int tableElements = 0;
        int rowElements = 0;

        // Fills the table with swatches based on the array of colors.
        TableRow row = createTableRow();
        for (int color : colors) {
            View colorSwatch = createColorSwatch(color, selectedColor);
            setSwatchDescription(tableElements, color == selectedColor, colorSwatch, colorContentDescriptions);
            row.addView(colorSwatch);

            tableElements++;
            rowElements++;
            if (rowElements == mNumColumns) {
                addView(row);
                row = createTableRow();
                rowElements = 0;
            }
        }

        // Create blank views to fill the row if the last row has not been filled.
        if (rowElements > 0) {
            while (rowElements != mNumColumns) {
                row.addView(createBlankSpace());
                rowElements++;
            }
            addView(row);
        }
    }

    /**
     * Add a content description to the specified swatch view.
     */
    private void setSwatchDescription(int index, boolean selected, View swatch, String[] contentDescriptions) {
        String description;
        if (contentDescriptions != null && contentDescriptions.length > index) {
            description = contentDescriptions[index];
        } else {
            int accessibilityIndex;
            accessibilityIndex = index + 1;

            if (selected) {
                description = String.format(mDescriptionSelected, accessibilityIndex);
            } else {
                description = String.format(mDescription, accessibilityIndex);
            }
        }
        swatch.setContentDescription(description);
    }

    /**
     * Creates a blank space to fill the row.
     */
    private ImageView createBlankSpace() {
        ImageView view = new ImageView(getContext());
        TableRow.LayoutParams params = new TableRow.LayoutParams(mSwatchLength, mSwatchLength);
        params.setMargins(mMarginSize, mMarginSize, mMarginSize, mMarginSize);
        view.setLayoutParams(params);
        return view;
    }

    /**
     * Creates a color swatch.
     */
    private ColorPickerSwatch createColorSwatch(int color, int selectedColor) {
        ColorPickerSwatch view = new ColorPickerSwatch(getContext(), color,
                color == selectedColor, mOnColorSelectedListener);
        TableRow.LayoutParams params = new TableRow.LayoutParams(mSwatchLength, mSwatchLength);
        params.setMargins(mMarginSize, mMarginSize, mMarginSize, mMarginSize);
        view.setLayoutParams(params);
        return view;
    }

    public void selectColor(int color)
    {
        for(int i=0; i< this.getChildCount(); ++i)
        {
            View firstChild = (this).getChildAt(i);
            if (!(firstChild instanceof TableRow))
            {
                continue;
            }

            for(int j=0; j <((TableRow)firstChild).getChildCount(); j++)
            {
                View secondChild = ((TableRow)firstChild).getChildAt(j);
                ColorPickerSwatch swatch = (ColorPickerSwatch) secondChild;
                swatch.setChecked(swatch.getColor() == color);
            }
        }
    }

    public Integer getSelectedColor()
    {
        Integer selected = null;

        for(int i=0; i< this.getChildCount(); ++i)
        {
            View firstChild = (this).getChildAt(i);
            if (!(firstChild instanceof TableRow))
            {
                continue;
            }

            for(int j=0; j <((TableRow)firstChild).getChildCount(); j++)
            {
                View secondChild = ((TableRow)firstChild).getChildAt(j);
                ColorPickerSwatch swatch = (ColorPickerSwatch) secondChild;
                if (swatch.getChecked() == true)
                {
                    selected = swatch.getColor();
                    break;
                }
            }

            if (selected != null)
            {
                break;
            }
        }
        return selected;
    }
}
