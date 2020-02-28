package fr.ece.lab3.thread;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "fr.ece.lab3.thread.extra.MESSAGE";
    private static final int MY_ACTIVITY_CODE = 1;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menulab4, menu); //Le deuxième paramètre "menu" est le menu passé en paramètre de la fonction OnCreateOptionsMenu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.item1 :
                Toast.makeText(this, "Historique sélectionné", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, WebViewDisplay.class);
                intent.putExtra(EXTRA_MESSAGE, "Dernière opération : " + param1_tmp + " " + operation_tmp + " " + param2_tmp + " = " + resultat_operation);
                startActivity(intent);
                return true;
            default : return super.onOptionsItemSelected(item);
        }
    }

    /**Intent intent = new Intent(this, MyCalculusRunnable.class);**/

    TextView textCalcul;
    TextView result;
    LinearLayout linearLayout;
    Button equal_btn;

    Handler handler;
    String resultat_operation = "0";

    // param 1 pour le nombre 1, param 2 pour le nombre 2 de l'opération
    int param1 = 0;
    int param2 = 0;
    //param pour l'historique
    int param1_tmp = 0;
    int param2_tmp = 0;
    //Opérande
    String operation = "undefined";
    //opération pour l'historique
    String operation_tmp = "+"; //valeur par défaut arbitraire


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textCalcul = (TextView) findViewById(R.id.textCalcul);
        result = (TextView) findViewById(R.id.textResult);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayoutDigit);
        equal_btn = (Button) findViewById(R.id.equal_btn);

        handler = new Handler();

        /* //DECLARATION JAVA DU BOUTON -- LAB 2
        //Déclaration du bouton =
        final Button equal_btn = new Button(this);
        equal_btn.setTag(R.string.equal);
        equal_btn.setText(R.string.equal);

        //équivalent à android:onclick="clickHandler"
        equal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickHandler(equal_btn);
            }
        });

        //Test de set des width et height du bouton =
        equal_btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        //Ajout au linearLayout
        linearLayout.addView(equal_btn);
*/

        /**Décommenter cette ligne pour la version AsyncTask*/
        equal_btn.setOnClickListener(startResultDisplay_async());


    }

    public void clickHandler(View b) {


        //Si on appuie sur un chiffre
        if (!b.getTag().toString().equals("=") && //Syntaxe pour Not Equal avec un String
                !b.getTag().toString().equals("+") && !b.getTag().toString().equals("-") &&
                !b.getTag().toString().equals("*") && !b.getTag().toString().equals("/")) {
            //si un signe d'opération n'a pas encore été cliqué
            if (operation.equals("undefined")) {
                textCalcul.setText(textCalcul.getText() + b.getTag().toString());
                param1 = Integer.parseInt(textCalcul.getText().toString());
            }
            //si un signe d'opération a déjà été cliqué
            else {
                textCalcul.setText(textCalcul.getText() + b.getTag().toString());
                param2 = Integer.parseInt(textCalcul.getText().toString());
            }
        }

        //Si on appuie sur un signe d'opération
        else if (b.getTag().toString().equals("+") || b.getTag().toString().equals("-") ||
                b.getTag().toString().equals("*") || b.getTag().toString().equals("/")) {
            //On commence par reset le textView
            textCalcul.setText("");
            //switch pour savoir quelle opération est selectionée
            switch (b.getTag().toString()) {
                case "+":
                    operation = "+";
                    break;
                case "-":
                    operation = "-";
                    break;
                case "*":
                    operation = "*";
                    break;
                case "/":
                    operation = "/";
                    break;
            }
        }

        //Si on appuie sur le bouton "="
        else if (b.getTag().toString().equals("=")) {

            //On reset le textView
            textCalcul.setText("");
            //On effectue l'opération simple en fonction du signe opératoire
            int res = 0;
            switch (operation) {
                case "+":
                    res = param1 + param2;
                    break;
                case "-":
                    res = param1 - param2;
                    break;
                case "*":
                    res = param1 * param2;
                    break;
                case "/": //Blindage de la division par 0
                    if (param2 != 0) {
                        res = param1 / param2;
                    }
                    if (param2 == 0) {
                        Toast.makeText(this, "Division par 0 impossible", Toast.LENGTH_LONG).show();
                    }
                    break;
                case "undefined":
                    res = param1;
                    break;
            }

            //Puis on affiche le résultat
            if (param2 != 0) {
                //Traitement sans Thread
                //result.setText(String.valueOf(res));

                resultat_operation = String.valueOf(res);
                /**Décommenter cette ligne pour la version Handler*/
                //startResultDisplay();
                /**Décommenter cette ligne pour la version AsyncTask*/
                startResultDisplay_async();
                //Vérification de l'opération avec un Toast
                Toast.makeText(this, param1 + " " + operation + " " + param2, Toast.LENGTH_LONG).show();
            }
            if (param2 == 0) {
                Toast.makeText(this, "Division par 0 impossible", Toast.LENGTH_LONG).show();
            }

            //On réinitialise les variables pour l'opération suivante
            param1 = 0;
            param2 = 0;
            operation = "undefined";
        }
    }

    /**
     * Exemple avec Handler
     **/
    public void startResultDisplay() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        result.setText(resultat_operation);
                    }
                });

            }
        };
        new Thread(runnable).start();
    }

    public View.OnClickListener startResultDisplay_async() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                Async_t task = new Async_t();
                task.execute();
            }
        };
    }

    private class Async_t extends AsyncTask<Void, Void, String> {
        @SuppressLint("WrongThread")
        protected String doInBackground(Void... vals) {
            try {
                //Adresse privée pour la machine virtuelle (émulateur)
                //Socket socket = new Socket("10.0.2.2", 9876);
                //Adresse ip de mon téléphone en partage de co - les deux appareils DOIVENT être sur
                //le même réseau pour se capter l'un l'autre
                Socket socket = new Socket("192.168.43.200", 9876);
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeDouble(param1);
                dos.writeChar(operation.charAt(0));
                dos.writeDouble(param2);
                final double res = dis.readDouble();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, res+"", Toast.LENGTH_SHORT).show();
                    }
                });
                dis.close();
                dos.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String res_fin = "";
            switch (operation) {
                case "+":
                    res_fin = String.valueOf(param1 + param2);
                    break;
                case "-":
                    res_fin = String.valueOf(param1 - param2);
                    break;
                case "*":
                    res_fin = String.valueOf(param1 * param2);
                    break;
                case "/": //Blindage de la division par 0
                    if (param2 != 0) {
                        res_fin = String.valueOf(param1 / param2);
                    }
                    if (param2 == 0) {
                        Toast.makeText(MainActivity.this, "Division par 0 impossible", Toast.LENGTH_LONG).show();
                    }
                    break;
                case "undefined":
                    res_fin = String.valueOf(param1);
                    break;
            }


            return res_fin;
        }

        protected void onPostExecute(String resultat) {

            textCalcul.setText("");
            result.setText(resultat);
            Toast.makeText(MainActivity.this, param1 + " " + operation + " " + param2, Toast.LENGTH_LONG).show();
            //On save les valeurs des paramètres de l'opération qui vient d'avoir lieue dans les paramètres temporaires...
            param1_tmp = param1;
            param2_tmp = param2;
            operation_tmp = operation;
            //Avant de reset les param pour la prochaine opération
            param1 = 0;
            param2 = 0;
            operation = "undefined";
            resultat_operation = resultat;
        }
    }
}
