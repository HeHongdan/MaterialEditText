package com.rengwuxian.materialedittext;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by Zhukai on 2014/5/29 0029.
 */
public class Density {

  public static int dp2px(Context context, float dp) {
    Resources r = context.getResources();
    float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    return Math.round(px);
  }


  public static int px2dp(Context context, final float pxValue) {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    return (int) (pxValue / displayMetrics.density + 0.5f);
  }

}
