package takahirom.github.com.playqrcode.ui.camera;

import android.content.Context;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

/**
 * Created by takahirom on 15/09/02.
 */
public class CameraFacade {

    private static final String TAG = "BarcodeTracker";
    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;


    public CameraFacade(final Context context, CameraSourcePreview preview, GraphicOverlay graphicOverlay) {
        this.mPreview = preview;
        mGraphicOverlay = graphicOverlay;

        preview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mPreview.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                BarcodeDetector detector = new BarcodeDetector.Builder(context).build();
                detector.setProcessor(
                        new MultiProcessor.Builder<>(new GraphicBarcodeTrackerFactory()).build());

                if (!detector.isOperational()) {
                    // Note: The first time that an app using barcode API is installed on a device, GMS will
                    // download a native library to the device in order to do detection.  Usually this
                    // completes before the app is run for the first time.  But if that download has not yet
                    // completed, then the above call will not detect any barcodes.
                    //
                    // isOperational() can be used to check if the required native library is currently
                    // available.  The detector will automatically become operational once the library
                    // download completes on device.
                    Log.w(TAG, "Barcode detector dependencies are not yet available.");
                }

                int height = (mPreview.getHeight() * 480) / mPreview.getWidth();
                Toast.makeText(context, "height:" + height, Toast.LENGTH_SHORT).show();
                mCameraSource = new CameraSource.Builder(context, detector)
                        .setRequestedPreviewSize(height, 480)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedFps(30.0f)
                        .build();
                startCameraSource();
            }
        });


    }

    //==============================================================================================
    // Camera Source Preview
    //==============================================================================================

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    public void startCameraSource() {
        try {
            mPreview.start(mCameraSource, mGraphicOverlay);
        } catch (IOException e) {
            Log.e(TAG, "Unable to start camera source.", e);
            mCameraSource.release();
            mCameraSource = null;
        }
    }

    public void stopPreview() {
        mPreview.stop();
    }

    public void releaseCameraSource() {
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    //==============================================================================================
    // Graphic Barcode Tracker
    //==============================================================================================

    /**
     * Factory for creating a barcode tracker to be associated with a new barcode.  The multiprocessor
     * uses this factory to create barcode trackers as needed -- one for each individual.
     */
    private class GraphicBarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {
        @Override
        public Tracker<Barcode> create(Barcode barcode) {
            return new GraphicBarcodeTracker(mGraphicOverlay);
        }
    }

    /**
     * Barcode tracker for each detected individual. This maintains a barcode graphic within the app's
     * associated barcode overlay.
     */
    private class GraphicBarcodeTracker extends Tracker<Barcode> {
        private GraphicOverlay mOverlay;
        private BarcodeGraphic mBarcodeGraphic;

        GraphicBarcodeTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mBarcodeGraphic = new BarcodeGraphic(overlay);
        }

        /**
         * Start tracking the detected barcode instance within the barcode overlay.
         */
        @Override
        public void onNewItem(int barcodeId, Barcode item) {
            mBarcodeGraphic.setId(barcodeId);
            mBarcodeGraphic.setInfo(item.rawValue);
            if (mOnNewQRCodeListener != null) {
                mOnNewQRCodeListener.onNewQRCodeListener(item);
            }
        }

        /**
         * Update the position/characteristics of the barcode within the overlay.
         */
        @Override
        public void onUpdate(BarcodeDetector.Detections<Barcode> detectionResults, Barcode barcode) {
            mOverlay.add(mBarcodeGraphic);
            mBarcodeGraphic.updateBarcode(barcode);
        }

        /**
         * Hide the graphic when the corresponding barcode was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the barcode was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(BarcodeDetector.Detections<Barcode> detectionResults) {
            mOverlay.remove(mBarcodeGraphic);
        }

        /**
         * Called when the barcode is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mOverlay.remove(mBarcodeGraphic);
        }
    }

    public interface OnNewQRCodeListener {
        void onNewQRCodeListener(Barcode item);
    }

    private OnNewQRCodeListener mOnNewQRCodeListener;

    public void setOnNewQRCodeListener(OnNewQRCodeListener onNewQRCodeListener) {
        this.mOnNewQRCodeListener = onNewQRCodeListener;
    }
}
