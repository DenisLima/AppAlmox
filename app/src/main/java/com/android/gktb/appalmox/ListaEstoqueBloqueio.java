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
 * Created by denis on 01/04/2015.
 */
public class ListaEstoqueBloqueio extends Activity {

    private String codigoMaterial;
    ProgressDialog dialog;
    JSONArray estoque = null;
    String TAG_PRIN = "estoque";
    String TAG_MAT = "material";
    String TAG_LOTE = "lote";
    String TAG_LOCAL = "local";
    String TAG_DISP = "disponivel";
    String TAG_RES = "reservado";
    String TAG_BLOQ = "bloqueado";
    String TAG_PRO = "processo";
    ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
    ListView lista;
    String material;

    protected void onCreate(Bundle icicle){

        super.onCreate(icicle);
        setContentView(R.layout.listagem_consulta_lotes);

        Intent it = getIntent();
        material = it.getStringExtra("material");
        setCodigoMaterial(material);

        //Toast.makeText(ListaEstoque.this, "You Clicked at " + getCodigoMaterial(), Toast.LENGTH_SHORT).show();

        CarregaEstoque();

        Button btnSair = (Button) findViewById(R.id.btnSairEstoque);
        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListaEstoqueBloqueio.this, telaMenu.class);
                startActivity(i);
                finish();
            }
        });

    }

    public void CarregaEstoque(){

        AsyncTask<String, String, JSONObject> task = new AsyncTask<String, String, JSONObject>() {

            protected void onPreExecute(){
                dialog = new ProgressDialog(ListaEstoqueBloqueio.this);
                dialog.setMessage("Carregando ...");
                dialog.setIndeterminate(false);
                dialog.setCancelable(true);
                dialog.show();
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                AdapterListView jParser = new AdapterListView();
                // Getting JSON from URL
                String ord = "http://10.55.1.242/nova_intranet/views/ti/web_service_almox2.php?acao=carrega_json4&&mate="+getCodigoMaterial();
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
                        final String cod_lote     = c.getString(TAG_LOTE);
                        String cod_local    = c.getString(TAG_LOCAL);
                        String qtdDisp      = c.getString(TAG_DISP);
                        String qtdRes       = c.getString(TAG_RES);
                        String qtdBloq      = c.getString(TAG_BLOQ);
                        String qtdProc      = c.getString(TAG_PRO);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_MAT, cod_material);
                        map.put(TAG_LOTE, cod_lote);
                        map.put(TAG_LOCAL, cod_local);
                        map.put(TAG_DISP, qtdDisp);
                        map.put(TAG_RES, qtdRes);
                        map.put(TAG_BLOQ, qtdBloq);
                        map.put(TAG_PRO, qtdProc);

                        oslist.add(map);

                        lista = (ListView) findViewById(R.id.listaEstoque);
                        BaseAdapter adapter = new SimpleAdapter(ListaEstoqueBloqueio.this, oslist, R.layout.estoque_custom,
                                new String[] {TAG_MAT, TAG_LOTE, TAG_LOCAL, TAG_DISP, TAG_RES, TAG_BLOQ, TAG_PRO},
                                new int[] {R.id.txtMaterialEstoque, R.id.txtLoteEstoque, R.id.txtLocalEstoque, R.id.txtDisponivelEstoque,
                                        R.id.txtReservadoEstoque, R.id.txtBloqueadoEstoque, R.id.txtProcessoEstoque});
                        lista.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                //Toast.makeText(ListaEstoqueBloqueio.this, "O item clicado foi: " + oslist.get(+position).get("lote"), Toast.LENGTH_SHORT).show();
                                Intent it = new Intent(ListaEstoqueBloqueio.this,ListaMotivoBloqueio.class);
                                String codLote = oslist.get(+position).get("lote");
                                String codLocal = oslist.get(+position).get("local");
                                it.putExtra("codLote",codLote);
                                it.putExtra("codLocal", codLocal);
                                it.putExtra("material",material);
                                startActivity(it);


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

    public String getCodigoMaterial() {
        return codigoMaterial;
    }

    public void setCodigoMaterial(String codigoMaterial) {
        this.codigoMaterial = codigoMaterial;
    }
}
