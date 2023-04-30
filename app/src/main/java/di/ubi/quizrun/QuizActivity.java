package di.ubi.quizrun;

import static android.os.Looper.myLooper;

import static di.ubi.quizrun.MainActivity.MESSAGE_READ;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {


        }
    };

    private void viewSettings(){
        // colocar fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // retirar o titulo da action bar
        Objects.requireNonNull(getSupportActionBar()).hide();
        // Deixar o ecra ligado
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        viewSettings();
        // iniciar buttons
        Button btnA = findViewById(R.id.Btn_A);
        Button btnB = findViewById(R.id.Btn_B);
        Button btnC = findViewById(R.id.Btn_C);
        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
        SharedPreferences prefs = getSharedPreferences(MainActivity.pref_name, MODE_PRIVATE);
        prefs.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (sharedPreferences.getBoolean("fechar_quiz", false)){
                finish();
            }
        });
    }
    public void onResume(){
        super.onResume();

    }
    public void onPause(){
        super.onPause();
    }
    @Override
    public void onClick(View view) {
        Intent resultIntent = new Intent();
        switch (view.getId()) {
            case R.id.Btn_A:
                resultIntent.putExtra("resultado", "1");
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
                break;
            case R.id.Btn_B:
                resultIntent.putExtra("resultado", "2");
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
                break;
            case R.id.Btn_C:
                resultIntent.putExtra("resultado", "3");
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
                break;
        }
    }
    public void onStop() {
        super.onStop();
        finish();
    }
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences prefs = getSharedPreferences(MainActivity.pref_name, MODE_PRIVATE);
        prefs.edit().putBoolean("fechar_quiz", false).apply();
        finish();
    }
}