package com.github.ersin_ertan.hinterrortextinputview;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.transition.TransitionManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import com.github.ersin_ertan.hinterrortextinputview.validator.Validateable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by mms on 12/28/16.
 */

public class HintErrorTextInputView extends TextInputLayout {

  public static final long TIMED_ERROR_DURATION_DEFAULT = TimeUnit.SECONDS.toMillis(4);
  private static final int AVG_NUM_VALIDATORS = 2;
  private static final String EMPTY = "";
  // TODO: 12/28/16 enable and disable error to have smaller sized views
  List<Validateable> validators;
  private boolean isShowingError = false;
  private boolean usingError = true;
  private TextInputEditText textInputEditText;
  private String timedErrorText;
  @Nullable private KeyListener tempKeyListener = null;
  private int tempInputType = InputType.TYPE_NULL;
  private boolean hideErrorOnTextChanged = true;
  private int textAppearanceRes;
  private TextWatcher textWatcher; // does this need to be a field?
  private List<WeakReference<IsValidListener>> isValidListenerList;
  private TimedErrorRunnable timedErrorRunnable;

  public HintErrorTextInputView(Context context) {
    this(context, null);
  }

  public HintErrorTextInputView(Context context, AttributeSet attrs) {
    this(context, attrs, -1);
  }

  public HintErrorTextInputView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    textInputEditText = new TextInputEditText(getContext());
    initEditText(textInputEditText);
    setAttributeValues(attrs);
    setSaveEnabled(true);
  }

  private void initEditText(TextInputEditText textInputEditText) {

    //Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/AvenirLTStd-Roman.otf");
    //textInputEditText.setTypeface(tf);
    //setTypeface(tf);
    if (textWatcher == null) {
      textWatcher = new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override public void afterTextChanged(Editable s) {
          if (isShowingError && usingError && hideErrorOnTextChanged) hideError();
          updateIsValidListeners(isValidNoError());
        }
      };
    }
    textInputEditText.addTextChangedListener(textWatcher);

    addView(textInputEditText, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT));
  }

  // should we be using weak references for listeners?
  private void updateIsValidListeners(boolean isValid) {
    if (isValidListenerList != null && !isValidListenerList.isEmpty()) {
      for (WeakReference<IsValidListener> ivl : isValidListenerList) {
        ivl.get().isValid(isValid);
      }
    }
  }

  public void addIsValidListener(IsValidListener isValidListener) {
    if (isValidListenerList == null) isValidListenerList = new ArrayList<>(2);
    isValidListenerList.add(new WeakReference<>(isValidListener));
  }

  private void setAttributeValues(@Nullable AttributeSet attrs) {
    if (attrs != null) {

      TypedArray typedArray =
          getContext().obtainStyledAttributes(attrs, R.styleable.HintErrorTextInputView);
      if (typedArray != null) {

        setTextAppearance(
            typedArray.getResourceId(R.styleable.HintErrorTextInputView_android_textAppearance,
                android.R.style.TextAppearance_DeviceDefault_Widget_EditText));

        setErrorTextAppearance(
            typedArray.getResourceId(R.styleable.HintErrorTextInputView_errorTextAppearance,
                android.support.design.R.styleable.TextInputLayout_errorTextAppearance));

        setHintTextAppearance(
            typedArray.getResourceId(R.styleable.HintErrorTextInputView_floatingLabelTextAppearance,
                android.support.design.R.styleable.TextInputLayout_hintTextAppearance));

        if (typedArray.getText(R.styleable.HintErrorTextInputView_android_hint) != null) {
          setHint(typedArray.getText(R.styleable.HintErrorTextInputView_android_hint));
        }

        tempInputType = typedArray.getInteger(R.styleable.HintErrorTextInputView_android_inputType,
            textInputEditText.getInputType());
        textInputEditText.setInputType(tempInputType);

        textInputEditText.setTextColor(
            typedArray.getInteger(R.styleable.HintErrorTextInputView_android_textColor,
                textInputEditText.getCurrentTextColor()));

        if (!typedArray.getBoolean(R.styleable.HintErrorTextInputView_showUnderline, true)) {
          textInputEditText.setBackgroundResource(android.R.color.transparent);
        }
        textInputEditText.setImeOptions(
            typedArray.getInteger(R.styleable.HintErrorTextInputView_android_imeOptions,
                EditorInfo.IME_ACTION_NEXT));

        float textSize =
            typedArray.getDimension(R.styleable.HintErrorTextInputView_android_textSize,
                textInputEditText.getTextSize());
        textInputEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        //if (!typedArray.getBoolean(R.styleable.HintErrorTextInputView_showHintAndLabel, false)) {
        // prevents the dual hints edittext/floating label, to change color, use style
        textInputEditText.setHintTextColor(
            ContextCompat.getColor(getContext(), android.R.color.transparent));
        //}
        //else {
        //    setHintAnimationEnabled(false); // else overlap on edittext unfocus with hint/label
        //  }
        setErrorEnabled(false);
        if (!typedArray.getBoolean(R.styleable.HintErrorTextInputView_errorEnabled, true)) {
          usingError = false;
        }

        hideErrorOnTextChanged =
            typedArray.getBoolean(R.styleable.HintErrorTextInputView_hideErrorOnTextChanged, true);

        CharSequence text = typedArray.getText(R.styleable.HintErrorTextInputView_android_text);
        if (text != null) {
          textInputEditText.setText(text);
        }
      }

      setEditable(typedArray.getBoolean(R.styleable.HintErrorTextInputView_android_editable, true));
      typedArray.recycle();
    }
  }

  public void setEditable(boolean isEditable) {
    if (isEditable) {
      textInputEditText.setFocusable(true);
      textInputEditText.setCursorVisible(true);
      textInputEditText.setTextIsSelectable(true);
      textInputEditText.setFocusableInTouchMode(true);
      if (tempInputType != -1) textInputEditText.setInputType(tempInputType);
      if (tempKeyListener != null) {
        textInputEditText.setKeyListener(tempKeyListener);
      }
    } else {
      textInputEditText.setTextIsSelectable(true);
      textInputEditText.setFocusableInTouchMode(true);
      textInputEditText.setCursorVisible(false);
      tempInputType = textInputEditText.getInputType();
      textInputEditText.setInputType(InputType.TYPE_NULL);
      tempKeyListener = textInputEditText.getKeyListener();
      textInputEditText.setKeyListener(null);
      ((InputMethodManager) getContext().getSystemService(
          Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(textInputEditText.getWindowToken(),
          0);
    }
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    textInputEditText.onTouchEvent(event);
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

  public boolean isValidNoError() {
    return isValid(false);
  }

  public boolean isValid() {
    return isValid(true);
  }

  private boolean isValid(boolean shouldShowError) {
    if (validators != null && !validators.isEmpty()) {
      String input = textInputEditText.getText().toString();
      for (Validateable v : validators) {
        if (!v.isValid(input)) {
          showError(v, shouldShowError);
          return false;
        }
      }
    }
    return true;
  }

  private void showError(Validateable v, boolean shouldShowError) {
    if (usingError && shouldShowError) {
      TransitionManager.beginDelayedTransition(this);
      setErrorEnabled(true);
      CharSequence hint = getHint();
      if (hint != null) {
        isShowingError = true;
        setError(hint + " " + v.getErrorMessage());
      } else {
        CharSequence error = v.getErrorMessage();
        if (error != null && error.length() > 0) {
          isShowingError = true;
          CharSequence firstLetter = Character.toString(error.charAt(0)).toUpperCase();
          if (error.length() == 1) {
            setError(firstLetter);
          } else {
            setError(firstLetter + error.subSequence(1, error.length()).toString());
          }
        } else {
          isShowingError = false;
        }
      }
      textInputEditText.requestFocus();
    }
  }

  public void showTimedError(@NonNull final String errorText, final long durationMillis) {
    if (usingError) {
      timedErrorRunnable = new TimedErrorRunnable(errorText, durationMillis);
      postDelayed(timedErrorRunnable, durationMillis);
    }
  }

  public void showTimedError(@NonNull final String errorText) {
    showTimedError(errorText, TIMED_ERROR_DURATION_DEFAULT);
  }

  @Override protected void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
  }

  public void hideError() {
    setError(EMPTY);
    setErrorEnabled(false);
    isShowingError = false;
  }

  public boolean getIsShowingError() {
    return isShowingError;
  }

  public boolean getHideErrorOnTextChanged() {
    return hideErrorOnTextChanged;
  }

  public void setText(String text) {
    textInputEditText.setText(text);
  }

  public void setTextSize(float size) {
    setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
  }

  public void setTextSize(int unit, float size) {
    textInputEditText.setTextSize(unit, size);
    // will not reposition the text in this view until another input
  }

  public void setTextAppearance(@StyleRes int resId) {
    textAppearanceRes = resId;
    if (textInputEditText != null) {
      TextViewCompat.setTextAppearance(textInputEditText, resId);
    }
  }

  public int getInputType() {
    if (tempInputType != InputType.TYPE_NULL) {
      return tempInputType;
    } else {
      return textInputEditText.getInputType();
    }
  }

  public void setInputType(int inputType) {
    tempInputType = inputType;
    textInputEditText.setInputType(inputType);
  }

  public List<Validateable> getValidators() {
    if (validators.isEmpty()) return new ArrayList<>(0);
    return new ArrayList<>(validators);
  }

  @Nullable public KeyListener getKeyListener() {
    return textInputEditText.getKeyListener() != null ? textInputEditText.getKeyListener()
        : tempKeyListener;
  }

  public void setKeyListener(KeyListener keyListener) {
    tempKeyListener = keyListener;
    textInputEditText.setKeyListener(keyListener);
  }

  @Override public Parcelable onSaveInstanceState() {

    Parcelable superState = super.onSaveInstanceState();
    SavedState ss = new SavedState(superState);

    ss.currentText = textInputEditText.getText().toString();
    ss.inputType = tempInputType;
    ss.isShowingError = isShowingError ? 1 : 0;
    ss.usingError = usingError ? 1 : 0;
    ss.hideErrorOnTextChanged = hideErrorOnTextChanged ? 1 : 0;
    if (timedErrorRunnable != null && timedErrorRunnable.endTime > SystemClock.uptimeMillis()) {
      ss.timedErrorDuration = timedErrorRunnable.endTime;
      ss.timedErrorText = timedErrorText;
      timedErrorRunnable = null;
    }

    return ss;
  }

  @Override public void onRestoreInstanceState(Parcelable state) {
    if (!(state instanceof SavedState)) {
      super.onRestoreInstanceState(state);
      return;
    }

    final SavedState ss = (SavedState) state;
    super.onRestoreInstanceState(ss.getSuperState());

    if (ss.currentText != null) textInputEditText.setText(ss.currentText);

    tempInputType = ss.inputType;
    isShowingError = ss.isShowingError == 1;
    usingError = ss.usingError == 1;
    hideErrorOnTextChanged = ss.hideErrorOnTextChanged == 1;
    if (usingError && ss.timedErrorDuration > 0 && ss.timedErrorText != null) {
      //showTimedError(ss.timedErrorText, ss.timedErrorDuration);
      // FIXME: 2/3/17 A newly created timed error will run, however it will not hide the error
      // after rotation, thus to keep state predictable, disable this feature
      // Rotation will imply timed events are not accounted for until this is fixed
      timedErrorText = null;
      hideError();
    }
  }

  public interface IsValidListener {
    void isValid(boolean isValid);
  }

  static class SavedState extends BaseSavedState {
    public static final Parcelable.Creator<SavedState> CREATOR =
        new Parcelable.Creator<SavedState>() {
          public SavedState createFromParcel(Parcel in) {
            return new SavedState(in);
          }

          public SavedState[] newArray(int size) {
            return new SavedState[size];
          }
        };
    String currentText;
    int inputType;
    int isShowingError;
    int usingError;
    int hideErrorOnTextChanged;
    long timedErrorDuration;
    String timedErrorText;

    SavedState(Parcelable superState) {
      super(superState);
    }

    private SavedState(Parcel in) {
      super(in);
      this.currentText = in.readString();
      this.inputType = in.readInt();
      this.isShowingError = in.readInt();
      this.usingError = in.readInt();
      this.hideErrorOnTextChanged = in.readInt();
      this.timedErrorDuration = in.readLong();
      this.timedErrorText = in.readString();
    }

    @Override public void writeToParcel(Parcel out, int flags) {
      super.writeToParcel(out, flags);
      out.writeString(this.currentText);
      out.writeInt(this.inputType);
      out.writeInt(this.isShowingError);
      out.writeInt(this.usingError);
      out.writeInt(this.hideErrorOnTextChanged);
      out.writeLong(this.timedErrorDuration);
      out.writeString(this.timedErrorText);
    }
  }

  private class TimedErrorRunnable implements Runnable {

    final long endTime;
    final String errorText;

    TimedErrorRunnable(@NonNull final String errorText, final long durationMillis) {
      endTime = SystemClock.uptimeMillis() + durationMillis;
      TransitionManager.beginDelayedTransition(HintErrorTextInputView.this);
      setErrorEnabled(true);
      setError(timedErrorText = this.errorText = errorText);
      isShowingError = true;
    }

    @Override public void run() {
      // another error may be triggered thus we do not want to hide that error
      if (isErrorEnabled() && isShowingError && errorText != null && errorText.equals(getError())) {
        HintErrorTextInputView.this.hideError();
        timedErrorText = null;
        timedErrorRunnable = null;
      }
    }
  }
}
