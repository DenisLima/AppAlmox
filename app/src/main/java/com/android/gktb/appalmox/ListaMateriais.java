package com.android.gktb.appalmox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
 * Created by denis on 31/03/2015.
 */
public class ListaMateriais extends Activity {

    private String material;
    ProgressDialog dialog;
    JSONArray materiais = null;
    String TAG_DESC = "material";
    String TAG_COD_MATERIAL = "mat";
    String TAG_COD_FORN = "cod_forn";
    ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
    ListView lista;
    private Context context;

    private String disponivel;
    private String reservada;
    private String pedido;
    private String processo;
    private String inspecao;
    private String bloqueado;
    private String emterc;

    public String par;
    public String motivo;

    @Override
    protected void onCreate(Bundle icicle){
        super.onCreate(icicle);
        setContentView(R.layout.layout_lista_materiais);

        Intent it = getIntent();

        par = it.getStringExtra("par");
        motivo = it.getStringExtra("motivo");

        setMaterial(it.getStringExtra("mat"));
        setDisponivel(it.getStringExtra("disponivel"));
        setReservada(it.getStringExtra("reservada"));
        setPedido(it.getStringExtra("pedido"));
        setProcesso(it.getStringExtra("processo"));
        setInspecao(it.getStringExtra("inspecao"));
        setBloqueado(it.getStringExtra("bloqueado"));
        setEmterc(it.getStringExtra("emterc"));

        ObtemListaMateriais();

        Button btnVoltar = (Button) findViewById(R.id.btnSairLista);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListaMateriais.this, telaMenu.class);
                startActivity(i);
                finish();
            }
        });

    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public void ObtemListaMateriais(){

        AsyncTask<String, String, JSONObject> task = new AsyncTask<String, String, JSONObject>() {

            @Override
            protected void onPreExecute(){
                dialog = new ProgressDialog(ListaMateriais.this);
                dialog.setMessage("Carregando ...");
                dialog.setIndeterminate(false);
                dialog.setCancelable(true);
                dialog.show();
            }

            @Override
            protected JSONObject doInBackground(String... args) {

                AdapterListView jParser = new AdapterListView();
                // Getting JSON from URL
                String ord = "http://10.55.1.242/nova_intranet/views/ti/web_service_almox2.php?acao=carrega_json2&&mat="+getMaterial();
                JSONObject json = jParser.getJSONFromUrl(ord);

                return json;
            }

            @Override
            protected void onPostExecute(JSONObject json){

                dialog.dismiss();

                try {

                    materiais = json.getJSONArray(TAG_DESC);

                    for (int i = 0; i < materiais.length(); i++) {

                        JSONObject c = materiais.getJSONObject(i);

                        String cod_material = c.getString(TAG_COD_MATERIAL);
                        String cod_forn     = c.getString(TAG_COD_FORN);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_COD_MATERIAL, cod_material);
                        map.put(TAG_COD_FORN, cod_forn);

                        oslist.add(map);

                        lista = (ListView) findViewById(R.id.listaMateriais);
                        BaseAdapter adapter = new SimpleAdapter(ListaMateriais.this, oslist, R.layout.lista_materiais_custom,
                                new String[] {TAG_COD_FORN, TAG_COD_MATERIAL}, new int[] {R.id.txtCodForn, R.id.txtCodMaterial});
                        lista.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //Toast.makeText(ListaMateriais.this, "You Clicked at " + oslist.get(+position).get("mat"), Toast.LENGTH_SHORT).show();
                                String value = oslist.get(+position).get("mat");
                                //Toast.makeText(ListaMateriais.this, "You Clicked at " + value, Toast.LENGTH_SHORT).show();
                                Intent it = new Intent(ListaMateriais.this, ListaEstoque.class);
                                it.putExtra("material",value);
                                it.putExtra("par", par);
                                it.putExtra("motivo", motivo);
                                it.putExtra("disponivel",getDisponivel());
                                it.putExtra("reservada",getReservada());
                                it.putExtra("inspecao",getInspecao());
                                it.putExtra("processo",getProcesso());
                                it.putExtra("pedido",getPedido());
                                it.putExtra("emterc",getEmterc());
                                it.putExtra("bloqueado",getBloqueado());
                                startActivity(it);
                                finish();
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

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getDisponivel() {
        return disponivel;
    }

    public void setDisponivel(String disponivel) {
        this.disponivel = disponivel;
    }

    public String getReservada() {
        return reservada;
    }

    public void setReservada(String reservada) {
        this.reservada = reservada;
    }

    public String getPedido() {
        return pedido;
    }

    public void setPedido(String pedido) {
        this.pedido = pedido;
    }

    public String getProcesso() {
        return processo;
    }

    public void setProcesso(String processo) {
        this.processo = processo;
    }

    public String getInspecao() {
        return inspecao;
    }

    public void setInspecao(String inspecao) {
        this.inspecao = inspecao;
    }

    public String getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(String bloqueado) {
        this.bloqueado = bloqueado;
    }

    public String getEmterc() {
        return emterc;
    }

    public void setEmterc(String emterc) {
        this.emterc = emterc;
    }
}
