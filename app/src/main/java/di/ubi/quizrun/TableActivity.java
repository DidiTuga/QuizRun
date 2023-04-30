package di.ubi.quizrun;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Objects;

public class TableActivity extends AppCompatActivity {
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
        viewSettings();
        setContentView(R.layout.activity_table);
        addTable();

    }
    public void onBackPressed() {
        finish();
    }

    public void addTable() {
        TableLayout table = findViewById(R.id.tableLayout);
        SharedPreferences prefs = getSharedPreferences(MainActivity.pref_name, MODE_PRIVATE);

        // Data
        for (int i = 1; i < 22; i++){
            TableRow row2 = new TableRow(this);
            // padding
            if (i == 1){
                row2.setBackgroundColor(getResources().getColor(R.color.blue));
            }
            for (int j = 0; j < 8; j++)
            {
                TextView data = new TextView(this);
                data.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                if (i ==1){
                    data.setTextColor(getResources().getColor(R.color.white));
                    // colocar no centro
                    // colocar a negrito

                }
                else if (i % 2 == 0){
                    data.setTextColor(getResources().getColor(R.color.black));
                    row2.setBackgroundColor(getResources().getColor(R.color.light_gray));

                }
                else{
                    data.setTextColor(getResources().getColor(R.color.black));
                    row2.setBackgroundColor(getResources().getColor(R.color.white));
                }

                // adicionar margem a cada celula
                data.setPadding(20, 20, 20, 20);
                //
                data.setText(prefs.getString(""+i+j, ""));
                // auumentar o tamanho do texto
                data.setTextSize(17);
                row2.addView(data);
            }
            table.addView(row2);
            // table                     android:stretchColumns="*"
        }


    }
}