package com.hehongdan.materialedittext;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.hehongdan.LogUtils;
import com.rengwuxian.materialedittext.Colors;
import com.rengwuxian.materialedittext.Density;
import com.rengwuxian.materialedittext.R;
import com.rengwuxian.materialedittext.validation.METLengthChecker;
import com.rengwuxian.materialedittext.validation.METValidator;

import java.util.List;


/**
 * 类描述：自定义输入框。
 *
 * @author HeHongdan
 * @date 2021/1/26
 * @since v2021/1/26
 */
public class MaterialEditText3 extends AppCompatEditText {
    /** 图标的大小(默认)。 */
    private final int dIconSizeDp = Density.dp2px(getContext(), 16);
    /** 图标外部宽度(默认)。 */
    private final int dIconOuterHeightDp = 16;
    /** 图标外部高度(默认)。 */
    private final int dIconOuterWidthDp = 16;


    /** 之前的文本内容。 */
    private String proText;




    /** 左边文本。 */
    private String leftText;
    /** 左边文本颜色。 默认为黑色。*/
    private int leftTextColor;
    /** 左边文本宽度。 */
    private float leftTextWidth = 0;
    /** 左边文本宽度。 *///TODO
    private float leftTextOuterWidth = 0;
    /** 左边文本的(内)边距。 */
    private int leftTextPaddingRight;

    /** 只进来一次。 */
    private boolean isOne = true;
    /** 文本大小。 */
    private float textSize;
    /** 绘制文本(浮动、底部)的笔。 */
    private TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    /** 绘制图标(左右、清除)的笔。 */
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /** 是否改变(获取焦点、错误...)颜色。 */
    private boolean changeColor;
    /** 提示文本的颜色。 */
    private ColorStateList textColorHintStateList;
    /** 行和文本的基本颜色。 默认为黑色。the base color of the line and the texts. default is black. */
    private int baseColor;
    /** 下划线的颜色，浮动标签的颜色。*/
    private int primaryColor;
    /** 错误颜色(图标、)。*/
    private int errorColor;
    /** 错误颜色(图标、)。*/
    private final String ERROR_COLOR = "#e7492E";
    /** 颜色状态(选中、未选中...)列表。*/
    private ColorStateList textColorStateList;

    /** 最小字符数限制。 0表示没有限制。(影响行高) */
    private int minCharacters;
    /**最多字符数限制； 0表示没有限制。(影响行高) */
    private int maxCharacters;


    /** 左边图标。 */
    private Bitmap[] iconLeftBitmaps;
    /** 右边图标。 */
    private Bitmap[] iconRightBitmaps;
    /** 清除按钮图标。 */
    private Bitmap[] clearButtonBitmaps;
    /** 是否显示清除按钮。 */
    private boolean showClearButton;


    /** 左边图标右(内)边距。 */
    private float leftIconPaddingRight;
    /** 左边图标右(内)边距。 */
    private float rightIconPaddingLeft;
    /** 左边图标右(内)边距。 */
    private float leftIconOuterWidth;
    /** 左边图标右(内)边距。 */
    private float rightIconOuterWidth;
    /** 左边图标右(内)边距。 */
    private float leftIconOuterHeight;
    /** 左边图标右(内)边距。 */
    private float rightIconOuterHeight;
    /** 左边图标右(内)边距。 */
    private float clearButtonOuterWidth;



    /** 图标的大小。 */
    //private int iconSize;
    /** 图标外围的宽度。 */
    //private int iconOuterWidth;
    /** 图标外围的高度。 */
    //private int iconOuterHeight;
    /** 图标的(内)边距。 */
    //private int iconPadding;




    /** 左(内部的)边距。 */
    private int innerPaddingLeft;
    /** 右(内部的)边距。 */
    private int innerPaddingRight;
    /** 上(内部的)边距。 */
    private int innerPaddingTop;
    /** 下(内部的)边距。 */
    private int innerPaddingBottom;

    /** 正文和内部顶部填充之间的间距。 */
    private int extraPaddingTop;
    private int extraPaddingBottom;
    /** 正文和左侧之间的多余间距，实际上是左侧图标的间距。 */
    private float extraPaddingLeft;
    private float extraPaddingRight;

    /** 焦点变化监听事件(选中)。 */
    private OnFocusChangeListener innerFocusChangeListener;
    /** 焦点变化监听事件(未选中)。 */
    private OnFocusChangeListener outerFocusChangeListener;
    /** 清空按钮触摸。 */
    private boolean clearButtonTouched;
    /** 清空按钮点击。 */
    private boolean clearButtonClicking;
    //==============================================================================================
    /**
     * 文本更改后是否立即验证。 默认为False。Whether to validate as soon as the text has changed. False by default
     */
    private boolean autoValidate;
    private List<METValidator> validators;
    private boolean firstShown;
    /** 是否在开始显示时检查字符计数。Whether check the characters count at the beginning it's shown. */
    private boolean checkCharactersCountAtBeginning;
    /** 字符数是否有效。Whether the characters count is valid */
    private boolean charactersCountValid;
    /** 字符长度检测器。 */
    private METLengthChecker lengthChecker;

    /** 手动调用的错误文本 {@link #setError(CharSequence)}。 */
    private String tempErrorText;
    /** 主文本和底部组件之间的间距。 */
    private int bottomSpacing;
    /** 是否在单行模式下显示底部的省略号。 默认为false。 */
    //private boolean singleLineEllipsis;
    //==============================================================================================


    /**
     * 初始化文本监控器。//TODO 这里实现是否更改了文本(对比初始化时的文本和最后的文本)
     */
    private void initTextWatcher() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkCharactersCount();
                if (autoValidate) {
                    validate();
                } else {
                    setError(null);
                }
                postInvalidate();
            }
        });
    }

    /**
     * 检查所有验证器，如果没有，则设置错误文本。
     * <p />注意：此操作将在第一个验证器处报告无效。
     *
     * @return 如果所有验证者均通过，则为true；否则为false
     */
    public boolean validate() {
        if (validators == null || validators.isEmpty()) {
            return true;
        }

        CharSequence text = getText();
        boolean isEmpty = text.length() == 0;

        boolean isValid = true;
        for (METValidator validator : validators) {
            //noinspection ConstantConditions
            isValid = isValid && validator.isValid(text, isEmpty);
            if (!isValid) {
                setError(validator.getErrorMessage());
                break;
            }
        }
        if (isValid) {
            setError(null);
        }

        postInvalidate();
        return isValid;
    }


    public MaterialEditText3(Context context) {
        super(context);
        init(context, null);
    }

    public MaterialEditText3(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MaterialEditText3(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        init(context, attrs);
    }



    private void init(Context context, AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }

        //32dp
        //iconSize = Density.dp2px(getContext(), dIconSizeDp);
        //iconOuterWidth = Density.dp2px(getContext(), dIconOuterWidthDp);
        //iconOuterHeight = Density.dp2px(getContext(), dIconOuterHeightDp);

        // default baseColor is black
        int defaultBaseColor = Color.BLACK;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaterialEditText);
        textColorStateList = typedArray.getColorStateList(R.styleable.MaterialEditText_met_textColor);
        textColorHintStateList = typedArray.getColorStateList(R.styleable.MaterialEditText_met_textColorHint);
        baseColor = typedArray.getColor(R.styleable.MaterialEditText_met_baseColor, defaultBaseColor);
        leftTextColor = typedArray.getColor(R.styleable.MaterialEditText_met_leftTextColor, defaultBaseColor);
        errorColor = typedArray.getColor(R.styleable.MaterialEditText_met_errorColor, Color.parseColor(ERROR_COLOR));
        primaryColor = getPrimaryColor(context,typedArray);
        changeColor = typedArray.getBoolean(R.styleable.MaterialEditText_met_changeColor, false);
        leftText = typedArray.getString(R.styleable.MaterialEditText_met_leftText);

        minCharacters = typedArray.getInt(R.styleable.MaterialEditText_met_minCharacters, 0);
        maxCharacters = typedArray.getInt(R.styleable.MaterialEditText_met_maxCharacters, 0);

        iconLeftBitmaps = generateIconBitmaps(typedArray.getResourceId(R.styleable.MaterialEditText_met_iconLeft, -1), Density.dp2px(getContext(), 16));
        iconRightBitmaps = generateIconBitmaps(typedArray.getResourceId(R.styleable.MaterialEditText_met_iconRight, -1), Density.dp2px(getContext(), 16));
        showClearButton = typedArray.getBoolean(R.styleable.MaterialEditText_met_clearButton, false);
        clearButtonBitmaps = generateIconBitmaps(R.drawable.met_ic_clear, Density.dp2px(getContext(), 32));
        //iconPadding = typedArray.getDimensionPixelSize(R.styleable.MaterialEditText_met_iconPadding, Density.dp2px(getContext(), 0));
        leftIconPaddingRight = typedArray.getDimensionPixelSize(R.styleable.MaterialEditText_met_leftIconPaddingRight, Density.dp2px(getContext(), 16));
        rightIconPaddingLeft = typedArray.getDimensionPixelSize(R.styleable.MaterialEditText_met_rightIconPaddingLeft, Density.dp2px(getContext(), 16));

        leftTextPaddingRight = typedArray.getDimensionPixelSize(R.styleable.MaterialEditText_met_leftTextPaddingRight, Density.dp2px(getContext(), 0));

        typedArray.recycle();

        int[] paddings = new int[]{
                android.R.attr.padding,
                android.R.attr.paddingLeft,
                android.R.attr.paddingTop,
                android.R.attr.paddingRight,
                android.R.attr.paddingBottom
        };
        TypedArray paddingsTypedArray = context.obtainStyledAttributes(attrs, paddings);
        int padding = paddingsTypedArray.getDimensionPixelSize(0, 0);
        innerPaddingLeft = paddingsTypedArray.getDimensionPixelSize(1, padding);
        innerPaddingTop = paddingsTypedArray.getDimensionPixelSize(2, padding);
        innerPaddingRight = paddingsTypedArray.getDimensionPixelSize(3, padding);
        innerPaddingBottom = paddingsTypedArray.getDimensionPixelSize(4, padding);
        paddingsTypedArray.recycle();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(null);
        } else {
            setBackgroundDrawable(null);
        }


        initPadding();
        initText();
        initTextWatcher();
        checkCharactersCount();
    }

    /**
     * 获取主题主颜色。
     *
     * @param context
     * @param typedArray
     * @return
     */
    @ColorInt
    private int getPrimaryColor(Context context, TypedArray typedArray) {
        // 系统主颜色。retrieve the default primaryColor
        int defaultPrimaryColor;
        TypedValue primaryColorTypedValue = new TypedValue();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                context.getTheme().resolveAttribute(android.R.attr.colorPrimary, primaryColorTypedValue, true);
                defaultPrimaryColor = primaryColorTypedValue.data;
            } else {
                throw new RuntimeException("SDK_INT less than LOLLIPOP");
            }
        } catch (Exception e) {
            try {
                int colorPrimaryId = getResources().getIdentifier("colorPrimary", "attr", getContext().getPackageName());
                if (colorPrimaryId != 0) {
                    context.getTheme().resolveAttribute(colorPrimaryId, primaryColorTypedValue, true);
                    defaultPrimaryColor = primaryColorTypedValue.data;
                } else {
                    throw new RuntimeException("colorPrimary not found");
                }
            } catch (Exception e1) {
                defaultPrimaryColor = baseColor;
            }
        }

        return typedArray.getColor(R.styleable.MaterialEditText_met_primaryColor, defaultPrimaryColor);
    }



    /**
     * 初始化文本。
     */
    private void initText() {
        if (!TextUtils.isEmpty(getText())) {
            CharSequence text = getText();
            setText(null);
            resetHintTextColor();
            setText(text);
            setSelection(text.length());
        } else {
            resetHintTextColor();
        }
        resetTextColor();
    }

    /**
     * 重置文本颜色。
     */
    private void resetTextColor() {
        if (textColorStateList == null) {
            textColorStateList = new ColorStateList(new int[][]{new int[]{android.R.attr.state_enabled}, EMPTY_STATE_SET}, new int[]{baseColor & 0x00ffffff | 0xdf000000, baseColor & 0x00ffffff | 0x44000000});
            setTextColor(textColorStateList);
        } else {
            setTextColor(textColorStateList);
        }
    }

    /**
     * 重置提示文本颜色。
     */
    private void resetHintTextColor() {
        if (textColorHintStateList == null) {
            setHintTextColor(baseColor & 0x00ffffff | 0x44000000);
        } else {
            setHintTextColor(textColorHintStateList);
        }
    }


    /**
     * 从右到左的布局(RTL Layout)。
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean isRTL() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return false;
        }
        Configuration config = getResources().getConfiguration();
        return config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    private int getButtonsCount() {
        return isShowClearButton() ? 1 : 0;
    }

    public boolean isShowClearButton() {
        return showClearButton;
    }

    /**
     * 生成(位图)图标【普通、选中(焦点)、不可用、无效】。
     *
     * @param origin 图标资源Id。
     * @return 位图。
     */
    private Bitmap[] generateIconBitmaps(@DrawableRes int origin,final int iconSiz) {
        if (origin == -1) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), origin, options);
        int size = Math.max(options.outWidth, options.outHeight);
        options.inSampleSize = size >  dIconSizeDp? size / dIconSizeDp : 1;
        options.inJustDecodeBounds = false;

        LogUtils.d(getResources() + " " + origin + " " + options);
        return generateIconBitmaps(BitmapFactory.decodeResource(getResources(), origin, options),iconSiz);
    }

    /**
     * 生成(位图)图标【普通、选中(焦点)、不可用、无效】。
     *
     * @param origin 位图。
     * @return 位图。
     */
    private Bitmap[] generateIconBitmaps(Bitmap origin,final int iconSiz) {
        if (origin == null) {
            return null;
        }
        Bitmap[] iconBitmaps = new Bitmap[4];
        LogUtils.d("原图= "+origin);
        origin = scaleIcon(origin, iconSiz);
        LogUtils.w("原图= "+origin);

        if (changeColor) {
            iconBitmaps[0] = origin.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(iconBitmaps[0]);
            canvas.drawColor(baseColor & 0x00ffffff | (Colors.isLight(baseColor) ? 0xff000000 : 0x8a000000), PorterDuff.Mode.SRC_IN);

            iconBitmaps[1] = origin.copy(Bitmap.Config.ARGB_8888, true);
            canvas = new Canvas(iconBitmaps[1]);
            canvas.drawColor(primaryColor, PorterDuff.Mode.SRC_IN);

            iconBitmaps[2] = origin.copy(Bitmap.Config.ARGB_8888, true);
            canvas = new Canvas(iconBitmaps[2]);
            canvas.drawColor(baseColor & 0x00ffffff | (Colors.isLight(baseColor) ? 0x4c000000 : 0x42000000), PorterDuff.Mode.SRC_IN);

            iconBitmaps[3] = origin.copy(Bitmap.Config.ARGB_8888, true);
            canvas = new Canvas(iconBitmaps[3]);
            canvas.drawColor(errorColor, PorterDuff.Mode.SRC_IN);
        } else {
            iconBitmaps[0] = origin.copy(Bitmap.Config.ARGB_8888, true);

            iconBitmaps[1] = origin.copy(Bitmap.Config.ARGB_8888, true);

            iconBitmaps[2] = origin.copy(Bitmap.Config.ARGB_8888, true);

            iconBitmaps[3] = origin.copy(Bitmap.Config.ARGB_8888, true);
        }

        return iconBitmaps;
    }

    /**
     * 图标尺寸。
     *
     * @param origin 原图。
     * @return
     */
    private Bitmap scaleIcon(final Bitmap origin,final int iconSiz) {
        int width = origin.getWidth();
        int height = origin.getHeight();
        int size = Math.max(width, height);
        if (size == dIconSizeDp) {
            return origin;
        } else if (size > dIconSizeDp) {
            int scaledWidth;
            int scaledHeight;
            if (width > dIconSizeDp) {
                scaledWidth = dIconSizeDp;
                scaledHeight = (int) (dIconSizeDp * ((float) height / width));
            } else {
                scaledHeight = dIconSizeDp;
                scaledWidth = (int) (dIconSizeDp * ((float) width / height));
            }
            return Bitmap.createScaledBitmap(origin, scaledWidth, scaledHeight, false);
        } else {
            return origin;
        }
    }

    /**
     * 线内部的清空按钮。
     *
     * @param event
     * @return
     */
    private boolean insideClearButton(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float startX = getScrollX() + (iconLeftBitmaps == null ? 0 : (leftIconOuterWidth + leftIconPaddingRight));
        float endX = getScrollX() + (iconRightBitmaps == null ? getWidth() : getWidth() - rightIconOuterWidth - rightIconPaddingLeft);
        float buttonLeft;
        float iconOuterWidth;
        if (isRTL()) {
            buttonLeft = startX;
            iconOuterWidth = leftIconOuterWidth;
        } else {
            buttonLeft = endX - rightIconOuterWidth;
            iconOuterWidth = rightIconOuterWidth;
        }
        float buttonTop = getScrollY() + getHeight() - getPaddingBottom() + bottomSpacing - Math.max(leftIconOuterHeight, rightIconOuterHeight);
        return (x >= buttonLeft && x < buttonLeft + iconOuterWidth && y >= buttonTop && y < buttonTop + Math.max(leftIconOuterHeight, rightIconOuterHeight));
    }

    /**
     * Use this method instead of {@link #setPadding(int, int, int, int)} to automatically set the paddingTop and the paddingBottom correctly.
     */
    public void setPaddings(int left, int top, int right, int bottom) {
        innerPaddingTop = top;
        innerPaddingBottom = bottom;
        innerPaddingLeft = left;
        innerPaddingRight = right;
        correctPaddings();
    }





    /**
     * 校验字符个数。
     */
    private void checkCharactersCount() {
        if ((!firstShown && !checkCharactersCountAtBeginning) || !hasCharactersCounter()) {
            charactersCountValid = true;
        } else {
            CharSequence text = getText();
            int count = text == null ? 0 : checkLength(text);
            charactersCountValid = (count >= minCharacters && (maxCharacters <= 0 || count <= maxCharacters));
        }
    }

    /**
     * 检查字符长度。
     *
     * @param text
     * @return
     */
    private int checkLength(CharSequence text) {
        if (lengthChecker == null) {
            return text.length();
        }
        return lengthChecker.getLength(text);
    }

    /**
     * 是否有最大最小字符限制。
     *
     * @return
     */
    private boolean hasCharactersCounter() {
        return minCharacters > 0 || maxCharacters > 0;
    }


    /**
     * 内部有效。
     *
     * @return
     */
    private boolean isInternalValid() {
        return tempErrorText == null && isCharactersCountValid();
    }

    /**
     * 有效文本个数。
     *
     * @return
     */
    public boolean isCharactersCountValid() {
        return charactersCountValid;
    }

    /**
     * 以前和现在的文本是否相同。
     *
     * @return 是否相同。
     */
    public boolean isSemp() {
        LogUtils.d("以前= " + proText + "，现在= " + getText());
        if (TextUtils.isEmpty(getText())) {
            if (TextUtils.isEmpty(proText)) {
                return true;
            } else {
                return false;
            }
        } else {
            return getText().toString().equals(proText);
        }
    }


    /**
     * use {@link #setPaddings(int, int, int, int)} instead, or the paddingTop and the paddingBottom may be set incorrectly.
     */
    @Deprecated
    @Override
    public final void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
    }

    @Override
    public void setError(CharSequence errorText) {
        tempErrorText = errorText == null ? null : errorText.toString();
//        if (adjustBottomLines()) {
//            postInvalidate();
//        }
    }


    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener listener) {
        if (innerFocusChangeListener == null) {
            super.setOnFocusChangeListener(listener);
        } else {
            outerFocusChangeListener = listener;
        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!firstShown) {
            firstShown = true;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (isOne) {
            proText = getText().toString();
            textSize = getTextSize();
            isOne = false;
        }

        LogUtils.d("获取文本= " + getText() + " " + Density.px2dp(getContext(), getTextSize()));
        if (changed) {
//            adjustBottomLines();
        }
    }


    /**
     *             // calculate the horizontal position
     *             float floatingLabelWidth = textPaint.measureText(floatingLabelText.toString());
     *             int floatingLabelStartX;
     *             if ((getGravity() & Gravity.RIGHT) == Gravity.RIGHT || isRTL()) {
     *                 floatingLabelStartX = (int) (endX - floatingLabelWidth);
     *             } else if ((getGravity() & Gravity.LEFT) == Gravity.LEFT) {
     *                 floatingLabelStartX = startX;
     *             } else {
     *                 floatingLabelStartX = startX + (int) (getInnerPaddingLeft() + (getWidth() - getInnerPaddingLeft() - getInnerPaddingRight() - floatingLabelWidth) / 2);
     *             }
     *
     *             // calculate the vertical position
     *             int distance = floatingLabelPadding;
     *             int floatingLabelStartY = (int) (innerPaddingTop + floatingLabelTextSize + floatingLabelPadding - distance * (floatingLabelAlwaysShown ? 1 : floatingLabelFraction) + getScrollY());
     *
     * @param canvas
     */
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        float startX = getScrollX() + (iconLeftBitmaps == null ? 0 : (leftIconOuterWidth + leftIconPaddingRight));
        float endX = getScrollX() + (iconRightBitmaps == null ? getWidth() : getWidth() - rightIconOuterWidth - rightIconPaddingLeft);
        //底线的Y轴起点=滑动顶部+整高+底(内)边距
        int lineStartY = getScrollY() + getHeight() - getPaddingBottom();

       //以ET文本基线为准
        Paint.FontMetrics fm = getPaint().getFontMetrics();
        float baseLine = getHeight() / 2.0f - (fm.top + fm.bottom) / 2;

        /** 绘制(左)文本 */
        if (!TextUtils.isEmpty(leftText)) {
            textPaint.setAlpha(255);
            textPaint.setTextSize(getTextSize());
            textPaint.setColor(leftTextColor);
            textPaint.setFakeBoldText(true);
            canvas.drawText(leftText, isRTL() ? endX - textPaint.measureText(leftText) : getScrollX(), baseLine, textPaint);
        }

        /** 绘制(左)图标 */
        if (iconLeftBitmaps != null) {
            paint.setAlpha(255);
            //【普通、选中(焦点)、不可用、无效】。
            Bitmap icon = iconLeftBitmaps[!isInternalValid() ? 3 : !isEnabled() ? 2 : hasFocus() ? 1 : 0];
            float iconLeft = startX - leftIconPaddingRight - leftIconOuterWidth + (leftIconOuterWidth - icon.getWidth()) / 2.0f + (leftTextWidth + leftTextPaddingRight);
            icon = correctIconY(icon);
            //图标顶部=底线+底空隙-图标外高+(图标外高-图标高度)
            float iconTop = lineStartY + bottomSpacing - leftIconOuterHeight + (leftIconOuterHeight - icon.getHeight()) / 2;
            canvas.drawBitmap(icon, iconLeft, iconTop, paint);
        }

        /** 绘制(右)图标 */
        if (iconRightBitmaps != null) {
            paint.setAlpha(255);
            Bitmap icon = iconRightBitmaps[!isInternalValid() ? 3 : !isEnabled() ? 2 : hasFocus() ? 1 : 0];
            float iconLeft = endX + rightIconPaddingLeft + (rightIconOuterWidth - icon.getWidth()) / 2.0f;
            icon = correctIconY(icon);
            float iconTop = lineStartY + bottomSpacing - rightIconOuterHeight + (rightIconOuterHeight - icon.getHeight()) / 2;
            canvas.drawBitmap(icon, iconLeft, iconTop, paint);
        }

        /** 绘制(清空)图标 */
        if (hasFocus() && showClearButton && !TextUtils.isEmpty(getText()) && isEnabled()) {
            paint.setAlpha(255);
            Bitmap clearButtonBitmap = clearButtonBitmaps[0];

            float buttonLeft;
            if (isRTL()) {
                buttonLeft = startX;
            } else {
                buttonLeft = endX - clearButtonBitmap.getWidth();
            }
            buttonLeft += (clearButtonBitmap.getWidth() - clearButtonBitmap.getWidth()) / 2;

            //int iconTop = lineStartY + bottomSpacing - iconOuterHeight + (iconOuterHeight - clearButtonBitmap.getHeight()) / 2;
            int iconTop = (getHeight() - clearButtonBitmap.getHeight()) / 2;
            canvas.drawBitmap(clearButtonBitmap, buttonLeft, iconTop, paint);
        }

        // draw the original things
        super.onDraw(canvas);
    }

    /**
     * 矫正图标Y轴。
     *
     * @param icon 图标。
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private Bitmap correctIconY(final Bitmap icon) {
        //HHD
        if (icon.getHeight() > getHeight()) {
            return scaleBitmap(icon, getHeight());
        } else {
            return icon;
        }
    }

    /**
     * 根据给定的宽和高进行拉伸。
     *
     * @param origin    原图。
     * @param newHeight 新图的高。
     * @return 拉伸后位图。
     */
    private Bitmap scaleBitmap(Bitmap origin, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        int newWidth = newHeight * width / height;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        // 使用后乘
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        origin = newBM;
//        if (!origin.isRecycled()) {
//            origin.recycle();
//        }

        return origin;
    }

    /**
     * 使用此方法代替{@link #setPadding(int, int, int, int)} 来自动正确设置paddingTop和paddingBottom。
     */
    private void initPadding() {
        //extraPaddingTop = floatingLabelEnabled ? floatingLabelTextSize + floatingLabelPadding : floatingLabelPadding;
        //textPaint.setTextSize(bottomTextSize);
        Paint.FontMetrics textMetrics = textPaint.getFontMetrics();
        //extraPaddingBottom = (int) ((textMetrics.descent - textMetrics.ascent) * currentBottomLines) + (hideUnderline ? bottomSpacing : bottomSpacing * 2);


        textPaint.setTextSize(getTextSize());
        leftTextWidth = textPaint.measureText(leftText);
        leftTextOuterWidth += leftTextWidth;


        if (iconLeftBitmaps != null) {
            extraPaddingLeft += (leftIconOuterWidth + leftIconPaddingRight);
            LogUtils.d("左边额外= " + extraPaddingLeft);
        }
        if (!TextUtils.isEmpty(leftText)) {
            extraPaddingLeft += (int) (leftTextOuterWidth + leftTextPaddingRight);
            LogUtils.d("左边额外= " + extraPaddingLeft);
        } else {
            leftTextPaddingRight = 0;
        }

        extraPaddingRight = iconRightBitmaps == null ? 0 : (rightIconOuterWidth + rightIconPaddingLeft);
        clearButtonOuterWidth = clearButtonBitmaps == null ? 0 : (clearButtonBitmaps.length > 0 ? clearButtonBitmaps[0].getWidth() : 0);
        correctPaddings();
    }

    /**
     * 将填充设置为正确的值。
     */
    private void correctPaddings() {
        float buttonsWidthLeft = 0, buttonsWidthRight = 0;
        //清空按钮的宽度
        float buttonsWidth = clearButtonOuterWidth * getButtonsCount();
        if (isRTL()) {
            buttonsWidthLeft = buttonsWidth;
        } else {
            buttonsWidthRight = buttonsWidth;
        }
        super.setPadding((int) (innerPaddingLeft + extraPaddingLeft + buttonsWidthLeft), innerPaddingTop + extraPaddingTop, (int)(innerPaddingRight + extraPaddingRight + buttonsWidthRight), innerPaddingBottom + extraPaddingBottom);
    }

    /**
     * 更新文本内容(如按保存后)。
     */
    public void updateText() {
        proText = getText().toString();
    }








    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getScrollX() > 0 && event.getAction() == MotionEvent.ACTION_DOWN && event.getX() < Density.dp2px(getContext(), 4 * 5) && event.getY() > getHeight() - extraPaddingBottom - innerPaddingBottom && event.getY() < getHeight() - innerPaddingBottom) {
//        if (singleLineEllipsis && getScrollX() > 0 && event.getAction() == MotionEvent.ACTION_DOWN && event.getX() < getPixel(4 * 5) && event.getY() > getHeight() - extraPaddingBottom - innerPaddingBottom && event.getY() < getHeight() - innerPaddingBottom) {
            setSelection(0);
            return false;
        }
        if (hasFocus() && showClearButton && isEnabled()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (insideClearButton(event)) {
                        clearButtonTouched = true;
                        clearButtonClicking = true;
                        return true;
                    }
                case MotionEvent.ACTION_MOVE:
                    if (clearButtonClicking && !insideClearButton(event)) {
                        clearButtonClicking = false;
                    }
                    if (clearButtonTouched) {
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (clearButtonClicking) {
                        if (!TextUtils.isEmpty(getText())) {
                            setText(null);
                        }
                        clearButtonClicking = false;
                    }
                    if (clearButtonTouched) {
                        clearButtonTouched = false;
                        return true;
                    }
                    clearButtonTouched = false;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    clearButtonTouched = false;
                    clearButtonClicking = false;
                    break;

                default:break;
            }
        }
        return super.onTouchEvent(event);
    }

}
