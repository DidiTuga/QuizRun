package di.ubi.quizrun;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Objects;

public class TableActivity extends AppCompatActivity {
    ArrayList<User> users;
    RecyclerView recyclerView;
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

    public void onBackPressed() {
        finish();
    }


    private void initData() {
        SharedPreferences prefs = getSharedPreferences(MainActivity.pref_name, MODE_PRIVATE);
        users = new ArrayList<>();
        for (int i = 1; i< 21;i++){
            String pos = prefs.getString("" + i + 0, "");
            String date = prefs.getString("" + i + 1, "");
            String distancia = prefs.getString("" + i + 2, "");
            String tempo = prefs.getString("" + i + 3, "");
            String pontos = prefs.getString("" + i + 4, "");
            String num = prefs.getString("" + i + 5, "");
            String nome = prefs.getString("" + i + 6, "");
            String curso = prefs.getString("" + i + 7, "");

            User user = new User(pos, date, distancia, tempo, pontos, num, nome, curso);
            users.add(user);
        }
    }
    private void initRecyclerView() {
        UserAdapter adapter = new UserAdapter(users, this);
        recyclerView.setAdapter(adapter);
    }

}