/*
 * Copyright (C) The Android Open Source Project
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
package takahirom.github.com.playqrcode.ui.camera;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.android.gms.vision.barcode.Barcode;

/**
 * Graphic instance for rendering barcode position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
public class BarcodeGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    private static final int COLOR_CHOICES[] = {
        Color.BLUE,
        Color.CYAN,
        Color.GREEN,
        Color.MAGENTA,
        Color.RED,
        Color.WHITE,
        Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;

    private Paint mBarcodePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    private volatile Barcode mBarcode;
    private int mBarcodeId;
    private String mBarcodeInfo;

    BarcodeGraphic(GraphicOverlay overlay) {
        super(overlay);

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mBarcodePositionPaint = new Paint();
        mBarcodePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    }

    void setId(int id) {
        mBarcodeId = id;
    }

    void setInfo(String info) {
        mBarcodeInfo = info;
    }

    /**
     * Updates the barcode instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateBarcode(Barcode barcode) {
        mBarcode = barcode;
        postInvalidate();
    }

    /**
     * Draws the barcode annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Barcode barcode = mBarcode;
        if (barcode == null) {
            return;
        }

        // Draws a circle at the position of the detected barcode, with the barcode's track id below.
        float x = translateX(barcode.getBoundingBox().left + barcode.getBoundingBox().width() / 2);
        float y = translateY(barcode.getBoundingBox().top + barcode.getBoundingBox().height() / 2);
        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mBarcodePositionPaint);
        canvas.drawText("id: " + mBarcodeId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);
        canvas.drawText("info: " + mBarcodeInfo, x + ID_X_OFFSET, y + ID_Y_OFFSET + ID_TEXT_SIZE, mIdPaint);

        // Draws a bounding box around the barcode.
        float xOffset = scaleX(barcode.getBoundingBox().width() / 2.0f);
        float yOffset = scaleY(barcode.getBoundingBox().height() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        canvas.drawRect(left, top, right, bottom, mBoxPaint);
    }
}
