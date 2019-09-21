package com.spisoft.spsedittextview;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.johnkil.print.PrintView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Locale;

public class SpsEditText extends RelativeLayout implements RecognitionListener {

//    public static String EXTRA_STATUS_BARCODE_ACTIVITY = "ExtraStatusBarcodeActivity";
//    public static String BARCPDE_READER_PLACE = "BarcodeReaderPlace";
//    public static String BARCPDE_READER_BARCODE = "BarcodeReaderBarcode";

    public static Typeface TF_Holo ;
    private InputMethodManager imm;
    private boolean SpeechStatus = false;
    private SpeechRecognizer speechRecognizer;

    private View rootView;
    private ProgressBar circleProgress;
    private EditText MText;
    private TextView MCnt;
    private PrintView MBtn;
    private PrintView MBtnVoice;
    private ImageView MBtnQrCode;
    private Context mContext;

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
        init(context, attrs);
    }

    private void init(final Context context, AttributeSet attrs){

        mContext = context;
        TF_Holo = Typeface.createFromAsset(context.getAssets(), "holo-icon-font.ttf" + "");

        rootView = inflate(context, R.layout.sps_layout, this);
        circleProgress = rootView.findViewById(R.id.cProgress);
        MText = rootView.findViewById(R.id.mText);
        MCnt = rootView.findViewById(R.id.mCnt);
        MBtn = rootView.findViewById(R.id.iSearch);
        MBtnVoice = rootView.findViewById(R.id.iVoice);
        MBtnQrCode = rootView.findViewById(R.id.iQr);

        imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
//                == PackageManager.PERMISSION_GRANTED) {
//        } else {
//            requestRecordAudioPermission(context);
//        }
        MBtn.setIconFont(TF_Holo);
        MBtnVoice.setIconFont(TF_Holo);

        MBtnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput(context);
            }
        });
        MBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MText.getText().toString().trim().equals("")) {

                }
            }
        });

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
                context.startActivity(intentQR, ActivityOptions.makeCustomAnimation(context, R.anim.animate_zoom_enter, R.anim.animate_zoom_exit).toBundle());
            }
        });

        //        int textColor = ContextCompat.getColor(context, R.color.color_text);
        if (attrs != null) {
            // Attribute initialization
            final TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.MyCustomElement, 0, 0);

            //Use a
            Log.i("test",a.getString(R.styleable.MyCustomElement_TextHint));

            MText.setHint(a.getString(R.styleable.MyCustomElement_TextHint));
            //Don't forget this
            a.recycle();
        }
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
            //handle click
            promptSpeechInput(mContext);

//            finishActivity(REQ_CODE_SPEECH_INPUT);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
