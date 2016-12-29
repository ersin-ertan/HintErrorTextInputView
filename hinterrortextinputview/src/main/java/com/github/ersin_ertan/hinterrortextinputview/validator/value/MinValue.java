package com.github.ersin_ertan.hinterrortextinputview.validator.value;

import android.content.Context;
import android.support.annotation.NonNull;
import com.github.ersin_ertan.hinterrortextinputview.R;
import com.github.ersin_ertan.hinterrortextinputview.validator.AbsValidateable;

/**
 * Created by mms on 12/28/16.
 */

public class MinValue<T extends Number> extends AbsValidateable {

  public final T min;

  private MinValue(T min, String errorMessage) {
    this.min = min;
    this.errorMessage = errorMessage;
  }

  public static <T extends Number> MinValue is(@NonNull Context context, @NonNull T min) {
    return new MinValue<>(min, context.getString(R.string.error_min_val) + " " + min);
  }

  public static <T extends Number> MinValue is(@NonNull String errorMessage, @NonNull T min) {
    return new MinValue<>(min, errorMessage);
  }

  @Override public boolean isValid(@NonNull String input) throws NumberFormatException {
    if (min instanceof Byte) {
      return Byte.valueOf(input).compareTo(min.byteValue()) >= 0;
    } else if (min instanceof Double) {
      return Double.valueOf(input).compareTo(min.doubleValue()) >= 0;
    } else if (min instanceof Float) {
      return Float.valueOf(input).compareTo(min.floatValue()) >= 0;
    } else if (min instanceof Integer) {
      return Integer.valueOf(input).compareTo(min.intValue()) >= 0;
    } else if (min instanceof Long) {
      return Long.valueOf(input).compareTo(min.longValue()) >= 0;
    } else if (min instanceof Short) {
      return Short.valueOf(input).compareTo(min.shortValue()) >= 0;
    }
    return false;
  }
}