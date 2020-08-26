package com.example.androidinferenceexample;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.amazon.neo.dlr.DLR;

import java.util.Arrays;

public class DisplayResultActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_result);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        float[] inputArr = intent.getFloatArrayExtra(MainActivity.INPUT_ARR);

        long handle = loadModel(); //TODO:: fix the model path by placing the model into libs folder

        // This is an optional method, comment it out if not needed
        logModelMetadata(handle);

        final float result = performInference(handle, inputArr);


        final TextView textView = findViewById(R.id.textViewResult);
        final String resultString = Arrays.toString(inputArr) + " -> " + result;

        //view the result on the screen
        textView.setText(resultString);
    }

    private long loadModel() {
        //TODO:: fix the model path by placing the model in libs
        long handle = DLR.CreateDLRModel("fullModelPath", 1, 0);
        Log.i("DLR", "CreateDLRModel: " + handle);
        if (handle == 0) {
            Log.i("DLR", "DLRGetLastError: " + DLR.DLRGetLastError());
            throw new RuntimeException("CreateDLRModel failed");
        }
        return handle;
    }

    private void logModelMetadata(long handle) {
        Log.i("DLR", "GetDLRBackend: " + DLR.GetDLRBackend(handle));
        Log.i("DLR", "GetDLRNumInputs: " + DLR.GetDLRNumInputs(handle));
        Log.i("DLR", "GetDLRNumWeights: " + DLR.GetDLRNumWeights(handle));
        Log.i("DLR", "GetDLRNumOutputs: " + DLR.GetDLRNumOutputs(handle));

        String inputName = DLR.GetDLRInputName(handle, 0);
        Log.i("DLR", "GetDLRInputName[0]: " + inputName);
        Log.i("DLR", "GetDLRWeightName[4]: " + DLR.GetDLRWeightName(handle, 4));

        int outDim = DLR.GetDLROutputDim(handle, 0);
        long outSize = DLR.GetDLROutputSize(handle, 0);
        Log.i("DLR", "GetDLROutputSize[0]: " + outSize);
        Log.i("DLR", "GetDLROutputDim[0]: " + outDim);
        //GetDLROutputShape
        long[] out_shape = new long[outDim];
        if (DLR.GetDLROutputShape(handle, 0, out_shape) != 0) {
            Log.i("DLR", "DLRGetLastError: " + DLR.DLRGetLastError());
            throw new RuntimeException("GetDLROutputShape failed");
        }
        Log.i("DLR", "GetDLROutputShape[0]: " + Arrays.toString(out_shape));
    }


    private float performInference(long handle, float[] inputArr) {
        setModelInput(handle, inputArr);
        runModel(handle);
        return getInferenceResult(handle);
    }

    private void setModelInput(long handle, float[] inputArr) {
        long[] shape = {1, 4}; // hard coded as of now

        if (DLR.SetDLRInput(handle, "data", shape, inputArr, 2) != 0) {
            Log.i("DLR", "DLRGetLastError: " + DLR.DLRGetLastError());
            throw new RuntimeException("SetDLRInput failed");
        }
        Log.i("DLR", "SetDLRInput: OK");
    }

    private void runModel(long handle) {
        if (DLR.RunDLRModel(handle) != 0) {
            Log.i("DLR", "DLRGetLastError: " + DLR.DLRGetLastError());
            throw new RuntimeException("RunDLRModel failed");
        }
        Log.i("DLR", "RunDLRModel: OK");
    }

    private float getInferenceResult(long handle) {
        float[] output = {0};
        if (DLR.GetDLROutput(handle, 0, output) != 0) {
            Log.i("DLR", "DLRGetLastError: " + DLR.DLRGetLastError());
            throw new RuntimeException("GetDLROutput failed");
        }
        return output[0];
    }


}