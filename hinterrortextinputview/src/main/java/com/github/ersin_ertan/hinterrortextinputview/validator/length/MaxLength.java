package com.github.ersin_ertan.hinterrortextinputview.validator.length;

import android.content.Context;
import android.support.annotation.NonNull;
import com.github.ersin_ertan.hinterrortextinputview.R;
import com.github.ersin_ertan.hinterrortextinputview.validator.AbsValidateable;

/**
 * Created by mms on 12/28/16.
 */

public class MaxLength extends AbsValidateable {

  public final int max;

  private MaxLength(int max, CharSequence errorMessage) {
    this.max = max;
    this.errorMessage = errorMessage;
  }

  public static MaxLength is(@NonNull Context context, int max) {
    return new MaxLength(max,
        context.getString(R.string.error_max_len) + " " + max + " characters");
  }

  public static MaxLength is(@NonNull String errorMessage, int max) {
    return new MaxLength(max, errorMessage);
  }

  @Override public boolean isValid(String input) {
    return input.length() <= max;
  }
}