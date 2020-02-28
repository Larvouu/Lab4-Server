package fr.ece.lab3.thread;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WebViewDisplay extends AppCompatActivity {

    private TextView textView;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_display);

        Button button = (Button) findViewById(R.id.button);
        final EditText editText = (EditText) findViewById(R.id.editText);
        webView = (WebView) findViewById(R.id.webview);
        textView = (TextView) findViewById(R.id.textView);
        //On écrit cette ligne pour que la page internet s'affiche sur l'application
        //sans elle, une page s'ouvre sur notre navigateur par défaut
        webView.setWebViewClient(new WebViewClient());
        //Puis cette ligne pour charger l'URL
        webView.loadUrl("https://www.google.com");

        //Get l'intent qui active cette activité
        Intent intent = getIntent();
        textView.setText(intent.getStringExtra(MainActivity.EXTRA_MESSAGE));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.setWebViewClient(new WebViewClient());
                webView.loadUrl(editText.getText().toString());
                Toast.makeText(WebViewDisplay.this,editText.getText().toString(),Toast.LENGTH_LONG ).show();
            }
        });
    }

    //Override cette méthode permet de ne pas fermer l'app si on ne peut plus
    //Revenir à la page d'avant avec le bouton précédent du téléphone
    //Par exemple si on se trouve sur la première page
    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }


}
