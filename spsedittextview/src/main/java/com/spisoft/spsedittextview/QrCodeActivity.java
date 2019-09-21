package com.spisoft.spsedittextview;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

import static com.spisoft.spsedittextview.GlobalCls.buildCounterRecDrawable;
import static com.spisoft.spsedittextview.SpsEditText.TF_Holo;


public class QrCodeActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, QRCodeView.Delegate {

    private static final int MY_PERMISSION_REQUEST_CAMERA = 0;
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;

    private ViewGroup mainLayout;
    private MenuItem flashSwitchItem, cameraSwitchItem;
    private boolean FlashIsChecked = false;
    private int CurrentCamera = Camera.CameraInfo.CAMERA_FACING_BACK;
    private TextView TxtName, TxtPrice;
    private LinearLayout LlyShow;
    private ZXingView mZBarView;
    private String _Status_Code = null;

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putCharSequence(EXTRA_STATUS_BARCODE_ACTIVITY, _Status_Code);
    }

    @Override
    public void onBackPressed() {
        if(LlyShow.getVisibility() == View.GONE){
            super.onBackPressed();
        }
        else
        {
            LlyShow.setVisibility(View.GONE);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decoder);

//        if (savedInstanceState == null) {
//            Intent mIntent = getIntent();
//            if(mIntent == null) {
//                _Status_Code = null;
//            } else {
//                _Status_Code = mIntent.getStringExtra(EXTRA_STATUS_BARCODE_ACTIVITY);
//            }
//        } else {
//            _Status_Code = savedInstanceState.getString(EXTRA_STATUS_BARCODE_ACTIVITY,"");
//        }
//
//
//        if(_Status_Code != null){
//            if(_Status_Code.equals(BARCPDE_READER_PLACE))
//                setTitle("جانمایی کالا");
//            if(_Status_Code.equals(BARCPDE_READER_BARCODE))
//                setTitle("بارکد کالا");
//        }

        mainLayout = (ViewGroup) findViewById(R.id.main_layout);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            initQRCodeReaderView();
        } else {
            requestCameraPermission();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scanner, menu);

        flashSwitchItem = menu.findItem(R.id.action_flash);
        flashSwitchItem.setIcon(buildCounterRecDrawable(QrCodeActivity.this, 0, R.string.ic_holo_flash_on, Color.YELLOW,0, TF_Holo));

        cameraSwitchItem = menu.findItem(R.id.action_switch_camera);
        cameraSwitchItem.setIcon(buildCounterRecDrawable(QrCodeActivity.this, 0, R.string.ic_holo_switch_camera, Color.YELLOW,0, TF_Holo));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_flash) {
            FlashIsChecked = !FlashIsChecked;
            if(FlashIsChecked) {
                mZBarView.openFlashlight();
                flashSwitchItem.setIcon(buildCounterRecDrawable(QrCodeActivity.this, 0, R.string.ic_holo_flash_off, Color.YELLOW, 0, TF_Holo));
            }else {
                mZBarView.closeFlashlight();
                flashSwitchItem.setIcon(buildCounterRecDrawable(QrCodeActivity.this, 0, R.string.ic_holo_flash_on, Color.YELLOW, 0, TF_Holo));
            }
        }

        if (id == R.id.action_switch_camera) {
            mZBarView.stopSpot();
            mZBarView.stopCamera();
            if(CurrentCamera == Camera.CameraInfo.CAMERA_FACING_BACK)
                CurrentCamera = Camera.CameraInfo.CAMERA_FACING_FRONT;
            else
                CurrentCamera = Camera.CameraInfo.CAMERA_FACING_BACK;

            mZBarView.startCamera(CurrentCamera);
            mZBarView.startSpotAndShowRect();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != MY_PERMISSION_REQUEST_CAMERA) {
            return;
        }

        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(mainLayout, "Camera permission was granted.", Snackbar.LENGTH_SHORT).show();
            initQRCodeReaderView();
        } else {
            Snackbar.make(mainLayout, "Camera permission request was denied.", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Snackbar.make(mainLayout, "Camera access is required to display the camera preview.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(QrCodeActivity.this, new String[] {
                            Manifest.permission.CAMERA
                    }, MY_PERMISSION_REQUEST_CAMERA);
                }
            }).show();
        } else {
            Snackbar.make(mainLayout, "Permission is not available. Requesting camera permission.",
                    Snackbar.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.CAMERA
            }, MY_PERMISSION_REQUEST_CAMERA);
        }
    }

    private void initQRCodeReaderView() {
        View content = getLayoutInflater().inflate(R.layout.content_decoder, mainLayout, true);

        mZBarView = findViewById(R.id.zbarview);
        mZBarView.setDelegate(this);

        LlyShow = content.findViewById(R.id.llyShow);
        TxtName = (TextView) content.findViewById(R.id.txt_name);
        TxtPrice = (TextView) content.findViewById(R.id.txt_price);

//        TxtName.setTypeface(TF_BMitra);
//        TxtPrice.setTypeface(TF_Tahoma);
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    protected void onStop() {
        mZBarView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZBarView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(_Status_Code != null) {
            mZBarView.getScanBoxView().setShowDefaultGridScanLineDrawable(false);
            mZBarView.getScanBoxView().setCustomScanLineDrawable(getResources().getDrawable(R.drawable.custom_scan_line));
//            mZBarView.getScanBoxView().setBarcodeRectHeight(R.dimen.sps_lpr_sz_100);
            mZBarView.getScanBoxView().setAutoZoom(true);
//            mZBarView.getScanBoxView().setRectWidth(R.dimen.sps_lpr_sz_100);
            mZBarView.getScanBoxView().setAnimTime(100);
//            mZBarView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }
            mZBarView.startCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        mZBarView.startSpotAndShowRect();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        setTitle(result);
        vibrate();
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", result);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();

        mZBarView.startSpot();
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        String tipText = mZBarView.getScanBoxView().getTipText();
        String ambientBrightnessTip = "\nمحیط تاریک است، لطفا فلش را روشن کنید";
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                mZBarView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
                mZBarView.getScanBoxView().setTipText(tipText);
            }
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mZBarView.showScanRect();

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY) {
            final String picturePath = BGAPhotoPickerActivity.getSelectedPhotos(data).get(0);
            mZBarView.decodeQRCode(picturePath);

        }
    }


}