package di.ubi.quizrun;

import static java.lang.Thread.sleep;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int MESSAGE_READ = 23;
    public static final String pref_name = "pref_name";
    public static String deviceName = "INSMAN";
    //public static String deviceName = "BTBEE PRO";
    static BluetoothManager mBluetoothManager;
    Button btnStart, btnTabela;
    FloatingActionButton btnLanguage;
    private Animation animation;
    private int flag_quiz = 0;
    private int flag_keyboard = 0;
    private int i = 0;

    private void initButton() {

        btnStart = findViewById(R.id.Btn_Start);
        btnStart.setOnClickListener(this);
        btnTabela = findViewById(R.id.Btn_table);
        btnTabela.setOnClickListener(this);
        btnStart.setEnabled(true);
        btnLanguage = findViewById(R.id.Btn_Language);
        btnLanguage.setOnClickListener(this);
    }
    private void viewSettings() {
        // colocar fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // retirar o titulo da action bar
        Objects.requireNonNull(getSupportActionBar()).hide();
        // Deixar o ecra ligado
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // iniciar o handler
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_READ) {
                String readMessage = (String) msg.obj;

                readMessage = readMessage.trim();
                switch (readMessage) {
                    case "S":
                        Uteis.MSG_Log("START");
                        // se a question ou o keyboard estiverem abertos, fechar  e voltar para o main
                        setContentView(R.layout.activity_main);
                        // quando receber o S meter o botao disable

                        if (flag_keyboard == 1) {
                            flag_keyboard = 0;
                            SharedPreferences prefs = getSharedPreferences(pref_name, MODE_PRIVATE);
                            prefs.edit().putBoolean("fechar_keyboard", true).apply();
                        }

                        initButton();

                        break;
                    case "R":
                        Uteis.MSG_Log("RUN");

                        // mudar content view para a activity do run
                        setContentView(R.layout.activity_run);
                        ImageView imageView = findViewById(R.id.run_gif);
                        imageView.startAnimation(animation);
                        if (flag_quiz == 1) {
                            flag_quiz = 0;
                            Uteis.MSG_Log("FLAG QUIZ");
                            SharedPreferences prefs = getSharedPreferences(pref_name, MODE_PRIVATE);
                            prefs.edit().putBoolean("fechar_quiz", true).apply();
                        }
                        break;
                    case "Q":
                        Uteis.MSG_Log("QUIZ");
                        flag_quiz = 1;
                        // mudar content view para a activity do quiz
                        Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                        startActivityForResult(intent, 1);
                        break;
                    case "K":
                        Uteis.MSG_Log("KEYBOARD");
                        flag_keyboard = 1;
                        Intent keyboard = new Intent(MainActivity.this, KeyboardActivity.class);
                        keyboard.putExtra("Tempo", "6H");
                        keyboard.putExtra("Distancia", "10km");
                        keyboard.putExtra("Pontos", "50");
                        startActivityForResult(keyboard, 2);
                        // mandar informação  para a activity do keyboard

                        // voltar para o content view do main
                        setContentView(R.layout.activity_main);
                        break;
                    default:
                        i++;
                        // vai receber a tabela de tempos

                        String[] tabela = readMessage.split(";");
                        SharedPreferences prefs = getSharedPreferences(pref_name, MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        for (int j = 0; j < tabela.length; j++) {
                            editor.putString("" + i + j, tabela[j]);
                        }

                        editor.apply();

                        if (i == 21) {
                            i = 0;
                        }
                        break;
                }
            }
        }
    };


    // função para testar a leaderboard
    private void guardarInfo(){
        SharedPreferences prefs = getSharedPreferences(pref_name, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        for(int i = 1; i< 21; i++){
            String teste = i+";23/01/21;213;01:12;21;45842;diogo;EI";
            String[] tabela = teste.split(";");
            for (int j = 0; j < tabela.length; j++) {
                editor.putString("" + i + j, tabela[j]);
            }

        }
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // iniciar o button
        initButton();
        btnStart.setEnabled(false);
        //guardarInfo();
        viewSettings();
        // animação do ciclista
        animation = new AlphaAnimation(1, 0); // Altera alpha de visível a invisível
        animation.setDuration(500); // duração - meio segundo
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE); // Repetir infinitamente
        animation.setRepeatMode(Animation.REVERSE); //Inverte a animação no final para que o botão vá desaparecendo
        // Ver se o bluetooth é suportado
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Toast.makeText(this, "NÃO È SUPORTADO", Toast.LENGTH_SHORT).show();
            finish();
        }
        // iniciar o bluetooth
        mBluetoothManager = new BluetoothManager(this, mHandler, this);
        mBluetoothManager.connectToDevice(deviceName);
        if (mBluetoothManager.isConnected()) {
            // handler de 1,5 segundos para mandar o t
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothManager.sendData("t");
                }
            }, 1500);


        }

    }

    public void onDestroy() {
        super.onDestroy();
        Uteis.MSG_Log("Desconectado");

        mBluetoothManager.disconnect();
    }

    // recebe o resultado da Activity iniciada
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String resultado = data.getStringExtra("resultado");
            // faz algo com o resultado recebido
            Uteis.MSG_Log("Resultado_Quiz: " + resultado);
            mBluetoothManager.sendData(resultado);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            String resultado = data.getStringExtra("resultado");
            // faz algo com o resultado recebido
            Uteis.MSG_Log("Resultado_Keyboard: " + resultado);
            mBluetoothManager.sendData(resultado);
            // handler de 1,5 segundos para mandar o t para atualizar a tabela
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothManager.sendData("t");
                }
            }, 1500);
        } else if (requestCode == 2 && resultCode == RESULT_CANCELED) {
            Uteis.MSG_Log("Resultado_Keyboard: Cancelado");
            setContentView(R.layout.activity_main);
        } else if (requestCode == 1 && resultCode == RESULT_CANCELED) {
            Uteis.MSG_Log("Resultado_Quiz: Cancelado");
            setContentView(R.layout.activity_run);
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(@NonNull View view) {
        switch (view.getId()) {
            case R.id.Btn_Start:
                Uteis.MSG_Log("Botão ativado");
                mBluetoothManager.sendData("s");
                break;
            case R.id.Btn_table:
                Uteis.MSG_Log("Botão tabela ativado");
                Intent intent = new Intent(MainActivity.this, TableActivity.class);
                startActivity(intent);
                break;
            case R.id.Btn_Language:
                Uteis.MSG_Log("Botão linguagem ativado");
                // pop up para escolher a linguagem
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                String title = getResources().getString(R.string.Str_escolher);
                title = Html.fromHtml("<font color='#000055'>"+title+"</font>",Html.FROM_HTML_MODE_LEGACY).toString();
                builder.setTitle(title);
                String[] linguagens = getResources().getStringArray(R.array.linguagem);
                builder.setItems(linguagens, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // mudar a linguagem para português
                            Uteis.setLanguage(MainActivity.this,"pt");
                            recreate();

                        } else if (which == 1) {
                            // mudar a linguagem para inglês
                            Uteis.setLanguage(MainActivity.this,"es");
                            recreate();

                        } else if (which == 2) {
                            // mudar a linguagem para espanhol
                            Uteis.setLanguage(MainActivity.this,"en");
                            recreate();
                        }
                    }
                });
                builder.create().show();
                break;
            default:
                Uteis.MSG_Log("Erro no comando recebido");
                break;

        }
    }




}