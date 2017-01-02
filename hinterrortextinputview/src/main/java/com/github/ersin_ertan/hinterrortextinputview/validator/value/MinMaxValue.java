package com.github.ersin_ertan.hinterrortextinputview.validator.value;

import android.content.Context;
import android.support.annotation.NonNull;
import com.github.ersin_ertan.hinterrortextinputview.R;
import com.github.ersin_ertan.hinterrortextinputview.validator.AbsValidateable;

/**
 * Created by mms on 12/28/16.
 */

public class MinMaxValue<T extends Number> extends AbsValidateable {

  public final T min;
  public final T max;

  private MinMaxValue(T min, T max, CharSequence errorMessage) {
    this.min = min;
    this.max = max;
    this.errorMessage = errorMessage;
  }

  public static <T extends Number> MinMaxValue is(@NonNull Context context, T min, T max) {
    return new MinMaxValue<>(min, max,
        context.getString(R.string.error_min_max_len) + " " + min + " - " + max + " characters");
  }

  public static <T extends Number> MinMaxValue is(@NonNull String errorMessage, T min, T max) {
    return new MinMaxValue<>(min, max, errorMessage);
  }

  @Override public boolean isValid(String input) {
    if (min instanceof Byte) {
      return Byte.valueOf(input) >= min.byteValue() && Byte.valueOf(input) <= max.byteValue();
    } else if (min instanceof Double) {
      return Double.valueOf(input) >= min.doubleValue()
          && Double.valueOf(input) <= max.doubleValue();
    } else if (min instanceof Float) {
      return Float.valueOf(input) >= min.floatValue() && Float.valueOf(input) <= max.floatValue();
    } else if (min instanceof Integer) {
      return Integer.valueOf(input) >= min.intValue() && Integer.valueOf(input) <= max.intValue();
    } else if (min instanceof Long) {
      return Long.valueOf(input) >= min.longValue() && Long.valueOf(input) <= max.longValue();
    } else if (min instanceof Short) {
      return Short.valueOf(input) >= min.shortValue() && Short.valueOf(input) <= max.shortValue();
    }
    return false;
  }
}