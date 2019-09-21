package com.spisoft.spsedittextview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.johnkil.print.PrintDrawable;
import com.github.johnkil.print.PrintView;

public class GlobalCls {

    private static Typeface TF_Material;

    public static Drawable buildCounterRecDrawable(Context pContext, int pCount, int pIcon, int pColor, int pSize, Typeface pTF) {
        TF_Material = Typeface.createFromAsset(pContext.getAssets(), "material-icon-font.ttf" + "");

        if(pTF == null)
            pTF = TF_Material;

        int tSize = R.dimen.sps_txt_sz_18 ,iSize = R.dimen.sps_lpr_sz_50, cSize = R.dimen.sps_lpr_sz_30;
        switch (pSize) {
            case 0 :
                iSize = R.dimen.sps_lpr_sz_50;
                tSize = R.dimen.sps_txt_sz_18;
                break;
            case 1 :
                iSize = R.dimen.sps_lpr_sz_25;
                tSize = R.dimen.sps_txt_sz_8;
                break;
        }
        LayoutInflater inflater = LayoutInflater.from(pContext);
        View view = inflater.inflate(R.layout.menu_icon_counter, null);
        ImageView mIcon = view.findViewById(R.id.ivIcon);
        mIcon.setImageDrawable(new PrintDrawable
                .Builder(pContext)
                .iconTextRes(pIcon)
                .iconFont(pTF)
                .iconColor(Color.WHITE)
                .iconSizeRes(iSize)
                .build()
        );

        PrintView vCircle = view.findViewById(R.id.ivCircle);
        vCircle.setIconColor(pColor);
        vCircle.setIconFont(TF_Material);

        TextView IvText = view.findViewById(R.id.ivText);
        IvText.setTextColor(Color.BLACK);
        IvText.setTextSize(TypedValue.COMPLEX_UNIT_PX, pContext.getResources().getDimension(tSize));

        RelativeLayout vView = view.findViewById(R.id.ivRly);

        if(pSize == 1) {
            android.view.ViewGroup.LayoutParams params = vView.getLayoutParams();
            params.width = params.width / 2;
            params.height = params.height / 2;
            vView.setLayoutParams(params);
        }

        if (pCount <= 0) {
            vView.setVisibility(View.GONE);
        } else {
            vView.setVisibility(View.VISIBLE);
            if(pCount > 99)
                IvText.setText("+");
            else
                IvText.setText("" + pCount);
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(pContext.getResources(), bitmap);
    }

}
