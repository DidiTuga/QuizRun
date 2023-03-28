package di.ubi.quizrun;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import java.util.Objects;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int MESSAGE_READ = 23;
    public static String deviceName= "INSMAN";
    private Animation animation;

    static BluetoothManager mBluetoothManager;
    Button btnStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // iniciar o button
        btnStart = findViewById(R.id.Btn_Start);
        btnStart.setOnClickListener(this);

        // colocar fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // retirar o titulo da action bar
        Objects.requireNonNull(getSupportActionBar()).hide();
        // Deixar o ecra ligado
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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

    }


    public void onStop() {
        super.onStop();
        Uteis.MSG_Log("Desconectado");

        mBluetoothManager.disconnect();
    }
    // iniciar o handler
    @SuppressLint("HandlerLeak") Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_READ) {
                String readMessage = (String) msg.obj;
                readMessage = readMessage.trim();
                switch (readMessage) {
                    case "S":
                        Uteis.MSG_Log("START");

                        break;
                    case "R":
                        Uteis.MSG_Log("RUN");
                        // mudar content view para a activity do run
                        setContentView(R.layout.activity_run);
                        ImageView imageView = findViewById(R.id.run_gif);
                        imageView.startAnimation(animation);
                        break;
                    case "Q":
                        Uteis.MSG_Log("QUIZ");
                        // mudar content view para a activity do quiz
                        Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                        startActivityForResult(intent, 1);
                        break;
                    case "K":
                        Uteis.MSG_Log("KEYBOARD");
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
                        Uteis.MSG_Log("Erro no comando recebido: " + readMessage);
                        break;


                }
            }
        }
    };

    // recebe o resultado da Activity iniciada
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String resultado = data.getStringExtra("resultado");
            // faz algo com o resultado recebido
            Uteis.MSG_Log("Resultado_Quiz: " + resultado);
            mBluetoothManager.sendData(resultado);
        }else if (requestCode == 2 && resultCode == RESULT_OK) {
            String resultado = data.getStringExtra("resultado");
            // faz algo com o resultado recebido
            Uteis.MSG_Log("Resultado_Keyboard: " + resultado);
            mBluetoothManager.sendData(resultado);
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(@NonNull View view) {
        switch (view.getId()) {
            case R.id.Btn_Start:
                Uteis.MSG_Log("Botão ativado");
                mBluetoothManager.sendData("cS");
                break;
            default:
                Uteis.MSG_Log("Erro no comando recebido");
                break;

                }
    }

}