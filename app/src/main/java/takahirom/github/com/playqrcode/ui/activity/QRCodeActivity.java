package takahirom.github.com.playqrcode.ui.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.vision.barcode.Barcode;

import takahirom.github.com.playqrcode.R;
import takahirom.github.com.playqrcode.ui.camera.CameraFacade;
import takahirom.github.com.playqrcode.ui.camera.CameraSourcePreview;
import takahirom.github.com.playqrcode.ui.camera.GraphicOverlay;
import takahirom.github.com.playqrcode.viewmodel.QRCodePresenter;

public class QRCodeActivity extends AppCompatActivity {

    private QRCodePresenter qrCodePresenter;


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
        CameraFacade cameraFacade = new CameraFacade(context ,(CameraSourcePreview) findViewById(R.id.preview),(GraphicOverlay) findViewById(R.id.barcodeOverlay));
        qrCodePresenter = new QRCodePresenter(this, cameraFacade);
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        qrCodePresenter.onResume();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        qrCodePresenter.onPause();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        qrCodePresenter.onDestroy();
    }


    public void startQRDetailActivity(Barcode barcode) {
        startActivity(QRDetailActivity.getQRDetailIntent(this, barcode));
    }
}
