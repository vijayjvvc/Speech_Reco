package com.jvworld.speechreco;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private TextView resultTv;
    private String resultStr;
    private static final int REQUEST = 1000;
    int a, b;

    private boolean isAdd = false, isMinus = false, isMultiply = false, isDivide = false;

    private final String appSpeak = "This app can perform addition , subtraction , Multiplication, Division.\n " +
            "Which operation do you want to perform ?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inItUi();
        a = codeRandom();
        b = codeRandom();
    }

    private int codeRandom() {
        int min = 1;
        int max = 10;

        Random random = new Random();

        int value = random.nextInt(max + min) + min;
        return value;
    }

    private void inItUi() {
        resultTv = findViewById(R.id.result_tv);
        Button retryBtn = findViewById(R.id.retry_btn);
        retryBtn.setOnClickListener(v -> {
            getTextFromSpeech();
        });

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);

                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported");
                } else {
                    speak(appSpeak);
                }
            } else {
                Log.e("TTS", "Initialization failed");
            }
        });

        countTimer(9000);

    }

    private void speak(String text) {
        textToSpeech.setPitch(0.8f);
        textToSpeech.setSpeechRate(0.8f);
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void getTextFromSpeech() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "JV");
        startActivityForResult(intent, REQUEST);
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST:
                int sum = a + b;
                int multiply = a * b;
                int minus = a-b;

                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    resultStr = text.get(0);
                    resultTv.setText(resultStr);
                }

                if (isAdd){
                    isAdd = false;
                    try {
                        if (sum == Integer.parseInt(resultStr)) {
                            speak("correct");
                        } else {
                            speak("Incorrect");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }if (isMultiply){
                    isMultiply = false;
                    try {
                        if (multiply == Integer.parseInt(resultStr)) {
                            speak("correct");
                        } else {
                            speak("Incorrect");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }if (isMinus){
                    isMinus = false;
                    try {
                        if (minus == Integer.parseInt(resultStr)) {
                            speak("correct");
                        } else {
                            speak("Incorrect");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                switch (resultStr) {
                    case "division":
                        speak("Not yet implemented");
                        break;
                    case "addition":
                        isAdd = true;
                        speak(String.valueOf(a) + "plus" + String.valueOf(b) + "is equal to");
                        countTimer(3000);
                        break;
                    case "multiplication":
                        isMultiply = true;
                        speak(String.valueOf(a) + "multiply by" + String.valueOf(b) + "is equal to");
                        countTimer(3000);
                        break;
                    case "subtraction":
                        isMinus = true;
                        speak(String.valueOf(a) + "minus" + String.valueOf(b) + "is equal to");
                        countTimer(3000);
                        break;
                }
                break;
        }
    }

    private void countTimer(int time) {
        CountDownTimer timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                getTextFromSpeech();
            }
        }.start();
    }
}