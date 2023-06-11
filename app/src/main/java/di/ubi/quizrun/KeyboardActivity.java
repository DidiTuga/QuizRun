package di.ubi.quizrun;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class KeyboardActivity extends AppCompatActivity implements View.OnClickListener {


    EditText edt_nome, edt_numero, edt_curso;

    private void viewSettings() {
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
        setContentView(R.layout.activity_keyboard);
        // iniciar o button
        Button btnStart = findViewById(R.id.Btn_submeter);
        btnStart.setOnClickListener(this);
        // inicar textview e edittext
        TextView textView = findViewById(R.id.Txt_texto);
        edt_nome = findViewById(R.id.Edt_Nome);
        edt_numero = findViewById(R.id.Edt_Naluno);
        edt_curso = findViewById(R.id.Edt_Curso);

        viewSettings();
        // receber o texto do intent
        Intent intent = getIntent();
        String tempo = intent.getStringExtra("Tempo");
        String ponto = intent.getStringExtra("Pontos");
        String distancia = intent.getStringExtra("Distancia");
        // log para ver se o texto foi recebido
        Uteis.MSG_Log("Texto recebido: " + tempo + " Pontos: " + ponto + " Distancia: " + distancia);
        String texto = getString(R.string.Str_Tempo, tempo, distancia, ponto);
        textView.setText("texto");
        SharedPreferences prefs = getSharedPreferences(MainActivity.pref_name, MODE_PRIVATE);
        prefs.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (sharedPreferences.getBoolean("fechar_keyboard", false)) {
                finish();
            }
        });

    }

    @Override
    public void onClick(View view) {
        Intent resultIntent = new Intent();
        if (view.getId() == R.id.Btn_submeter) {
            String nome = edt_nome.getText().toString();
            String numero = edt_numero.getText().toString();
            String curso = edt_curso.getText().toString();
            String resultado = numero + ";" + nome + ";" + curso;
            Uteis.MSG_Log("Resultado: " + resultado);
            MainActivity.mBluetoothManager.sendData(resultado);
            // janela de dialogo
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(Html.fromHtml("<font color='#000055'>Confirmar dados!</font>", Html.FROM_HTML_MODE_LEGACY));
            builder.setMessage(Html.fromHtml("<font color='#00BFFF'>Confirma os seguintes dados:\nNome: " + nome + "\nNúmero: " + numero + "\nCurso: " + curso + "</font>", Html.FROM_HTML_MODE_LEGACY));
            // se o utilizador clicar em sim
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    resultIntent.putExtra("resultado", "et");
                    setResult(RESULT_OK, resultIntent);

                    finish();
                }
            });
            // se o utilizador clicar em nao
            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    alertDialog.getWindow().setBackgroundDrawableResource(R.color.white);
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#002E80"));
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#FF0000"));
                }
            });
            alertDialog.show();
        }

    }

    public void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences(MainActivity.pref_name, MODE_PRIVATE);
        prefs.edit().putBoolean("fechar_keyboard", false).apply();
        finish();
    }


}