package com.github.ersin_ertan.hinterrortextinputview.validator.value;

import android.content.Context;
import android.support.annotation.NonNull;
import com.github.ersin_ertan.hinterrortextinputview.R;
import com.github.ersin_ertan.hinterrortextinputview.validator.AbsValidateable;

/**
 * Created by mms on 12/28/16.
 */

public class MaxValue<T extends Number> extends AbsValidateable {

  public final T max;

  private MaxValue(T max, String errorMessage) {
    this.max = max;
    this.errorMessage = errorMessage;
  }

  public static <T extends Number> MaxValue is(@NonNull Context context, @NonNull T max) {
    return new MaxValue<>(max, context.getString(R.string.error_max_val) + " " + max);
  }

  public static <T extends Number> MaxValue is(@NonNull String errorMessage, @NonNull T max) {
    return new MaxValue<>(max, errorMessage);
  }

  @Override public boolean isValid(@NonNull String input) throws NumberFormatException {
    if (max instanceof Byte) {
      return Byte.valueOf(input).compareTo(max.byteValue()) <= 0;
    } else if (max instanceof Double) {
      return Double.valueOf(input).compareTo(max.doubleValue()) <= 0;
    } else if (max instanceof Float) {
      return Float.valueOf(input).compareTo(max.floatValue()) <= 0;
    } else if (max instanceof Integer) {
      return Integer.valueOf(input).compareTo(max.intValue()) <= 0;
    } else if (max instanceof Long) {
      return Long.valueOf(input).compareTo(max.longValue()) <= 0;
    } else if (max instanceof Short) {
      return Short.valueOf(input).compareTo(max.shortValue()) <= 0;
    }
    return false;
  }
}