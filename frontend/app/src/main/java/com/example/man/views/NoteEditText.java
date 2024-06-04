package com.example.man.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.man.NoteActivity;
import com.example.man.NoteContentActivity;
import com.example.man.adapters.NoteContentAdapter;

public class NoteEditText extends androidx.appcompat.widget.AppCompatEditText {

    private TextView textView;
    private boolean isTextViewVisible = false;
    private boolean isDetached = false;
    private NoteContentAdapter.OnItemViewClickListener mListener;
    private ViewTreeObserver.OnGlobalLayoutListener keyboardListener;



    public NoteEditText(Context context) {
        super(context);
        init(context);
    }

    public NoteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NoteEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        textView = new TextView(context);
        textView.setVisibility(GONE);

        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
        textView.setTextSize(getTextSize() / getResources().getDisplayMetrics().density);
        textView.setTextColor(getCurrentTextColor());
        textView.setTypeface(getTypeface());
        textView.setGravity(getGravity());

        // 设置EditText支持回车换行
        setInputType(getInputType() | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
        setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        setSingleLine(false);
        setMaxLines(Integer.MAX_VALUE);

        this.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (!hasFocus) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    if (!getText().toString().isEmpty()){
                        switchToTextView();
                    }
                }
                else {
                    mListener.onItemViewClick(NoteEditText.this);
                    imm.showSoftInput(v, 0);
                }
            }
        });

        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToEditText();
                requestFocus();
            }
        });
    }

    private void switchToTextView() {
        if (!isTextViewVisible) {
            textView.setText(getText());
            textView.setVisibility(VISIBLE);
            this.setVisibility(GONE);
            isTextViewVisible = true;
        }
    }

    public void switchToEditText() {
        if (isTextViewVisible) {
            this.setVisibility(VISIBLE);
            textView.setVisibility(GONE);
            isTextViewVisible = false;
            this.requestFocus();
        }
    }

    public void setListener(NoteContentAdapter.OnItemViewClickListener listener){
        this.mListener = listener;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        // 只在 textView 没有父视图时添加
        if (textView.getParent() == null && getParent() instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) getParent();
            int index = parent.indexOfChild(this);
            parent.addView(textView, index + 1);
        }
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            clearFocus();
        }
        return super.onKeyPreIme(keyCode, event);
    }
}