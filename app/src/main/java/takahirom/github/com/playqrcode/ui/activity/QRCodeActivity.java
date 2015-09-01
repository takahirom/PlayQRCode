package takahirom.github.com.playqrcode.ui.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import takahirom.github.com.playqrcode.R;
import takahirom.github.com.playqrcode.ui.camera.CameraFacade;
import takahirom.github.com.playqrcode.ui.camera.CameraSourcePreview;
import takahirom.github.com.playqrcode.ui.camera.GraphicOverlay;

public class QRCodeActivity extends AppCompatActivity {

    private CameraFacade cameraFacade;


    //==============================================================================================
    // Activity Methods
    //==============================================================================================

    /**
     * Initializes the UI and initiates the creation of a barcode detector.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_qrcode);

        Context context = getApplicationContext();
        cameraFacade = new CameraFacade(context ,(CameraSourcePreview) findViewById(R.id.preview),(GraphicOverlay) findViewById(R.id.barcodeOverlay));
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        cameraFacade.startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        cameraFacade.stopPreview();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraFacade.releaseCameraSource();
    }

}
