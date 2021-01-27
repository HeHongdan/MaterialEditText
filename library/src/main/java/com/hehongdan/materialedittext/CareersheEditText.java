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
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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


/**
 * 类描述：。
 *
 * @author HeHongdan
 * @date 2021/1/26
 * @since v2021/1/26
 */
public class CareersheEditText extends AppCompatEditText {

    /** 右边图标。 */
    private Bitmap[] iconRightBitmaps;
    /** 图标的(内)边距。 */
    private int rightIconPaddingLeft;
    private int rightIconOuterWidth;
    private int rightIconOuterHeight;

    /** 图标的大小。 */
    private int clearIconSize;
    /** 图标外部宽度。 */
    private int clearIconOuterWidth;
    /** 图标外部高度。 */
    private int clearIconOuterHeight;

    /** 之前的文本内容(保存时进行比较)。 */
    private String priorText;
    /** 是否开始首次显示了。 */
    private boolean firstShown;
    /** 左边文本。 */
    private String leftText;
    /** 左边文本颜色。 默认为黑色。 */
    private int leftTextColor;
    /** 左边文本宽度。 */
    private float leftTextWidth = 0;
    /** 左边文本的(内)边距。 */
    private int leftTextPaddingRight;

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
    /** 下划线的颜色，浮动标签的颜色。 */
    private int primaryColor;
    /** 错误颜色(图标、)。 */
    private int errorColor;
    /** 错误颜色(图标、)。 */
    private final String ERROR_COLOR = "#e7492E";
    /** 颜色状态(选中、未选中...)列表。 */
    private ColorStateList textColorStateList;

    /** 最小字符数限制。 0表示没有限制。(影响行高) */
    private int minCharacters;
    /** 最多字符数限制； 0表示没有限制。(影响行高) */
    private int maxCharacters;

    /** 清除按钮图标。 */
    private Bitmap[] clearButtonBitmaps;
    /** 是否彩色清除图标。 */
    private boolean clearMultiColour;
    /** 是否显示清除按钮。 */
    private boolean showClearButton;

    /** 清空按钮触摸。 */
    private boolean clearButtonTouched;
    /** 清空按钮点击。 */
    private boolean clearButtonClicking;
    /** 字符个数是否有效。 */
    private boolean charactersCountValid;


    /** 左(内)边距。 */
    private int innerPaddingLeft;
    /** 右(内)边距。 */
    private int innerPaddingRight;
    /** 上(内)边距。 */
    private int innerPaddingTop;
    /** 下(内)边距。 */
    private int innerPaddingBottom;

    /** 正文和左侧之间的多余间距，实际上是左侧图标的间距。 */
    private int extraPaddingLeft;
    private int extraPaddingRight;

    /** 焦点变化监听事件(选中)。 */
    private OnFocusChangeListener innerFocusChangeListener;
    /** 焦点变化监听事件(未选中)。 */
    private OnFocusChangeListener outerFocusChangeListener;

    /**
     * 初始化文本监控器。
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
                postInvalidate();
            }
        });
    }

    public CareersheEditText(Context context) {
        super(context);
        init(context, null);
    }

    public CareersheEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CareersheEditText(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }

        //32dp
        clearIconSize = Density.dp2px(getContext(), 16);
        clearIconOuterWidth = Density.dp2px(getContext(), 16);
        clearIconOuterHeight = Density.dp2px(getContext(), 16);

        // default baseColor is black
        int defaultBaseColor = Color.BLACK;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaterialEditText);
        textColorStateList = typedArray.getColorStateList(R.styleable.MaterialEditText_met_textColor);
        textColorHintStateList = typedArray.getColorStateList(R.styleable.MaterialEditText_met_textColorHint);
        baseColor = typedArray.getColor(R.styleable.MaterialEditText_met_baseColor, defaultBaseColor);
        leftTextColor = typedArray.getColor(R.styleable.MaterialEditText_met_leftTextColor, defaultBaseColor);
        errorColor = typedArray.getColor(R.styleable.MaterialEditText_met_errorColor, Color.parseColor(ERROR_COLOR));
        primaryColor = getPrimaryColor(context, typedArray);
        changeColor = typedArray.getBoolean(R.styleable.MaterialEditText_met_changeColor, false);
        leftText = typedArray.getString(R.styleable.MaterialEditText_met_leftText);

        minCharacters = typedArray.getInt(R.styleable.MaterialEditText_met_minCharacters, 0);
        maxCharacters = typedArray.getInt(R.styleable.MaterialEditText_met_maxCharacters, 0);

        iconRightBitmaps = generateIconBitmaps(typedArray.getResourceId(R.styleable.MaterialEditText_met_iconRight, -1));
        showClearButton = typedArray.getBoolean(R.styleable.MaterialEditText_met_clearButton, false);
        clearMultiColour = typedArray.getBoolean(R.styleable.MaterialEditText_met_clearMultiColour, false);
        clearButtonBitmaps = generateIconBitmaps(R.drawable.met_ic_clear);
        rightIconPaddingLeft = typedArray.getDimensionPixelSize(R.styleable.MaterialEditText_met_rightIconPaddingLeft, 0);
        rightIconOuterWidth = typedArray.getDimensionPixelSize(R.styleable.MaterialEditText_met_rightIconOuterWidth, Density.dp2px(getContext(), 16));
        rightIconOuterHeight = typedArray.getDimensionPixelSize(R.styleable.MaterialEditText_met_rightIconOuterHeight, Density.dp2px(getContext(), 16));

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
            textColorStateList = new ColorStateList(new int[][]{new int[]{android.R.attr.state_enabled}, EMPTY_STATE_SET},
                    new int[]{baseColor & 0x00ffffff | 0xdf000000, baseColor & 0x00ffffff | 0x44000000});
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
    private Bitmap[] generateIconBitmaps(@DrawableRes int origin) {
        if (origin == -1) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), origin, options);
        int size = Math.max(options.outWidth, options.outHeight);
        options.inSampleSize = size > clearIconSize ? size / clearIconSize : 1;
        options.inJustDecodeBounds = false;

        return generateIconBitmaps(BitmapFactory.decodeResource(getResources(), origin, options));
    }

    /**
     * 生成(位图)图标。
     *
     * @param drawable
     * @return
     */
    private Bitmap[] generateIconBitmaps(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        //抗锯齿
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return generateIconBitmaps(Bitmap.createScaledBitmap(bitmap, clearIconSize, clearIconSize, false));
    }

    /**
     * 生成(位图)图标【普通、选中(焦点)、不可用、无效】。
     *
     * @param origin 位图。
     * @return 位图。
     */
    private Bitmap[] generateIconBitmaps(Bitmap origin) {
        if (origin == null) {
            return null;
        }
        Bitmap[] iconBitmaps = new Bitmap[4];
        origin = scaleIcon(origin);

        if (changeColor) {
            iconBitmaps[0] = origin.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(iconBitmaps[0]);
            //抗锯齿
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvas.drawColor(baseColor & 0x00ffffff | (Colors.isLight(baseColor) ? 0xff000000 : 0x8a000000), PorterDuff.Mode.SRC_IN);

            iconBitmaps[1] = origin.copy(Bitmap.Config.ARGB_8888, true);
            canvas = new Canvas(iconBitmaps[1]);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvas.drawColor(primaryColor, PorterDuff.Mode.SRC_IN);

            iconBitmaps[2] = origin.copy(Bitmap.Config.ARGB_8888, true);
            canvas = new Canvas(iconBitmaps[2]);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvas.drawColor(baseColor & 0x00ffffff | (Colors.isLight(baseColor) ? 0x4c000000 : 0x42000000), PorterDuff.Mode.SRC_IN);

            iconBitmaps[3] = origin.copy(Bitmap.Config.ARGB_8888, true);
            canvas = new Canvas(iconBitmaps[3]);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
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
     * @param origin
     * @return
     */
    private Bitmap scaleIcon(Bitmap origin) {
        int width = origin.getWidth();
        int height = origin.getHeight();
        int size = Math.max(width, height);
        if (size == clearIconSize) {
            return origin;
        } else if (size > clearIconSize) {
            int scaledWidth;
            int scaledHeight;
            if (width > clearIconSize) {
                scaledWidth = clearIconSize;
                scaledHeight = (int) (clearIconSize * ((float) height / width));
            } else {
                scaledHeight = clearIconSize;
                scaledWidth = (int) (clearIconSize * ((float) width / height));
            }
            return Bitmap.createScaledBitmap(origin, scaledWidth, scaledHeight, true);
        } else {
            return origin;
        }
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
        if (!firstShown || !hasCharactersCounter()) {
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
        return text.length();
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
        return isCharactersCountValid();
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
    public boolean isSame() {
        LogUtils.d("以前= " + priorText + "，现在= " + getText());
        if (TextUtils.isEmpty(getText())) {
            if (TextUtils.isEmpty(priorText)) {
                return true;
            } else {
                return false;
            }
        } else {
            return getText().toString().equals(priorText);
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
            priorText = getText().toString();
        }
        LogUtils.d("首次显示的文本= "+ getText());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }


    /**
     * 矫正图标Y轴。
     *
     * @param icon
     */
    private void correctIconY(final Bitmap icon) {
        //HHD
        if (clearIconOuterHeight > getHeight()) {
            clearIconOuterHeight = getHeight();
        }
        if (icon.getHeight() > getHeight()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                icon.setHeight(getHeight());
            } else {
                //TODO
            }
        }
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

        if (!TextUtils.isEmpty(leftText)) {
            extraPaddingLeft += (int) (leftTextWidth + leftTextPaddingRight);
        } else {
            leftTextPaddingRight = 0;
        }

        extraPaddingRight = iconRightBitmaps == null ? 0 : (clearIconOuterWidth + rightIconPaddingLeft);
        correctPaddings();
    }

    /**
     * 将填充设置为正确的值
     */
    private void correctPaddings() {
        int buttonsWidthLeft = 0, buttonsWidthRight = 0;
        //清空按钮的宽度
        int buttonsWidth = clearIconOuterWidth * getButtonsCount();
        if (isRTL()) {
            buttonsWidthLeft = buttonsWidth;
        } else {
            buttonsWidthRight = buttonsWidth;
        }
        super.setPadding(innerPaddingLeft + extraPaddingLeft + buttonsWidthLeft, innerPaddingTop,
                innerPaddingRight + extraPaddingRight + buttonsWidthRight, innerPaddingBottom);
    }

    /**
     * 判断(事件)是否在清空按钮范围。
     *
     * @param event
     * @return
     */
    private boolean insideClearButton(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        //输入框X轴开始
        float startX = getScrollX() + (TextUtils.isEmpty(leftText) ? 0 : (leftTextWidth + leftTextPaddingRight));
        //输入框X轴结束
        float endX = iconRightBitmaps == null ? getWidth() : getWidth() - rightIconOuterWidth - rightIconPaddingLeft;
        float buttonLeft;
        if (isRTL()) {
            buttonLeft = startX;
        } else {
            buttonLeft = endX - clearIconOuterWidth;
        }
        float lineStartY = getScrollY() + getHeight() - getPaddingBottom();
        float buttonTop = (lineStartY - clearIconOuterHeight) / 2;
        LogUtils.d(
                "清空按钮范围：X= " + (x >= buttonLeft)
                        + "，X= " + (x < buttonLeft + clearIconOuterWidth)
                        + "；Y= " + (y >= buttonTop)
                        + "，Y= " + (y < buttonTop + clearIconOuterHeight)
        );
        return (x >= buttonLeft &&
                x < buttonLeft + clearIconOuterWidth &&

                y >= buttonTop &&
                y < buttonTop + clearIconOuterHeight);
    }

    /**
     * 绘制各个组件。
     *
     * @param canvas 画布。，
     */
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        //输入框X轴开始
        int startX = (int) (getScrollX() + (TextUtils.isEmpty(leftText) ? 0 : (leftTextWidth + leftTextPaddingRight)));
        //输入框X轴结束
        int endX = getScrollX() + (iconRightBitmaps == null ? getWidth() : getWidth() - rightIconOuterWidth - rightIconPaddingLeft);
//        LogUtils.d("X轴结束= " + endX
//                + "，getWidth= " + getWidth()
//                + "，getScrollX= " + getScrollX()
//                + "，rightIconOuterWidth= " + rightIconOuterWidth
//                + "，rightIconPaddingLeft= " + rightIconPaddingLeft
//        );

        //底线的Y轴起点=滑动顶部+整高+底(内)边距
        int lineStartY = getScrollY() + getHeight() - getPaddingBottom();
        LogUtils.d("底线= " + lineStartY
                + "，getScrollY= " + getScrollY()
                + "，getHeight= " + getHeight()
                + "，getPaddingBottom= " + getPaddingBottom()
        );

        //以ET文本基线为准
        Paint.FontMetrics fm = getPaint().getFontMetrics();
        float baseLine = getHeight() / 2.0f - (fm.top + fm.bottom) / 2;

        /** 绘制(左)文本 */
        if (!TextUtils.isEmpty(leftText)) {
            textPaint.setAlpha(255);
            //抗锯齿
            paint.setAntiAlias(true);
            textPaint.setTextSize(getTextSize());
            textPaint.setColor(leftTextColor);
            textPaint.setFakeBoldText(true);
            canvas.drawText(leftText, isRTL() ? endX - textPaint.measureText(leftText) : getScrollX(), baseLine, textPaint);
        }

        /** 绘制(右)图标 */
        if (iconRightBitmaps != null) {
            paint.setAlpha(255);
            paint.setAntiAlias(true);
            //【普通、选中(焦点)、不可用、无效】。
            Bitmap icon = iconRightBitmaps[!isInternalValid() ? 3 : !isEnabled() ? 2 : hasFocus() ? 1 : 0];
            int iconRight = endX + rightIconPaddingLeft + (rightIconOuterWidth - icon.getWidth()) / 2;
            correctIconY(icon);
            //图标顶部=底线+底空隙-图标外高+(图标外高-图标高度)
            //int iconTop = lineStartY - rightIconOuterHeight + (rightIconOuterHeight - icon.getHeight()) / 2;
            int iconTop = (lineStartY - rightIconOuterHeight) / 2;
            canvas.drawBitmap(icon, iconRight, iconTop, paint);
        }

        /** 绘制(清空)图标 */
        if (hasFocus() && showClearButton && !TextUtils.isEmpty(getText()) && isEnabled()) {
            paint.setAlpha(255);
            paint.setAntiAlias(true);
            Bitmap bitmap;
            if (clearMultiColour) {
                bitmap = clearButtonBitmaps[!isInternalValid() ? 3 : !isEnabled() ? 2 : hasFocus() ? 1 : 0];
            } else {
                bitmap = clearButtonBitmaps[0];
            }
            int buttonLeft;
            if (isRTL()) {
                buttonLeft = startX;
            } else {
                buttonLeft = endX - clearIconOuterWidth;
            }
            //int buttonTop = lineStartY - clearIconOuterHeight + (clearIconOuterHeight - bitmap.getHeight()) / 2;
            int buttonTop = (lineStartY - clearIconOuterHeight) / 2;
            canvas.drawBitmap(bitmap, buttonLeft, buttonTop, paint);
        }

        // draw the original things
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getScrollX() > 0 &&
                event.getAction() == MotionEvent.ACTION_DOWN && event.getX() < Density.dp2px(getContext(), 4 * 5) &&
                event.getY() > getHeight() - innerPaddingBottom && event.getY() < getHeight() - innerPaddingBottom) {
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

                default:
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

}
