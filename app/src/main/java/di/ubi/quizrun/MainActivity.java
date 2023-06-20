/**
 * @file MainActivity.java
 * @brief Classe que representa a MainActivity, tendo como função principal a ligação ao bluetooth e a criação de um handler para receber os dados do arduino para alterar as janelas
 * @date 2023/05/30
 * @version 1.2
 * @autor Diogo Santos nº 45842
 */

package di.ubi.quizrun;

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
    public static final String pref_name = "dataBase";
    //public static String deviceName = "BTBEE PRO";
    @SuppressLint("StaticFieldLeak")
    public static BluetoothManager mBluetoothManager;
    public String deviceName;
    Button btnStart, btnTabela;
    FloatingActionButton btnLanguage;
    private Animation animation;
    private int flag_quiz = 0;
    private int flag_keyboard = 0;

    /**
     * Handler para receber os dados do arduino e alterar as janelas
     * @see BluetoothManager
     */
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler(Looper.myLooper()) {
        // variavel para contar o numero de jogadores so para ler os top 20
        private int count_players = 0;
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

                        initVariables();

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
                        count_players++;
                        // vai receber a tabela de tempos

                        String[] tabela = readMessage.split(";");
                        SharedPreferences prefs = getSharedPreferences(pref_name, MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        for (int j = 0; j < tabela.length; j++) {
                            editor.putString("" + count_players + j, tabela[j]);
                        }

                        editor.apply();

                        if (count_players == 21) {
                            count_players = 0;
                        }
                        break;
                }
            }
        }
    };

    /**
     * Função para iniciar todos os botões e variaveis, colocando os listeners nos botões
     * E iniciar a animação do ciclista
     */
    private void initVariables() {

        btnStart = findViewById(R.id.Btn_Start);
        btnStart.setOnClickListener(this);
        btnTabela = findViewById(R.id.Btn_table);
        btnTabela.setOnClickListener(this);
        btnStart.setEnabled(true);
        btnLanguage = findViewById(R.id.Btn_Language);
        btnLanguage.setOnClickListener(this);
        // animação do ciclista
        animation = new AlphaAnimation(1, 0); // Altera alpha de visível a invisível
        animation.setDuration(500); // duração - meio segundo
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE); // Repetir infinitamente
        animation.setRepeatMode(Animation.REVERSE); //Inverte a animação no final para que o botão vá desaparecendo
    }

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
     * Função para ir buscar a informação, das linguas e dispositivo guardada nas shared preferences
     */
    private void getInfo() {
            SharedPreferences prefs = getSharedPreferences(pref_name, MODE_PRIVATE);
            String lingua = prefs.getString("language", "pt");
            setLanguage(this, lingua);
            deviceName = prefs.getString("deviceName", "INSMAN");
    }

    /**
     * OnCreate method - Função que é chamada quando a activity é criada
     * É nela que se cria animação para activity do run, se verifica se o bluetooth é suportado
     * E tenta ligar ao dispositivo bluetooth
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getInfo();
        setContentView(R.layout.activity_main);
        // iniciar o button
        initVariables();
        btnStart.setEnabled(false);
        //guardarInfo();
        viewSettings();
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

    /**
     * Função para desligar o bluetooth quando a activity é destruida
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Uteis.MSG_Log("Desconectado");

        mBluetoothManager.disconnect();
    }

    /**
     * Função para receber o resultado do quiz e do keyboard, para depois enviar para o arduino
     * @param requestCode - codigo do pedido de resultado
     * @param resultCode - codigo do resultado, se foi bem sucedido ou não
     * @param data - dados recebidos
     */
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

    /**
     * OnClick dos botões
     * Se o botão start for clicado, envia um s para o arduino
     * Se o botão tabela for clicado, vai para a tabela
     * Se o botão definições for clicado, abre um popup com as definições
     *  Sendo elas, a linguagem e o dispositivo bluetooth
     * @param view - botão clicado
     */
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
                Uteis.MSG_Log("Botão Definições ativado");
                // POPUP para as definições
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                String title = getResources().getString(R.string.Str_definicoes);
                title = Html.fromHtml("<font color='#000055'>" + title + "</font>", Html.FROM_HTML_MODE_LEGACY).toString();
                builder.setTitle(title);
                String[] opcoes = getResources().getStringArray(R.array.definicoes);
                builder.setItems(opcoes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) { // mudar linguagem
                            getChangeLanguageDialog();
                        } else if (which == 1) { // mudar controlador bluetooth
                            getChangeBluetoothDialog();
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

    /**
     * Função para ir buscar o dialogo para mudar o controlador bluetooth
     */
    private void getChangeLanguageDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        String title = getResources().getString(R.string.Str_escolher);
        title = Html.fromHtml("<font color='#000055'>" + title + "</font>", Html.FROM_HTML_MODE_LEGACY).toString();
        builder.setTitle(title);
        String[] linguagens = getResources().getStringArray(R.array.linguagem);
        builder.setItems(linguagens, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // mudar a linguagem para português
                    setLanguage(MainActivity.this, "pt");
                    recreate();

                } else if (which == 1) {
                    // mudar a linguagem para inglês
                    setLanguage(MainActivity.this, "es");
                    recreate();

                } else if (which == 2) {
                    // mudar a linguagem para espanhol
                    setLanguage(MainActivity.this, "en");
                    recreate();
                }
            }
        });
        // botao cancelar
        builder.setNegativeButton(R.string.Str_cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        builder.create().show();
    }

    /**
     * Função para ir buscar o dialogo para mudar o controlador bluetooth
     */
    private void getChangeBluetoothDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        String title = getResources().getString(R.string.Str_controlador, deviceName);
        title = Html.fromHtml("<font color='#000055'>" + title + "</font>", Html.FROM_HTML_MODE_LEGACY).toString();
        builder.setTitle(title);
        String[] pairedDevices = mBluetoothManager.getpairedDevices();
        builder.setItems(pairedDevices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mBluetoothManager.setDeviceName(pairedDevices[which]);
                deviceName = pairedDevices[which];
                SharedPreferences.Editor editor = getSharedPreferences(pref_name, MODE_PRIVATE).edit();
                editor.putString("deviceName", deviceName);
                editor.apply();
                recreate();
            }
        });
        // botao para fechar o popup
        builder.setNegativeButton(R.string.Str_cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    /**
     * Função para mudar a linguagem da aplicação
     * @param context o contexto onde a aplicação está a correr
     * @param languageCode o código da linguagem para mudar
     */
    public static void setLanguage(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        // salvar a linguagem escolhida
        SharedPreferences.Editor editor = context.getSharedPreferences(pref_name, MODE_PRIVATE).edit();
        editor.putString("language", languageCode);
        editor.apply();
    }


}