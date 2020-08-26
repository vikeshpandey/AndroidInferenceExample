package com.example.androidinferenceexample;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public static final String INPUT_ARR = "com.example.androidinferenceexample.editTextInput";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user taps the Send button */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayResultActivity.class);
        EditText editText = findViewById(R.id.editTextInput);
        String inputText = editText.getText().toString();
        intent.putExtra(INPUT_ARR, convertStringToFloatArr(inputText));

        startActivity(intent);
    }

    private static float[] convertStringToFloatArr(String inputText) {
        final String[] stringArr = inputText.split(",");
        final int arrLength = stringArr.length;
        final float[] floatArr = new float[arrLength];
        for (int i = 0; i< arrLength; i++){
            floatArr[i] = Float.parseFloat(stringArr[i]);
        }
        return floatArr;
    }
}