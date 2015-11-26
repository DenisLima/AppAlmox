package com.android.gktb.appalmox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;


public class telaMenu extends Activity {

    String ultimoID;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_menu);

        Button bloqueiaMaterial = (Button) findViewById(R.id.btnBloqueia);
        bloqueiaMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder bloqMat = new AlertDialog.Builder(telaMenu.this);

                bloqMat.setTitle("Aviso");
                bloqMat.setMessage("Informe o código do material!");


                final EditText input = new EditText(telaMenu.this);
                input.setInputType(1);
                bloqMat.setView(input);

                bloqMat.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        // Do something with value!
                        //Toast.makeText(telaMenu.this, "As informacoes sao: "+value, Toast.LENGTH_LONG).show();
                  /*    Intent i = new Intent(ListagemItem.this, ListagemItem.class);
                        startActivity(i); */
                        //EnviaDadosFinais(sit, i, value);

                        Intent it = new Intent(telaMenu.this, ListaMateriaisBloqueio.class);
                        it.putExtra("mat",value);
                        startActivity(it);
                        finish();

                    }
                });

                bloqMat.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                bloqMat.show();

            }
        });

        Button liberarMaterial = (Button) findViewById(R.id.btnLibera);
        liberarMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                startActivityForResult(intent, 0);


            }
        });

        Button btnSair = (Button) findViewById(R.id.btnSair);
        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });

        Button btnConsultar = (Button) findViewById(R.id.btnConsulta);
        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(telaMenu.this, telaConsultaMaterial.class);
                startActivity(i);

             /*   AlertDialog.Builder alert = new AlertDialog.Builder(telaMenu.this);

                alert.setTitle("Atenção");
                alert.setMessage("Informe o código do material!");

                final EditText input = new EditText(telaMenu.this);
                input.setInputType(1);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        // Do something with value!
                        //Toast.makeText(telaMenu.this, "As informacoes sao: "+value, Toast.LENGTH_LONG).show();
                  /*    Intent i = new Intent(ListagemItem.this, ListagemItem.class);
                        startActivity(i);
                        //EnviaDadosFinais(sit, i, value);

                        Intent it = new Intent(telaMenu.this, ListaMateriais.class);
                        it.putExtra("mat",value);
                        startActivity(it);
                        finish();

                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();*/

            }
        });

    }

    //Recebe o resultado da primeira leitura
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

                String contents = intent.getStringExtra("SCAN_RESULT");
                ValidaLeituraMaterial(contents);

                //Toast.makeText(this, "Deu certo porra: "+contents, Toast.LENGTH_LONG).show();

            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
                Toast.makeText(this, "Deu erro", Toast.LENGTH_LONG).show();
            }
        }
        else if(requestCode == 1){
            if (resultCode == RESULT_OK) {

                String contents = intent.getStringExtra("SCAN_RESULT");
                ValidaLeituraLote(contents, ultimoID);

                //Toast.makeText(this, "Deu certo porra: "+contents, Toast.LENGTH_LONG).show();

            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
                Toast.makeText(this, "Deu erro", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void ValidaLeituraMaterial(final String mat){

            AsyncTask<String, Object, String> task = new AsyncTask<String, Object, String>() {

                @Override
                protected String doInBackground(String... paramss) {

                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost("http://10.55.1.242/nova_intranet/views/ti/web_service_almox.php");

                    try {

                        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                        pairs.add(new BasicNameValuePair("acao", "valida_material"));
                        pairs.add(new BasicNameValuePair("material", mat));
                        post.setEntity(new UrlEncodedFormEntity(pairs));

                        HttpResponse response = client.execute(post);
                        String responseString = EntityUtils.toString(response.getEntity());

                        return responseString.trim();

                    } catch (Exception e) {
                        return null;
                    }

                }

                @Override
                protected void onPreExecute() {
                    //Cria novo um ProgressDialogo e exibe
                    progressDialog = new ProgressDialog(telaMenu.this);
                    progressDialog.setMessage("Aguarde...");
                    progressDialog.show();
                }

                protected void onPostExecute(String result) {
                    //Cancela progressDialogo
                    progressDialog.dismiss();

                    final String r[] = result.split(";");

                    if (r[0].equals("material_nc")) {
                        Toast.makeText(telaMenu.this, "Esse material não existe, efetue a leitura novamente!", Toast.LENGTH_LONG).show();
                    } else {

                        new AlertDialog.Builder(telaMenu.this)
                                .setMessage("Agora efetue a leitura do NR")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        ultimoID = r[1];
                                        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                                        startActivityForResult(intent, 1);

                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                        //Toast.makeText(getContext(), "Material OK: "+r[1], Toast.LENGTH_LONG).show();
                    }
                }
            };
            task.execute();

    }


    public void ValidaLeituraLote(final String lot, final String id){

        AsyncTask<String, Object, String> task = new AsyncTask<String, Object, String>() {

            @Override
            protected String doInBackground(String... paramss) {

                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://10.55.1.242/nova_intranet/views/ti/web_service_almox.php");

                try {

                    List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                    pairs.add(new BasicNameValuePair("acao", "valida_lote"));
                    pairs.add(new BasicNameValuePair("lote", lot));
                    pairs.add(new BasicNameValuePair("id", id));
                    post.setEntity(new UrlEncodedFormEntity(pairs));

                    HttpResponse response = client.execute(post);
                    String responseString = EntityUtils.toString(response.getEntity());

                    return responseString.trim();

                } catch (Exception e) {
                    return null;
                }

            }

            @Override
            protected void onPreExecute() {
                //Cria novo um ProgressDialogo e exibe
                progressDialog = new ProgressDialog(telaMenu.this);
                progressDialog.setMessage("Aguarde...");
                progressDialog.show();
            }

            protected void onPostExecute(String result) {
                //Cancela progressDialogo
                progressDialog.dismiss();

                String r[] = result.split(";");

                if (r[0].equals("lote_nc")) {
                    Toast.makeText(telaMenu.this, "Esse lote não existe ou não possui quantidades bloqueadas!", Toast.LENGTH_LONG).show();
                } else {

                    //TelaQuantidade();
                    Intent i = new Intent(telaMenu.this, ListagemItem.class);
                    i.putExtra("id", id);
                    i.putExtra("lote", r[1]);
                    startActivity(i);
                    finish();
                    //Toast.makeText(telaMenu.this, "Lote OK: "+r[4], Toast.LENGTH_LONG).show();
                }
            }
        };
        task.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tela_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
