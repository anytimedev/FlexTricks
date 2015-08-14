package com.anytimedev.flextricks.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.anytimedev.flextricks.R;
import com.anytimedev.flextricks.controller.MascaraMonetaria;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edttAlcool;
    private EditText edttGasolina;
    private TextView buttCalcular;
    private TextView txtvCompensacao;
    private RelativeLayout microView1;
    private RelativeLayout microView2;
    private TextView alcoolView;
    private TextView gasView;
    protected static final int RESULT_SPEECH_ALC = 1;
    protected static final int RESULT_SPEECH_GAS = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        edttAlcool      = (EditText) findViewById(R.id.edttAlcool);
        edttGasolina    = (EditText) findViewById(R.id.edttGasolina);
        buttCalcular    = (TextView) findViewById(R.id.txtvCalcular);
        txtvCompensacao = (TextView) findViewById(R.id.txtvCompensacao);
        microView1      = (RelativeLayout) findViewById(R.id.microView);
        microView2      = (RelativeLayout) findViewById(R.id.microView2);
        alcoolView      = (TextView) findViewById(R.id.alcoolView);
        gasView         = (TextView) findViewById(R.id.gasView);


        alcoolView.setVisibility(View.GONE);
        gasView.setVisibility(View.GONE);
        txtvCompensacao.setVisibility(View.GONE);
        edttAlcool.addTextChangedListener(new MascaraMonetaria(edttAlcool));
        edttGasolina.addTextChangedListener(new MascaraMonetaria(edttGasolina));

        buttCalcular.setOnClickListener(this);

        microView1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "pt-BR");

                try {
                    startActivityForResult(intent, RESULT_SPEECH_ALC);
                    edttAlcool.setText("");
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });

        microView2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "pt-BR");

                try {
                    startActivityForResult(intent, RESULT_SPEECH_GAS);
                    edttGasolina.setText("");
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtvCalcular:
                String valorAlcool = edttAlcool.getText().toString();
                valorAlcool = valorAlcool.replace("R", "").replace("$", "").replace(".", "")
                        .replace(",", "");
                String valorGasolina = edttGasolina.getText().toString();
                valorGasolina = valorGasolina.replace("R", "").replace("$", "").replace(".", "")
                        .replace(",", "");

                Double resultado = Double.parseDouble(valorAlcool) / Double.parseDouble(valorGasolina);

                System.out.println("valor = " + resultado);

                if(resultado < 0.70){
                    gasView.setVisibility(View.GONE);
                    txtvCompensacao.setVisibility(View.VISIBLE);
                    txtvCompensacao.setText("Compensa mais colocar Álcool");
                    alcoolView.setVisibility(View.VISIBLE);
                }else{
                    alcoolView.setVisibility(View.GONE);
                    txtvCompensacao.setVisibility(View.VISIBLE);
                    txtvCompensacao.setText("Compensa mais colocar Gasolina");
                    gasView.setVisibility(View.VISIBLE);
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH_ALC: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    edttAlcool.setText("R$"+text.get(0).replace(".", ","));
                }
                break;
            }
            case RESULT_SPEECH_GAS: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    edttGasolina.setText("R$"+text.get(0).replace(".", ","));
                }
                break;
            }

        }
    }
}
