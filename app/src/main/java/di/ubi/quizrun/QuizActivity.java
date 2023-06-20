/**
 * @file QuizActivity.java
 * @brief Activity para o quiz
 * @date 20/05/2023
 * @version 1.1
 * @autor Diogo Santos nº45842
 *
 */

package di.ubi.quizrun;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnA;
    Button btnB;
    Button btnC;

    /**
     * Função para colocar a ecrã completo, retirar o titulo da action bar e deixar o ecra ligado
     */
    private void viewSettings() {
        // colocar fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // retirar o titulo da action bar
        Objects.requireNonNull(getSupportActionBar()).hide();
        // Deixar o ecra ligado
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * Função para inicializar as variaveis e definir os listeners dos botões
     */
    private void initVariables(){
        btnA = findViewById(R.id.Btn_A);
        btnB = findViewById(R.id.Btn_B);
        btnC = findViewById(R.id.Btn_C);
        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
    }

    /**
     * Função para inicializar a activity, definir o layout
     * e registar um listener para quando o arduino enviar um sinal para fechar o quiz
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        viewSettings();
        initVariables();
        SharedPreferences prefs = getSharedPreferences(MainActivity.pref_name, MODE_PRIVATE);
        prefs.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (sharedPreferences.getBoolean("fechar_quiz", false)) {
                finish();
            }
        });
    }

    /**
     * Enviar o resultado do quiz para a activity anterior
     * @param view - botão que foi clicado
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(@NonNull View view) {
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

    /**
     * Função de fechar a activity caso o utilizador minimize a aplicação
     */
    public void onStop() {
        super.onStop();
        finish();
    }

    /**
     * Função para fechar a activity quando o utilizador carrega no botão de voltar e informar a activity anterior que o quiz foi fechado
     */
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences prefs = getSharedPreferences(MainActivity.pref_name, MODE_PRIVATE);
        prefs.edit().putBoolean("fechar_quiz", false).apply();
        finish();
    }
}