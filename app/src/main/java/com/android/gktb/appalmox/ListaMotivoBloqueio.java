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
 * Created by denis on 01/04/2015.
 */
public class ListaMotivoBloqueio extends Activity {

    private String codigoLote;
    ProgressDialog dialog;
    JSONArray estoque = null;
    String TAG_PRIN = "estoque";
    String TAG_MAT = "descricaoBloqueio";
    String TAG_LOTE = "codBloqueio";

    ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
    ListView lista;
    String lote;
    private String material;
    private String qtd;
    private String motivo;
    String localOri;

    protected void onCreate(Bundle icicle){

        super.onCreate(icicle);
        setContentView(R.layout.layout_motivo_bloqueio);

        Intent it = getIntent();
        lote = it.getStringExtra("codLote");
        material = it.getStringExtra("material");
        localOri = it.getStringExtra("codLocal");
        setCodigoLote(lote);
        setMaterial(material);

        //Toast.makeText(ListaEstoque.this, "You Clicked at " + getCodigoMaterial(), Toast.LENGTH_SHORT).show();

        CarregaEstoque();

        Button btnSair = (Button) findViewById(R.id.btnSairMotivo);
        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListaMotivoBloqueio.this, telaMenu.class);
                startActivity(i);
                finish();
            }
        });

    }

    public void CarregaEstoque(){

        AsyncTask<String, String, JSONObject> task = new AsyncTask<String, String, JSONObject>() {

            protected void onPreExecute(){
                dialog = new ProgressDialog(ListaMotivoBloqueio.this);
                dialog.setMessage("Carregando ...");
                dialog.setIndeterminate(false);
                dialog.setCancelable(true);
                dialog.show();
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                AdapterListView jParser = new AdapterListView();
                // Getting JSON from URL
                String ord = "http://10.55.1.242/nova_intranet/views/ti/web_service_almox2.php?acao=motivo_bloqueio_json";
                JSONObject json = jParser.getJSONFromUrl(ord);

                return json;
            }

            protected void onPostExecute(JSONObject json){
                dialog.dismiss();

                try {

                    estoque = json.getJSONArray(TAG_PRIN);

                    for (int i = 0; i < estoque.length(); i++) {

                        JSONObject c = estoque.getJSONObject(i);

                        String cod_material = c.getString(TAG_MAT);
                        String cod_lote     = c.getString(TAG_LOTE);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_MAT, cod_material);
                        map.put(TAG_LOTE, cod_lote);

                        oslist.add(map);

                        lista = (ListView) findViewById(R.id.listaMotivoBloqueio);
                        final BaseAdapter adapter = new SimpleAdapter(ListaMotivoBloqueio.this, oslist, R.layout.estoque_custom_motivo,
                                new String[] {TAG_MAT, TAG_LOTE},
                                new int[] {R.id.txtDescricaoMotivo, R.id.txtCodMotivo});
                        lista.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                //Toast.makeText(ListaMotivoBloqueio.this, "O item clicado foi: " + oslist.get(+position).get("codBloqueio"), Toast.LENGTH_SHORT).show();

                                setMotivo(oslist.get(+position).get("codBloqueio"));

                                AlertDialog.Builder bloqMat = new AlertDialog.Builder(ListaMotivoBloqueio.this);

                                bloqMat.setTitle("Aviso");
                                bloqMat.setMessage("Informe a quantidade a ser bloqueada!");

                                final EditText input = new EditText(ListaMotivoBloqueio.this);
                                input.setInputType(2);
                                bloqMat.setView(input);

                                bloqMat.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                      String value = input.getText().toString();
                                      setQtd(value);
                                      //  Toast.makeText(ListaMotivoBloqueio.this,"A quantidade digitada foi: "+value,Toast.LENGTH_LONG).show();
                                      ValidaQuantidade(getCodigoLote(), value);
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

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        };
        task.execute();

    }

    public String getCodigoLote() {
        return codigoLote;
    }

    public void setCodigoLote(String codigoLote) {
        this.codigoLote = codigoLote;
    }

    public void ValidaQuantidade(final String lot, final String qtd){

        AsyncTask<String, Object, String> task = new AsyncTask<String, Object, String>() {

            @Override
            protected String doInBackground(String... paramss) {

                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://10.55.1.242/nova_intranet/views/ti/web_service_almox.php");

                try {

                    List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                    pairs.add(new BasicNameValuePair("acao", "valida_qtd"));
                    pairs.add(new BasicNameValuePair("lote", lot));
                    pairs.add(new BasicNameValuePair("qtd", qtd));
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
                dialog = new ProgressDialog(ListaMotivoBloqueio.this);
                dialog.setMessage("Aguarde...");
                dialog.show();
            }

            protected void onPostExecute(String result) {
                //Cancela progressDialogo
                dialog.dismiss();

                String res = result;

                if(res.equals("1")){
                    //Toast.makeText(ListaMotivoBloqueio.this,"A quantidade esta OK "+res,Toast.LENGTH_LONG).show();

                    AlertDialog.Builder bloqMat = new AlertDialog.Builder(ListaMotivoBloqueio.this);

                    bloqMat.setTitle("Aviso");
                    bloqMat.setMessage("Informe o local de bloqueio!");

                    final EditText input = new EditText(ListaMotivoBloqueio.this);
                    input.setInputType(1);
                    bloqMat.setView(input);

                    bloqMat.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = input.getText().toString();
                            //Toast.makeText(ListaMotivoBloqueio.this,"O local digitado foi: "+value,Toast.LENGTH_LONG).show();
                            ValidaLocal(value);
                        }
                    });

                    bloqMat.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                        }
                    });

                    bloqMat.show();

                }
                else{
                    Toast.makeText(ListaMotivoBloqueio.this,"Quantidade informada maior que o disponivel para bloqueio! ",Toast.LENGTH_LONG).show();
                }



            }
        };
        task.execute();

    }

    public void ValidaLocal(final String local){

        AsyncTask<String, Object, String> task = new AsyncTask<String, Object, String>() {

            @Override
            protected String doInBackground(String... paramss) {

                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://10.55.1.242/nova_intranet/views/ti/web_service_almox.php");

                try {

                    List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                    pairs.add(new BasicNameValuePair("acao", "valida_local"));
                    pairs.add(new BasicNameValuePair("local", local));
                    pairs.add(new BasicNameValuePair("localOri", localOri));
                    pairs.add(new BasicNameValuePair("material", getMaterial()));
                    pairs.add(new BasicNameValuePair("lote", getCodigoLote()));
                    pairs.add(new BasicNameValuePair("codigoBloq", getMotivo()));
                    pairs.add(new BasicNameValuePair("qtd", getQtd()));
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
                dialog = new ProgressDialog(ListaMotivoBloqueio.this);
                dialog.setMessage("Aguarde...");
                dialog.show();
            }

            protected void onPostExecute(String result) {
                //Cancela progressDialogo
                dialog.dismiss();

                String res = result;

                if(res.equals("1")){
                    //Toast.makeText(ListaMotivoBloqueio.this,"O local está OK "+res,Toast.LENGTH_LONG).show();

                    AlertDialog.Builder bloqMat = new AlertDialog.Builder(ListaMotivoBloqueio.this);

                    bloqMat.setTitle("Aviso");
                    bloqMat.setMessage("Material bloqueado com sucesso!");

                    bloqMat.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Toast.makeText(ListaMotivoBloqueio.this,"Ok finishi",Toast.LENGTH_LONG).show();
                            Intent i = new Intent(ListaMotivoBloqueio.this,telaMenu.class);
                            startActivity(i);
                            finish();
                        }
                    });

                    bloqMat.show();

                }
                else{
                    Toast.makeText(ListaMotivoBloqueio.this,"Esse local não existe, favor verificar!",Toast.LENGTH_LONG).show();
                }



            }
        };
        task.execute();

    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getQtd() {
        return qtd;
    }

    public void setQtd(String qtd) {
        this.qtd = qtd;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
