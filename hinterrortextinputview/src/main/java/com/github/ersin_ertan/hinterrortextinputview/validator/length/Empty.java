package com.github.ersin_ertan.hinterrortextinputview.validator.length;

import android.content.Context;
import android.support.annotation.NonNull;
import com.github.ersin_ertan.hinterrortextinputview.R;
import com.github.ersin_ertan.hinterrortextinputview.validator.AbsValidateable;

/**
 * Created by mms on 12/28/16.
 */

public class Empty extends AbsValidateable {

  private Empty(CharSequence errorMessage) {
    this.errorMessage = errorMessage;
  }

  public static Empty not(@NonNull Context context) {
    return new Empty(context.getString(R.string.error_empty_len));
  }

  public static Empty not(@NonNull CharSequence errorMessage) {
    return new Empty(errorMessage);
  }

  @Override public boolean isValid(String input) {
    return !input.isEmpty();
  }
}