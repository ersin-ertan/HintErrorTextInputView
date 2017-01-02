package com.github.ersin_ertan.hinterrortextinputview.validator.length;

import android.content.Context;
import android.support.annotation.NonNull;
import com.github.ersin_ertan.hinterrortextinputview.R;
import com.github.ersin_ertan.hinterrortextinputview.validator.AbsValidateable;

/**
 * Created by mms on 12/28/16.
 */

public class MinMaxLength extends AbsValidateable {

  public final int min;
  public final int max;

  private MinMaxLength(int min, int max, CharSequence errorMessage) {
    this.min = min;
    this.max = max;
    this.errorMessage = errorMessage;
  }

  public static MinMaxLength is(@NonNull Context context, int min, int max) {
    return new MinMaxLength(min, max,
        context.getString(R.string.error_min_max_len) + " " + min + " - " + max + " characters");
  }

  public static MinMaxLength is(@NonNull String errorMessage, int min, int max) {
    return new MinMaxLength(min, max, errorMessage);
  }

  @Override public boolean isValid(@NonNull String input) {
    return input.length() >= min && input.length() <= max;
  }
}