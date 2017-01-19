package com.github.ersin_ertan.hinterrortextinputview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.transition.TransitionManager;
import android.support.v4.content.ContextCompat;
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
import java.util.Collections;
import java.util.List;

/**
 * Created by mms on 12/28/16.
 */

public class HintErrorTextInputView extends TextInputLayout {

  private static final int AVG_NUM_VALIDATORS = 2;
  private static final String EMPTY = "";
  // TODO: 12/28/16 enable and disable error to have smaller sized views
  List<Validateable> validators;
  private boolean isShowingError = false;
  private boolean usingError = true;
  private TextInputEditText textInputEditText;
  @Nullable private KeyListener tempKeyListener = null;
  private int tempInputType = InputType.TYPE_NULL;
  private boolean hideErrorOnTextChanged = true;
  private TextWatcher textWatcher; // does this need to be a field?
  private List<WeakReference<IsValidListener>> isValidListenerList;

  public HintErrorTextInputView(Context context) {
    super(context);
    textInputEditText = new TextInputEditText(getContext());
    initEditText(textInputEditText);
    setSaveEnabled(true);
  }

  public HintErrorTextInputView(Context context, AttributeSet attrs) {
    super(context, attrs);
    textInputEditText = new TextInputEditText(getContext());
    initEditText(textInputEditText);
    setAttributeValues(attrs);
    setSaveEnabled(true);
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
        LayoutParams.WRAP_CONTENT));
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

        textInputEditText.setTextAppearance(getContext(),
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

  private boolean isValid(boolean showError) {
    if (validators != null && !validators.isEmpty()) {
      String input = textInputEditText.getText().toString();
      for (Validateable v : validators) {
        if (!v.isValid(input)) {
          if (usingError && showError) {
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
          return false;
        }
      }
    }
    return true;
  }

  public void hideError() {
    setError(EMPTY);
    isShowingError = false;
    setErrorEnabled(false);
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
    if (validators.isEmpty()) return Collections.EMPTY_LIST;
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

    ss.inputType = tempInputType;
    ss.isShowingError = isShowingError ? 1 : 0;
    ss.usingError = usingError ? 1 : 0;
    ss.hideErrorOnTextChanged = hideErrorOnTextChanged ? 1 : 0;

    return ss;
  }

  @Override public void onRestoreInstanceState(Parcelable state) {
    if (!(state instanceof SavedState)) {
      super.onRestoreInstanceState(state);
      return;
    }

    SavedState ss = (SavedState) state;
    super.onRestoreInstanceState(ss.getSuperState());

    tempInputType = ss.inputType;
    isShowingError = ss.isShowingError == 1;
    usingError = ss.usingError == 1;
    hideErrorOnTextChanged = ss.hideErrorOnTextChanged == 1;
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
    int inputType;
    int isShowingError;
    int usingError;
    int hideErrorOnTextChanged;

    SavedState(Parcelable superState) {
      super(superState);
    }

    private SavedState(Parcel in) {
      super(in);
      this.inputType = in.readInt();
      this.isShowingError = in.readInt();
      this.usingError = in.readInt();
      this.hideErrorOnTextChanged = in.readInt();
    }

    @Override public void writeToParcel(Parcel out, int flags) {
      super.writeToParcel(out, flags);
      out.writeInt(this.inputType);
      out.writeInt(this.isShowingError);
      out.writeInt(this.usingError);
      out.writeInt(this.hideErrorOnTextChanged);
    }
  }
}
