package com.github.ersin_ertan.hinterrortextinputview.validator.length;

import android.content.Context;
import android.support.annotation.NonNull;
import com.github.ersin_ertan.hinterrortextinputview.R;
import com.github.ersin_ertan.hinterrortextinputview.validator.AbsValidateable;

/**
 * Created by mms on 12/28/16.
 */

public class MinLength extends AbsValidateable {

  public final int min;

  private MinLength(int min, CharSequence errorMessage) {
    this.min = min;
    this.errorMessage = errorMessage;
  }

  public static MinLength is(@NonNull Context context, int min) {
    return new MinLength(min,
        context.getString(R.string.error_min_len) + " " + min + " characters");
  }

  public static MinLength is(@NonNull String errorMessage, int min) {
    return new MinLength(min, errorMessage);
  }

  @Override public boolean isValid(@NonNull String input) {
    return input.length() >= min;
  }
}