package com.spisoft.spsedittextview;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

import static android.view.View.GONE;
import static com.spisoft.spsedittextview.GlobalCls.buildCounterRecDrawable;
import static com.spisoft.spsedittextview.SpsEditText.ResultQrCode;
import static com.spisoft.spsedittextview.SpsEditText.TF_Holo;


public class QrCodeActivity extends AppCompatActivity implements QRCodeView.Delegate {

    private ViewGroup mainLayout;
    private MenuItem flashSwitchItem, cameraSwitchItem;
    private boolean FlashIsChecked = false;
    private int CurrentCamera = Camera.CameraInfo.CAMERA_FACING_BACK;
    private LinearLayout LlyShow;
    private ZXingView mZBarView;

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
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

        mainLayout = (ViewGroup) findViewById(R.id.main_layout);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            initQRCodeReaderView();
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
            if(CurrentCamera == Camera.CameraInfo.CAMERA_FACING_BACK) {
                CurrentCamera = Camera.CameraInfo.CAMERA_FACING_FRONT;
                flashSwitchItem.setIcon(buildCounterRecDrawable(QrCodeActivity.this, 0, R.string.ic_holo_flash_on, Color.YELLOW, 0, TF_Holo));
                FlashIsChecked = false;
                flashSwitchItem.setVisible(false);
            }else {
                CurrentCamera = Camera.CameraInfo.CAMERA_FACING_BACK;
                flashSwitchItem.setVisible(true);
            }

            mZBarView.startCamera(CurrentCamera);
            mZBarView.startSpotAndShowRect();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initQRCodeReaderView() {
        View content = getLayoutInflater().inflate(R.layout.content_decoder, mainLayout, true);

        mZBarView = findViewById(R.id.zbarview);
        mZBarView.setDelegate(this);

        LlyShow = content.findViewById(R.id.llyShow);
    }

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
        mZBarView.getScanBoxView().setShowDefaultGridScanLineDrawable(false);
        mZBarView.getScanBoxView().setCustomScanLineDrawable(getResources().getDrawable(R.drawable.custom_scan_line));
        mZBarView.getScanBoxView().setAutoZoom(false);
        mZBarView.getScanBoxView().setAnimTime(100);
        mZBarView.startCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        mZBarView.startSpotAndShowRect();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        setTitle(result);
        vibrate();
        Intent returnIntent = new Intent();
        returnIntent.putExtra(ResultQrCode, result);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

        mZBarView.startSpot();
    }


    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        String tipText = mZBarView.getScanBoxView().getTipText();
        String ambientBrightnessTip = "\n Please on flashlight";
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

}