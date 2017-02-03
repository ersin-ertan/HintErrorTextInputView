package com.github.ersin_ertan.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.github.ersin_ertan.hinterrortextinputview.HintErrorTextInputView;
import com.github.ersin_ertan.hinterrortextinputview.validator.length.Empty;

import static java.util.concurrent.TimeUnit.SECONDS;

public class MainActivity extends AppCompatActivity
    implements HintErrorTextInputView.IsValidListener {

  HintErrorTextInputView hint1;

  Button button;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    hint1 = (HintErrorTextInputView) findViewById(R.id.hint1);
    hint1.validateThat(Empty.not(this));
    button = (Button) findViewById(R.id.button);
    Button button2 = (Button) findViewById(R.id.button2);
    hint1.setInputType(InputType.TYPE_CLASS_DATETIME);
    //hint1.addIsValidListener(new HintErrorTextInputView.IsValidListener() {
    //  @Override public void isValid(boolean isValid) {
    //    Toast.makeText(MainActivity.this, String.valueOf(isValid), Toast.LENGTH_SHORT).show();
    //  }
    //});
    hint1.addIsValidListener(this);
    button.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        //hint1.setEditable(true);
        //boolean b = hint1.isValid();
        //Toast.makeText(MainActivity.this, String.valueOf(b), Toast.LENGTH_SHORT).show();
        //hint1.isValid();
        Toast.makeText(MainActivity.this, String.valueOf(hint1.getIsShowingError()),
            Toast.LENGTH_SHORT).show();
      }
    });

    button2.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        hint1.showTimedError("TIMED_ERROR-", SECONDS.toMillis(5));
      }
    });
  }

  @Override public void isValid(boolean isValid) {
    Toast.makeText(MainActivity.this, String.valueOf(isValid), Toast.LENGTH_SHORT).show();
  }
}
