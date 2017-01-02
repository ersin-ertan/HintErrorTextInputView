package com.github.ersin_ertan.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import com.github.ersin_ertan.hinterrortextinputview.HintErrorTextInputView;
import com.github.ersin_ertan.hinterrortextinputview.validator.length.MaxLength;

public class MainActivity extends AppCompatActivity {

  HintErrorTextInputView hint1;

  Button button;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    hint1 = (HintErrorTextInputView) findViewById(R.id.hint1);
    hint1.validateThat(MaxLength.is(this, 2));
    button = (Button) findViewById(R.id.button);
    hint1.setInputType(InputType.TYPE_CLASS_DATETIME);
    final boolean[] e = { false };
    button.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        //hint1.setEditable(true);
        hint1.isValid();

      }
    });
  }
}
