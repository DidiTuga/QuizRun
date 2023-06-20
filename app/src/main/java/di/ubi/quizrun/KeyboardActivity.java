/**
 * @file KeyboardActivity.java
 * @brief Coloca um formulário para o utilizador preencher e envia os dados para o servidor, caso o user fique no top20
 * @version 1.1
 * @date 20-05-2023
 * @autor Diogo Santos 45842
 */

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
    Button btnStart;
    TextView textView;

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
     * Função para inicializar as variaveis da classe, como os edittext e textview e button.
     * Colocar o listener no button
     */
    private void initVariables(){
        textView = findViewById(R.id.Txt_texto);
        btnStart = findViewById(R.id.Btn_submeter);
        btnStart.setOnClickListener(this);
        edt_nome = findViewById(R.id.Edt_Nome);
        edt_numero = findViewById(R.id.Edt_Naluno);
        edt_curso = findViewById(R.id.Edt_Curso);
    }
    /**
     * Coloca a lingua escolhida pelo utilizador, caso não tenha escolhido nada, coloca a lingua por defeito
     * E inicializa a activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences(MainActivity.pref_name, MODE_PRIVATE);
        String lingua = prefs.getString("language", "pt");
        MainActivity.setLanguage(this, lingua);
        setContentView(R.layout.activity_keyboard);
        viewSettings();
        // receber o texto do intent
        Intent intent = getIntent();
        String tempo = intent.getStringExtra("Tempo");
        String ponto = intent.getStringExtra("Pontos");
        String distancia = intent.getStringExtra("Distancia");
        // log para ver se o texto foi recebido
        Uteis.MSG_Log("Texto recebido: " + tempo + " Pontos: " + ponto + " Distancia: " + distancia);
        String texto = getString(R.string.Str_Tempo, tempo, distancia, ponto);
        textView.setText(texto);
        // ouvir as mudanças nas preferencias para fechar a activity
        prefs.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (sharedPreferences.getBoolean("fechar_keyboard", false)) {
                finish();
            }
        });

    }

    /**
     * Função para enviar os dados para o servidor, caso o utilizador clique no botão, e mostrar uma janela de dialogo para confirmar os dados
     * @param view - View do botão clicado
     */
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
    /**
     * Função para fechar a activity e dar um resultado para a activity anterior
     */
    public void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences(MainActivity.pref_name, MODE_PRIVATE);
        prefs.edit().putBoolean("fechar_keyboard", false).apply();
        finish();
    }


}