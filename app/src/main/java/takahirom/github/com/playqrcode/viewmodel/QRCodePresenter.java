package takahirom.github.com.playqrcode.viewmodel;

import com.google.android.gms.vision.barcode.Barcode;

import takahirom.github.com.playqrcode.ui.activity.QRCodeActivity;
import takahirom.github.com.playqrcode.ui.camera.CameraFacade;

/**
 * Created by takahirom on 15/09/03.
 */
public class QRCodePresenter implements CameraFacade.OnNewQRCodeListener {
    private final QRCodeActivity activity;
    private CameraFacade cameraFacade;

    public QRCodePresenter(QRCodeActivity activity,CameraFacade cameraFacade) {
        this.activity = activity;
        this.cameraFacade = cameraFacade;
        cameraFacade.setOnNewQRCodeListener(this);
    }

    /**
     * Restarts the camera.
     */
    public void onResume() {
        cameraFacade.startCameraSource();
    }

    /**
     * Stops the camera.
     */
    public void onPause() {
        cameraFacade.stopPreview();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    public void onDestroy() {
        cameraFacade.releaseCameraSource();
    }


    @Override
    public void onNewQRCodeListener(Barcode item) {
        activity.startQRDetailActivity(item);
    }
}
