package di.ubi.quizrun;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

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

    public static void setLanguage(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
        /* Colocar a aplicacao em fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Tirar o título da action bar
        Objects.requireNonNull(getSupportActionBar()).hide();*/