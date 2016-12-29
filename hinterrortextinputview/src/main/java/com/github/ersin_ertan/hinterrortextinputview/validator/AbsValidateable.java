package com.github.ersin_ertan.hinterrortextinputview.validator;

import android.support.annotation.NonNull;

/**
 * Created by mms on 12/28/16.
 */

public abstract class AbsValidateable implements Validateable {

  protected String errorMessage;

  @Override public abstract boolean isValid(@NonNull String input);

  @Override public String getErrorMessage() {
    return errorMessage;
  }
}
