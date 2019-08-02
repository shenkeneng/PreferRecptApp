package com.frxs.common_base.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.frxs.common_base.R;
import com.frxs.common_base.utils.CheckUtils;
import com.frxs.common_base.utils.ImeUtils;
import com.frxs.common_base.utils.LogUtils;
import com.frxs.common_base.utils.MathUtils;
import com.frxs.common_base.utils.SystemUtils;


/**
 * @author cate 2014-12-18 上午10:31:00
 */

public class CountEditText extends LinearLayout implements OnClickListener {

    private int mCount = 0;

    private EditText mEdit;

    private ImageView mMin;

    private ImageView mAdd;

    private onCountChangeListener mOnCountChangeListener;

    private int maxCount = 999999;

    @SuppressLint("NewApi")
    public CountEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(attrs);
    }

    public CountEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public CountEditText(Context context) {
        super(context);
    }

    private void initView(AttributeSet attrs) {
        LinearLayout view = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_countedittext, null);
        mMin = (ImageView) view.findViewById(R.id.count_sub);
        mAdd = (ImageView) view.findViewById(R.id.count_add);
        mEdit = (EditText) view.findViewById(R.id.count_edit);
        mEdit.setInputType(EditorInfo.TYPE_CLASS_PHONE);//设置数字键盘显示
        mEdit.setText(MathUtils.doubleTrans(mCount));

        mMin.setOnClickListener(this);
        mAdd.setOnClickListener(this);
        this.addView(view);
        if (SystemUtils.getSDKVersion() < 16) {
            // mMin.setBackgroundDrawable(ViewUtils.getStateDrawable(getContext(),
            // normal, active, disable));
        } else {
            // mMin.setBackground(ViewUtils.getStateDrawable(getContext(),
            // normal, active, disable));
        }

        mEdit.addTextChangedListener(new TextWatcher() {
                                         @Override
                                         public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                             String str = s.toString().trim();
                                         }

                                         @Override
                                         public void onTextChanged(CharSequence s, int start, int before, int count) {
                                             String str = s.toString().trim();
                                             if (CheckUtils.strIsInteger(str)){
                                                 if (Integer.valueOf(str) > maxCount) {
                                                     mEdit.setText(String.valueOf(maxCount));
                                                 } else if (Integer.valueOf(str) < 0) {
                                                     mEdit.setText(String.valueOf(0));
                                                 }
                                             } else {
                                                 if (str.length() <= 0) {
                                                     mEdit.setText("0");
                                                 } else {
                                                     String s1 = str.substring(0, start);
                                                     String s2 = str.substring((start + count - 1), str.length() - 1);
                                                     StringBuffer stringBuffer = new StringBuffer(s1);
                                                     stringBuffer.append(s2);
                                                     mEdit.setText(stringBuffer);
                                                 }
                                                 if (str.startsWith("0") && str.length() > 1) {
                                                     String substring = str.substring(1, str.length());
                                                     mEdit.setText(substring);
                                                 }
                                                 if (s.toString().contains(" ")) {
                                                     mEdit.setText(str.trim());
                                                 }
                                             }
                                         }

                                         @Override
                                         public void afterTextChanged(Editable s) {
                                             String value = mEdit.getText().toString().trim();
                                             mEdit.setSelection(value.length());
                                             if (CheckUtils.strIsInteger(value)) {
                                                 mCount = Integer.valueOf(value);
                                             } else {
                                                 mCount = 0;
                                             }
                                             LogUtils.e("CountEditText afterTextChanged getText()=" + mEdit.getText() + " s=" + s + " Count=" + mCount);
                                             if (mOnCountChangeListener != null) {
                                                 mOnCountChangeListener.onCountEdit(mCount);
                                             }

                                             // 直接修改数量时判断当前减少数量的标识状态
                                             if (getCount() <= 0) {
                                                 mMin.setEnabled(false);
                                                 mMin.setImageResource(R.mipmap.icon_subtract_disable);
                                             } else {
                                                 mMin.setEnabled(true);
                                                 mMin.setImageResource(R.mipmap.icon_subtract_enable);
                                             }

                                             // 直接修改数量时判断当前增加数量的标识状态
                                             if (getCount() < getMaxCount()) {
                                                 mAdd.setEnabled(true);
                                                 mAdd.setImageResource(R.mipmap.icon_red_cross_enable);
                                             } else {
                                                 mAdd.setEnabled(false);
                                                 mAdd.setImageResource(R.mipmap.icon_red_cross_disable);
                                             }
                                         }
                                     }
        );
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.count_sub) {
            if (getCount() > 0) {
                mCount = (int) MathUtils.sub(mCount, 1);
                if (mCount <= 0) {
                    mCount = 0;
                    mMin.setEnabled(false);
                    mMin.setImageResource(R.mipmap.icon_subtract_disable);
                }
                if (mCount < getMaxCount()) {
                    mAdd.setEnabled(true);
                    mAdd.setImageResource(R.mipmap.icon_red_cross_enable);
                } else {
                    mCount = getMaxCount();
                }
                mEdit.setText(MathUtils.doubleTrans(mCount));
                if (mOnCountChangeListener != null) {
                    mOnCountChangeListener.onCountSub(mCount);
                }
            }
        } else if (i == R.id.count_add) {
            if (getCount() < maxCount) {
                mCount = (int) MathUtils.add(mCount, 1);
                if (mCount > 0) {
                    mMin.setEnabled(true);
                    mMin.setImageResource(R.mipmap.icon_subtract_enable);
                }
                if (mCount >= maxCount) {
                    mCount = maxCount;
                    mAdd.setEnabled(false);
                    mAdd.setImageResource(R.mipmap.icon_red_cross_disable);
                }
                mEdit.setText(MathUtils.doubleTrans(mCount));
                if (mOnCountChangeListener != null) {
                    mOnCountChangeListener.onCountAdd(mCount);
                }
            }
        } else {
        }
    }

    public onCountChangeListener getOnCountChangeListener() {
        return mOnCountChangeListener;
    }

    public void setOnCountChangeListener(onCountChangeListener mOnCountChangeListener) {
        this.mOnCountChangeListener = mOnCountChangeListener;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int mCount) {
        this.mCount = mCount;
        if (mCount <= 0) {
            mCount = 0;
            mMin.setEnabled(false);
            mMin.setImageResource(R.mipmap.icon_subtract_disable);

            mEdit.setText(MathUtils.doubleTrans(mCount));
        } else if (0 < mCount && mCount < getMaxCount()) {
            mMin.setEnabled(true);
            mMin.setImageResource(R.mipmap.icon_subtract_enable);
            mAdd.setEnabled(true);
            mAdd.setImageResource(R.mipmap.icon_red_cross_enable);

            mEdit.setText(MathUtils.doubleTrans(mCount));
        } else {
            // 当前购物车数量与最大库存相等时显示最大库存，且“+”不可操作“-”号可操作|陈铁
            mEdit.setText(MathUtils.doubleTrans(maxCount));
            mMin.setEnabled(true);
            mMin.setImageResource(R.mipmap.icon_subtract_enable);
            mAdd.setEnabled(false);
            mAdd.setImageResource(R.mipmap.icon_red_cross_disable);
        }

    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
        mCount = 0;
        mEdit.setText(MathUtils.doubleTrans(mCount));
        mMin.setEnabled(false);
        mMin.setImageResource(R.mipmap.icon_subtract_disable);
        if (maxCount > 0) {
            mAdd.setEnabled(true);
            mAdd.setImageResource(R.mipmap.icon_red_cross_enable);
        } else {
            mAdd.setEnabled(false);
            mAdd.setImageResource(R.mipmap.icon_red_cross_disable);
        }

    }

    public interface onCountChangeListener {

        void onCountAdd(double count);

        void onCountSub(double count);

        void onCountEdit(double count);
    }

    public EditText getmEdit (){
        return mEdit;
    }

}
