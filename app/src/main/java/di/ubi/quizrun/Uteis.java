/**
 * @file Uteis.java
 * @brief Classe para ter funções uteis, para cada projeto
 * @date 10/03/2023
 * @version 1.0
 * @autor Diogo Santos nº45842
 */

package di.ubi.quizrun;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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