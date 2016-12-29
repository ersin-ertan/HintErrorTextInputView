package com.github.ersin_ertan.hinterrortextinputview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.transition.TransitionManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import com.github.ersin_ertan.hinterrortextinputview.validator.Validateable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mms on 12/28/16.
 */

public class HintErrorTextInputView extends TextInputLayout {

  private int errorTextAppearance;

  // TODO: 12/28/16 enable and disable error to have smaller sized views

  private static final int AVG_NUM_VALIDATORS = 2;
  List<Validateable> validators;
  boolean isShowingError = false;
  private TextInputEditText textInputEditText;
  private String hint;
  boolean isErrorEnabled = false;

  public HintErrorTextInputView(Context context) {
    super(context);
    textInputEditText = new TextInputEditText(getContext());
    //setAttributeValues(attrs);
    initEditText(textInputEditText);
  }

  public HintErrorTextInputView(Context context, AttributeSet attrs) {
    super(context, attrs);
    textInputEditText = new TextInputEditText(getContext());
    setAttributeValues(attrs);
    initEditText(textInputEditText);
  }

  public HintErrorTextInputView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    textInputEditText = new TextInputEditText(getContext());
    setAttributeValues(attrs);
    initEditText(textInputEditText);
  }

  private void initEditText(TextInputEditText textInputEditText) {

    //Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/AvenirLTStd-Roman.otf");
    //textInputEditText.setTypeface(tf);
    //setTypeface(tf);
    if (isErrorEnabled()) {
      isErrorEnabled = true;
      setErrorEnabled(false);
    }

    textInputEditText.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      @Override public void afterTextChanged(Editable s) {
        if (isShowingError && isErrorEnabled) {
          isShowingError = false;
          TransitionManager.beginDelayedTransition(HintErrorTextInputView.this);
          //setError(""); // delete me
          setErrorEnabled(false);
        }
      }
    });

    addView(textInputEditText, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
        LayoutParams.WRAP_CONTENT));
  }

  private void setAttributeValues(@Nullable AttributeSet attrs) {
    if (attrs != null) {

      TypedArray typedArray =
          getContext().obtainStyledAttributes(attrs, R.styleable.HintErrorTextInputView);
      if (typedArray != null) {

        // text appearance will overwrite set values like text color. Will doing it first nullify?
        textInputEditText.setTextAppearance(getContext(),
            typedArray.getResourceId(R.styleable.HintErrorTextInputView_android_textAppearance,
                android.R.style.TextAppearance_DeviceDefault_Widget_EditText));

        setErrorTextAppearance(
            typedArray.getResourceId(R.styleable.HintErrorTextInputView_errorTextAppearance,
                android.support.design.R.styleable.TextInputLayout_errorTextAppearance));

        // is this the floating label?
        setHintTextAppearance(
            typedArray.getResourceId(R.styleable.HintErrorTextInputView_floatingLabelTextAppearance,
                android.support.design.R.styleable.TextInputLayout_hintTextAppearance));

        if (typedArray.getText(R.styleable.HintErrorTextInputView_android_hint) != null) {
          hint = typedArray.getText(R.styleable.HintErrorTextInputView_android_hint).toString();
          setHint(hint);
        }
        //textInputEditText.setInputType(typedArray.getInteger(1, InputType.TYPE_CLASS_TEXT));
        textInputEditText.setInputType(
            typedArray.getInteger(R.styleable.HintErrorTextInputView_android_inputType,
                textInputEditText.getInputType()));

        textInputEditText.setTextColor(
            typedArray.getInteger(R.styleable.HintErrorTextInputView_android_textColor,
                textInputEditText.getCurrentTextColor()));

        if (!typedArray.getBoolean(R.styleable.HintErrorTextInputView_showUnderline, true)) {
          textInputEditText.setBackgroundResource(android.R.color.transparent);
        }
        textInputEditText.setImeOptions(
            typedArray.getInteger(R.styleable.HintErrorTextInputView_android_imeOptions,
                EditorInfo.IME_ACTION_NEXT));

        // will set the float value thus allows for any typedvalue complex unit
        float textSize =
            typedArray.getDimension(R.styleable.HintErrorTextInputView_android_textSize,
                textInputEditText.getTextSize());
        textInputEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        //textInputEditText.getTextSize() / getResources().getDisplayMetrics().scaledDensity);

        if (!typedArray.getBoolean(R.styleable.HintErrorTextInputView_showHintAndLabel, false)) {
          // prevents the dual hints edittext/floating label, to change color, use style
          textInputEditText.setHintTextColor(
              ContextCompat.getColor(getContext(), android.R.color.transparent));
        } else {
          setHintAnimationEnabled(false); // else overlap on edittext unfocus with hint/label
        }

        typedArray.recycle();
      }
    }
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    textInputEditText.requestFocus();
    return super.onTouchEvent(event);
  }

  /**
   * @return HintErrorTextInputView
   *
   * Allows for chained validations, check in the order of initialization.
   * Thus if you validate that input is not empty, then min, only empty will trigger.
   */
  public HintErrorTextInputView validateThat(Validateable validateable) {
    if (validators == null) {
      validators = new ArrayList<>(AVG_NUM_VALIDATORS);
    }
    validators.add(validateable);
    return this;
  }

  public boolean isValid() {
    if (validators != null && !validators.isEmpty()) {
      String input = textInputEditText.getText().toString();
      for (Validateable v : validators) {
        if (!v.isValid(input)) return setErrorText(v.getErrorMessage());
      }
    }
    return true;
  }

  private boolean setErrorText(@NonNull String errorRes) {
    if (isErrorEnabled) {
      setErrorEnabled(true);
      isShowingError = true;
      TransitionManager.beginDelayedTransition(this);
      if (hint != null) {
        setError(hint + " " + errorRes);
      }
      textInputEditText.requestFocus(); // add ability to show or hide keyboard
    }
    return false;
  }
}
