/**
 * @file TableActivity.java
 * @brief Classe para mostrar a tabela de classificação
 * @version 1.1
 * @date 10/06/2023
 * @autor Diogo Santos nº 45842
 */

package di.ubi.quizrun;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;


public class TableActivity extends AppCompatActivity {
    ArrayList<User> users;
    RecyclerView recyclerView;

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
     * Função para inicializar a activity, com a lingua guardada nas shared preferences, inicializar os dados e o recycler view
     * E inicializar o botão de voltar
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences(MainActivity.pref_name, MODE_PRIVATE);
        String lingua = prefs.getString("language", "pt");
        MainActivity.setLanguage(this, lingua);

        super.onCreate(savedInstanceState);
        viewSettings();
        setContentView(R.layout.activity_table);
        recyclerView = findViewById(R.id.recycler_view);
        initData();
        initRecyclerView();

        ImageView back = findViewById(R.id.IMG_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    /**
     * Função para quando o botão de voltar é pressionado, fechar a activity
     */
    public void onBackPressed() {
        finish();
    }

    /**
     * Função para inicializar os dados da tabela, com os dados guardados nas shared preferences, e colocar na lista
     * Se não houver dados, coloca dados default, a dizer que precisa de se conectar por bluetooth
     * @see User
     */
    private void initData() {
        SharedPreferences prefs = getSharedPreferences(MainActivity.pref_name, MODE_PRIVATE);
        users = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            String pos = prefs.getString("" + i + 0, "null");
            String date = prefs.getString("" + i + 1, "null");
            String distancia = prefs.getString("" + i + 2, "null");
            String tempo = prefs.getString("" + i + 3, "null");
            String pontos = prefs.getString("" + i + 4, "null");
            String num = prefs.getString("" + i + 5, "null");
            String nome = prefs.getString("" + i + 6, "Precisa de se conectar por Bluetooth");
            String curso = prefs.getString("" + i + 7, "null");

            User user = new User(pos, date, distancia, tempo, pontos, num, nome, curso);
            users.add(user);
        }
    }

    /**
     * Função para inicializar o recycler view, com os dados da lista
     * @see UserAdapter
     * @see User
     */
    private void initRecyclerView() {
        UserAdapter adapter = new UserAdapter(users, this);
        recyclerView.setAdapter(adapter);
    }

}