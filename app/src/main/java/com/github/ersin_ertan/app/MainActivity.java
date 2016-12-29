package com.github.ersin_ertan.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.github.ersin_ertan.hinterrortextinputview.HintErrorTextInputView;
import com.github.ersin_ertan.hinterrortextinputview.validator.length.MaxLength;

public class MainActivity extends AppCompatActivity {

  HintErrorTextInputView hint1;
  HintErrorTextInputView hint2;

  Button button;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    hint1 = (HintErrorTextInputView) findViewById(R.id.hint1);
    hint1.validateThat(MaxLength.is(this, 2));

    hint2 = (HintErrorTextInputView) findViewById(R.id.hint2);
    button = (Button) findViewById(R.id.button);
    button.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (hint1.isValid()) Toast.makeText(MainActivity.this, "Valid", Toast.LENGTH_SHORT).show();
      }
    });
  }
}
