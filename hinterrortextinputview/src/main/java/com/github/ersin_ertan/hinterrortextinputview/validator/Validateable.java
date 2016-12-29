package com.github.ersin_ertan.hinterrortextinputview.validator;

import android.support.annotation.NonNull;

/**
 * Created by mms on 12/27/16.
 */
public interface Validateable {
  boolean isValid(@NonNull String input);

  String getErrorMessage();
}
