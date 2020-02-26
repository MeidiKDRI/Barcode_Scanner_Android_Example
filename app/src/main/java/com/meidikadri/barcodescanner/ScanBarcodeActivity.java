package com.meidikadri.barcodescanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

// Pour les imports suivants,
// Pensez à l'implémentation de la dépendance ci-dessous dans gradle
// implementation 'com.google.android.gms:play-services-vision:11.0.2'
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class ScanBarcodeActivity extends AppCompatActivity {

    TextView tips, tips2;
    EditText barcodeEAN;
    SurfaceView camPreview;
    ImageButton ok;
    final String EXTRA_EAN = "saisie";

    private static final int PERMISSION_REQUEST_CAMERA = 0;

    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner);

        tips = findViewById(R.id.tips);

        camPreview = findViewById(R.id.camera);
        createCameraSource();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                Intent refresh = getIntent();
                startActivity(refresh);
            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }

    private void createCameraSource() {

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).build();
        final CameraSource cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1300, 800)
                .build();

        camPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(ScanBarcodeActivity.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {

                    ActivityCompat.requestPermissions(ScanBarcodeActivity.this, new String[]
                            {Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);

                }else {
                    try {
                        cameraSource.start(camPreview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() > 0) {
                    Intent iScanned = new Intent();
                    iScanned.putExtra("barcodes", barcodes.valueAt(0));
                    setResult(CommonStatusCodes.SUCCESS, iScanned);
                    finish();
                    //int modeSaisie = SCANNED;
                    //onInputComplete(modeSaisie);
                }
            }
        });
    }
}


