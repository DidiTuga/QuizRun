package di.ubi.quizrun;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class Uteis {

    public static void MSG(Context Cont, String txt) {
        Toast.makeText(Cont, txt, Toast.LENGTH_LONG).show();
    }

    public static void MSG_Debug(String txt) {
        Log.i("DEBUG", txt);
    }

    public static void MSG_Log(String txt) {
        Log.i("INFORX", txt);
        //  Categoria -- Depois posso meteter Passo 3 ect
    }
}
        /* Colocar a aplicacao em fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Tirar o título da action bar
        Objects.requireNonNull(getSupportActionBar()).hide();*/