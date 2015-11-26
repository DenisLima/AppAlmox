package com.android.gktb.appalmox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import library.AdapterListView;

/**
 * Created by denis on 28/05/15.
 */
public class ListaEstoqueBloqueadoCustom extends Activity {

    private String lote;
    ProgressDialog dialog;

    JSONArray estoque = null;
    String TAG_PRIN = "estoque";
    String TAG_MAT = "material";
    String TAG_LOTE = "lote";
    String TAG_LOCAL = "local";
    String TAG_DATA = "data";
    String TAG_QTD = "motivo";
    String TAG_MOTIVO = "qtd";
    ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
    ListView lista;

    protected void onCreate(Bundle icicle){
        super.onCreate(icicle);
        setContentView(R.layout.layout_lista_bloq_custom);

        Intent it = getIntent();
        setLote(it.getStringExtra("lote"));

        Button btnVoltar = (Button) findViewById(R.id.btnVoltarBloqCustom);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btnSair = (Button) findViewById(R.id.btnSairBloqCustom);
        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListaEstoqueBloqueadoCustom.this, telaMenu.class);
                startActivity(i);
                finish();
            }
        });

        CarregaDetalheEstoque();

    }

    public void CarregaDetalheEstoque(){
        AsyncTask<String, String, JSONObject> task = new AsyncTask<String, String, JSONObject>() {

            protected void onPreExecute(){
                dialog = new ProgressDialog(ListaEstoqueBloqueadoCustom.this);
                dialog.setMessage("Carregando ...");
                dialog.setIndeterminate(false);
                dialog.setCancelable(true);
                dialog.show();
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                AdapterListView jParser = new AdapterListView();
                // Getting JSON from URL
                String ord = "http://10.55.1.242/nova_intranet/views/ti/web_service_almox2.php?acao=carrega_json5&&lote="+getLote();
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
                        String cod_local    = c.getString(TAG_LOCAL);
                        String qtd      = c.getString(TAG_QTD);
                        String data       = c.getString(TAG_DATA);
                        String motivo      = c.getString(TAG_MOTIVO);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_MAT, cod_material);
                        map.put(TAG_LOTE, cod_lote);
                        map.put(TAG_LOCAL, cod_local);
                        map.put(TAG_QTD, qtd);
                        map.put(TAG_DATA, data);
                        map.put(TAG_MOTIVO, motivo);

                        oslist.add(map);

                        lista = (ListView) findViewById(R.id.listagemDestalheBloq);
                        BaseAdapter adapter = new SimpleAdapter(ListaEstoqueBloqueadoCustom.this, oslist, R.layout.bloqueio_custom,
                                new String[] {TAG_MAT, TAG_LOTE, TAG_LOCAL, TAG_MOTIVO, TAG_DATA, TAG_QTD},
                                new int[] {R.id.matCustom, R.id.loteCustom, R.id.localCustom, R.id.motivoCustom,
                                R.id.dataCustom, R.id.qtdCustom});
                        lista.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                // Toast.makeText(ListaEstoque.this, "You Clicked at " + oslist.get(+position).get("lote"), Toast.LENGTH_SHORT).show();
                                //BuscaBloqueio(oslist.get(+position).get("lote"));
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

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }
}
