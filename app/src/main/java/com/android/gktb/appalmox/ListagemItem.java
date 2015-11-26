package com.android.gktb.appalmox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import library.AdapterListView;

/**
 * Created by denis on 26/03/2015.
 */
public class ListagemItem extends Activity {


    TextView txtMaterial;
    TextView txtLote;
    TextView txtLocal;
    TextView txtQtd;
    private static final String TAG_OS = "android";
    private static final String TAG_VER = "ver";
    private static final String TAG_NAME = "name";
    private static final String TAG_API = "api";
    private static final String TAG_QTD = "qt";
    private static final String TAG_SIT = "sit";
    private static final String TAG_DESC = "desc";
    JSONArray android = null;
    ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
    ListView list;
    String codigoLote;
    String id;
    ProgressDialog dialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listagem_layout);
        ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
        oslist = new ArrayList<HashMap<String, String>>();

        Intent it = getIntent();
        codigoLote = it.getStringExtra("lote");
        id = it.getStringExtra("id");

        Button btnSair = (Button) findViewById(R.id.btnSair);
        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListagemItem.this, telaMenu.class);
                startActivity(intent);
                finish();
            }
        });

        new JSONParse(codigoLote, id).execute();

    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        private String lote;
        private String i;

        public JSONParse(String lote, String i){
            this.lote = lote;
            this.i = i;
        }

        public String getLote(){
            return this.lote;
        }

        public String getI(){
            return this.i;
        }

        @Override
        protected void onPreExecute () {
            super.onPreExecute();
            txtMaterial = (TextView) findViewById(R.id.codigoMaterial);
            txtLote = (TextView) findViewById(R.id.codigoLote);
            txtLocal = (TextView) findViewById(R.id.codigoLocal);
            txtQtd = (TextView) findViewById(R.id.quantidade);
            pDialog = new ProgressDialog(ListagemItem.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground (String...args){

            AdapterListView jParser = new AdapterListView();
            // Getting JSON from URL
            String ord = "http://10.55.1.242/nova_intranet/views/ti/web_service_almox2.php?acao=carrega_json&&lote="+getLote();
            JSONObject json = jParser.getJSONFromUrl(ord);

            return json;

        }

        @Override
        protected void onPostExecute (JSONObject json) {
            pDialog.dismiss();
            try {
                // Getting JSON Array from URL
                android = json.getJSONArray(TAG_OS);
                for (int i = 0; i < android.length(); i++) {

                    JSONObject c = android.getJSONObject(i);
                    // Storing  JSON item in a Variable
                    String ver      = c.getString(TAG_VER);
                    String name     = c.getString(TAG_NAME);
                    String api      = c.getString(TAG_API);
                    String situacao = c.getString(TAG_SIT);
                    String qtd      = c.getString(TAG_QTD);
                    String desc      = c.getString(TAG_DESC);
                    // Adding value HashMap key => value
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TAG_VER, ver);
                    map.put(TAG_NAME, name);
                    map.put(TAG_API, api);
                    map.put(TAG_SIT, situacao);
                    map.put(TAG_QTD, qtd);
                    map.put(TAG_DESC, desc);

                    oslist.add(map);
                    list = (ListView) findViewById(R.id.listaVisual);

                    final BaseAdapter adapter = new SimpleAdapter(ListagemItem.this, oslist,
                            R.layout.lista_materiais,
                            new String[]{TAG_VER, TAG_NAME, TAG_API, TAG_SIT, TAG_QTD, TAG_DESC}, new int[]{
                            R.id.codigoMaterial, R.id.codigoLote, R.id.codigoLocal, R.id.codigoSituacao, R.id.quantidade, R.id.codigoMotivo});
                    list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                           // Toast.makeText(ListagemItem.this, "You Clicked at " + oslist.get(+position).get("sit"), Toast.LENGTH_SHORT).show();

                            String s = oslist.get(+position).get("sit");
                            TelaQuantidade(s, getI());

                        }

                    });

                }

            } catch (JSONException e) {
                e.printStackTrace();

            }

        }

        public void TelaQuantidade(final String sit, final String i){

            AlertDialog.Builder alert = new AlertDialog.Builder(ListagemItem.this);

            alert.setTitle("Atenção");
            alert.setMessage("Informe a quantidade a ser liberada");

            final EditText input = new EditText(ListagemItem.this);
            input.setInputType(2);
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = input.getText().toString();
                    // Do something with value!
                    //Toast.makeText(ListagemItem.this, "As informacoes sao: "+sit+" "+i+" "+value, Toast.LENGTH_LONG).show();
                  /*  Intent i = new Intent(ListagemItem.this, ListagemItem.class);
                    startActivity(i); */
                    EnviaDadosFinais(sit, i, value);
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();

        }

        public void EnviaDadosFinais(final String situacao, final String id, final String qt){

            final String[] r = new String[1];

            AsyncTask<String, Integer, String> task = new AsyncTask<String, Integer, String>() {

                ProgressDialog dialog;

                @Override
                protected void onPreExecute(){
                    super.onPreExecute();
                    dialog = new ProgressDialog(ListagemItem.this);
                    dialog.setTitle("Aviso");
                    dialog.setMessage("Aguarde...");
                    dialog.show();
                }

                @Override
                protected String doInBackground(String... params) {

                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost("http://10.55.1.242/nova_intranet/views/ti/web_service_almox.php");

                    try{

                        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                        pairs.add(new BasicNameValuePair("acao","envia_dados"));
                        pairs.add(new BasicNameValuePair("situacao",situacao));
                        pairs.add(new BasicNameValuePair("id",id));
                        pairs.add(new BasicNameValuePair("qtd",qt));
                        post.setEntity(new UrlEncodedFormEntity(pairs));

                        HttpResponse response = client.execute(post);
                        String responseString = EntityUtils.toString(response.getEntity());

                        if(responseString != null){

                           // Toast.makeText(ListagemItem.this, "O retorno foi: "+responseString.trim(), Toast.LENGTH_LONG).show();
                            return responseString.trim();
                        }
                        else {
                            return null;
                        }

                        }catch (Exception e){
                            return null;
                        }

                }

                protected void onPostExecute(String result) {
                    //Cancela progressDialogo
                    dialog.dismiss();
                    //Toast.makeText(ListagemItem.this, "Material liberado com sucesso!", Toast.LENGTH_LONG).show();

                    if (result.equals("qt_maior")){
                        Toast.makeText(ListagemItem.this, "Quantidade informada maior que o saldo do lote!", Toast.LENGTH_LONG).show();
                    }
                    else {
                        AlertDialog.Builder di = new AlertDialog.Builder(ListagemItem.this);
                        di.setTitle("Aviso");
                        di.setMessage("Gostaria de imprimir a etiqueta ?");

                        di.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                ImprimeEtiqueta etiqueta = new ImprimeEtiqueta();
                                etiqueta.setContext(ListagemItem.this);
                                etiqueta.setId(id);
                                etiqueta.ImprimirEtiqueta();

                                Intent it = new Intent(ListagemItem.this, telaMenu.class);
                                startActivity(it);
                                finish();

                            }
                        });

                        di.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Canceled.
                                Intent it = new Intent(ListagemItem.this, telaMenu.class);
                                startActivity(it);
                                finish();
                            }
                        });
                        di.show();
                    }

                }

            };
            task.execute();

        }

    }

}


