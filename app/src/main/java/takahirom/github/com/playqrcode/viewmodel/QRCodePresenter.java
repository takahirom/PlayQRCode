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
    public void onNewQRCodeListener(Barcode barcode) {
        switch (barcode.valueFormat) {
            case Barcode.CONTACT_INFO:
            case Barcode.EMAIL:
            case Barcode.ISBN:
            case Barcode.PHONE:
            case Barcode.PRODUCT:
            case Barcode.SMS:
            case Barcode.TEXT:
            case Barcode.URL:
            case Barcode.WIFI:
            case Barcode.GEO:
            case Barcode.CALENDAR_EVENT:
            case Barcode.DRIVER_LICENSE:
        }
        activity.startQRDetailActivity(barcode);
    }
}
