package di.ubi.quizrun;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Objects;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        // colocar fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // retirar o titulo da action bar
        Objects.requireNonNull(getSupportActionBar()).hide();
        // iniciar buttons
        Button btnA = findViewById(R.id.Btn_A);
        Button btnB = findViewById(R.id.Btn_B);
        Button btnC = findViewById(R.id.Btn_C);
        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent resultIntent = new Intent();
        switch (view.getId()) {
            case R.id.Btn_A:
                resultIntent.putExtra("resultado", "A");
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
                break;
            case R.id.Btn_B:
                resultIntent.putExtra("resultado", "B");
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
                break;
            case R.id.Btn_C:
                resultIntent.putExtra("resultado", "C");
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
                break;
        }
    }
}