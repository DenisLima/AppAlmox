package com.android.gktb.appalmox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import library.AdapterListView;

/**
 * Created by denis on 26/05/15.
 */
public class telaConsultaBloqueado extends Activity {

    ProgressDialog dialog;
    JSONArray estoque = null;
    String TAG_PRIN = "estoque";
    String TAG_MAT = "descricaoBloqueio";
    String TAG_LOTE = "codBloqueio";
    ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
    ListView lista;
    private String material;
    private String disponivel;
    private String reservada;
    private String pedido;
    private String processo;
    private String inspecao;
    private String bloqueado;
    private String emterc;

    protected void onCreate(Bundle icicle){
        super.onCreate(icicle);
        setContentView(R.layout.layout_consulta_bloqueado);

        Intent it = getIntent();
        setMaterial(it.getStringExtra("mat"));
        setDisponivel(it.getStringExtra("disponivel"));
        setReservada(it.getStringExtra("reservada"));
        setPedido(it.getStringExtra("pedido"));
        setProcesso(it.getStringExtra("processo"));
        setInspecao(it.getStringExtra("inspecao"));
        setBloqueado(it.getStringExtra("bloqueado"));
        setEmterc(it.getStringExtra("emterc"));

        AsyncTask<String, String, JSONObject> task = new AsyncTask<String, String, JSONObject>() {

            protected void onPreExecute(){
                dialog = new ProgressDialog(telaConsultaBloqueado.this);
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

                        final String cod_material = c.getString(TAG_MAT);
                        String cod_lote     = c.getString(TAG_LOTE);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_MAT, cod_material);
                        map.put(TAG_LOTE, cod_lote);

                        oslist.add(map);

                        lista = (ListView) findViewById(R.id.listagemDestalheBloq);
                        final BaseAdapter adapter = new SimpleAdapter(telaConsultaBloqueado.this, oslist, R.layout.estoque_custom_motivo,
                                new String[] {TAG_MAT, TAG_LOTE},
                                new int[] {R.id.txtDescricaoMotivo, R.id.txtCodMotivo});
                        lista.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                String codBloqueio = oslist.get(+position).get("codBloqueio");

                                Intent it = new Intent(telaConsultaBloqueado.this, ListaMateriais.class);
                                it.putExtra("par", "bloq");
                                it.putExtra("motivo", codBloqueio);
                                it.putExtra("mat", getMaterial());
                                it.putExtra("disponivel", getDisponivel());
                                it.putExtra("reservada", getReservada());
                                it.putExtra("pedido", getPedido());
                                it.putExtra("inspecao", getInspecao());
                                it.putExtra("processo", getProcesso());
                                it.putExtra("bloqueado", getBloqueado());
                                it.putExtra("emterc", getEmterc());
                                startActivity(it);
                                finish();

                                //Toast.makeText(telaConsultaBloqueado.this, "O item clicado foi: " + codBloqueio, Toast.LENGTH_SHORT).show();
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

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }
}
