package com.spisoft.spsedittextview;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Dimension;
import androidx.annotation.RequiresApi;

import com.github.johnkil.print.PrintView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class SpsEditText extends RelativeLayout implements RecognitionListener {

//    public static String EXTRA_STATUS_BARCODE_ACTIVITY = "ExtraStatusBarcodeActivity";
//    public static String BARCPDE_READER_PLACE = "BarcodeReaderPlace";
//    public static String BARCPDE_READER_BARCODE = "BarcodeReaderBarcode";

    public static String RsultQrCode = "RsultQrCode";
    public static Typeface TF_Holo ;
    private InputMethodManager imm;
    private boolean SpeechStatus = false;
    private SpeechRecognizer speechRecognizer;

    private View rootView;
    private ProgressBar circleProgress;
    private static EditText MText;
    private TextView MCnt;
    private PrintView MBtn;
    private PrintView MBtnVoice;
    private ImageView MBtnQrCode;
    private Context mContext;
    private int REQ_CODE_QRCODE = 107;
    private boolean UseSpeechToText = true , UseBarcodeScanner = true;
    private RelativeLayout ViewBase;

    public SpsEditText(Context context) {
        super(context);
        init(context, null);
    }

    public SpsEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SpsEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SpsEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(final Context context, AttributeSet attrs){

        mContext = context;
        TF_Holo = Typeface.createFromAsset(context.getAssets(), "holo-icon-font.ttf" + "");

        rootView = inflate(context, R.layout.sps_layout, this);

        ViewBase = rootView.findViewById(R.id.viewBase);
        circleProgress = rootView.findViewById(R.id.cProgress);
        MText = rootView.findViewById(R.id.mText);
        MCnt = rootView.findViewById(R.id.mCnt);
        MBtn = rootView.findViewById(R.id.iSearch);
        MBtnVoice = rootView.findViewById(R.id.iVoice);
        MBtnQrCode = rootView.findViewById(R.id.iQr);

        imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);

        MBtn.setIconFont(TF_Holo);
        MBtnVoice.setIconFont(TF_Holo);

        MBtnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput(context);
            }
        });
//        MBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!MText.getText().toString().trim().equals("")) {
//                    ButtonPlusOnClick();
//                }
//            }
//        });
//        MBtn.setOnClickListener(ButtonPlusOnClick());
        MBtn.setVisibility(GONE);

        MText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    MBtn.callOnClick();
                }
                return false;
            }
        });

        MBtnQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                Intent intentQR = new Intent(context, QrCodeActivity.class);
                ((Activity) context).startActivityForResult(intentQR, REQ_CODE_QRCODE,ActivityOptions.makeCustomAnimation(context, R.anim.animate_zoom_enter, R.anim.animate_zoom_exit).toBundle());
            }
        });

        if (attrs != null) {
            final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpsEditText, 0, 0);

            MText.setHint(typedArray.getString(R.styleable.SpsEditText_TextHint));
//            ViewBase.setPadding(R.dimen.sps_lpr_5,R.dimen.sps_lpr_5,R.dimen.sps_lpr_5,R.dimen.sps_lpr_5);
            typedArray.recycle();
        }
    }

    public static void activityResult(String mCode){
        MText.setText(mCode);
    }

    private void promptSpeechInput(Context context) {
        imm.hideSoftInputFromWindow(MText.getWindowToken(), 0);
        if(SpeechStatus) {
            MBtnVoice.setIconColor(Color.GRAY);
            circleProgress.setVisibility(View.GONE);
            speechRecognizer.stopListening();
        }else {
            MBtnVoice.setIconColor(Color.RED);
            circleProgress.setVisibility(View.VISIBLE);
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
            speechRecognizer.setRecognitionListener(this);
            Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            speechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS ,true);
            speechRecognizer.startListening(speechIntent);
        }

    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {
        circleProgress.setProgress(Math.round(rmsdB)*8+20);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        MBtnVoice.setIconColor(Color.GRAY);
        SpeechStatus = false;
        circleProgress.setVisibility(View.GONE);
    }

    @Override
    public void onError(int error) {
        circleProgress.setVisibility(View.GONE);
        MBtnVoice.setIconColor(Color.GRAY);
    }

    @Override
    public void onResults(Bundle results) {
        MBtnVoice.setIconColor(Color.GRAY);
        SpeechStatus = false;
        circleProgress.setVisibility(View.GONE);
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        MText.setText(matches.get(0));
        MBtn.callOnClick();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        MText.setText(matches.get(0));
    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    //--------------------------------

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_HEADSETHOOK){
            promptSpeechInput(mContext);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    //-------------------------------- Function Attributes

    public void SetHint(String mHint){
        MText.setHint(mHint);
    }

    public void SetBorder(Drawable drawable, int padding){
        ViewBase.setBackground(drawable);
//        ViewBase.setPadding(padding,padding,padding,padding);
        RelativeLayout.LayoutParams mParams = new RelativeLayout.LayoutParams(
                new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        Resources r = mContext.getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                padding,
                r.getDisplayMetrics()
        );
        mParams.setMargins(px, px, px, px);
        ViewBase.setLayoutParams(mParams);
        ViewBase.requestLayout();
    }

    public View ButtonPlusView(Object mTag){
        MBtn.setTag(mTag);
        MBtn.setVisibility(VISIBLE);
        return this.getRootView().findViewWithTag(mTag);
    }

    public void SetUses(Activity activity, boolean useBarcodeScanner, boolean useSpeechToText){
        UseSpeechToText = useSpeechToText;
        UseBarcodeScanner = useBarcodeScanner;

        if(UseSpeechToText)
            MBtnVoice.setVisibility(VISIBLE);
        else
            MBtnVoice.setVisibility(GONE);

        if(UseBarcodeScanner)
            MBtnQrCode.setVisibility(VISIBLE);
        else
            MBtnQrCode.setVisibility(GONE);

        GetPermission(activity);
    }

    public void GetPermission(final Activity activity){
        String pCAMERA = Manifest.permission.CAMERA;
        String pRECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
        Collection<String> ListPermission = new ArrayList<>();
        if(UseBarcodeScanner) ListPermission.add(pCAMERA);
        if(UseSpeechToText) ListPermission.add(pRECORD_AUDIO);

        Dexter.withActivity(activity)
                .withPermissions(
                        ListPermission
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                List<PermissionDeniedResponse> ListDenide = report.getDeniedPermissionResponses();
                for(int i = 0; i < ListDenide.size(); i++){
                    if(ListDenide.get(i).getPermissionName().toUpperCase().contains("CAMERA"))
                        MBtnQrCode.setVisibility(GONE);
                    if(ListDenide.get(i).getPermissionName().toUpperCase().contains("RECORD_AUDIO"))
                        MBtnVoice.setVisibility(GONE);
                }
            }
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                for(int i = 0; i < permissions.size(); i++){
                    if(permissions.get(i).getName().toUpperCase().contains("CAMERA"))
                        if(UseSpeechToText) {
                            MBtnQrCode.setVisibility(VISIBLE);
                            Toast.makeText(activity,"CAMERA ....",Toast.LENGTH_SHORT).show();
                        }else
                            MBtnQrCode.setVisibility(GONE);
                    if(permissions.get(i).getName().toUpperCase().contains("RECORD_AUDIO"))
                        if(UseBarcodeScanner) {
                            Toast.makeText(activity,"AUDIO ....",Toast.LENGTH_SHORT).show();
                            MBtnVoice.setVisibility(VISIBLE);
                        }else
                            MBtnVoice.setVisibility(GONE);
                }
            }
        }).check();

    }

}
