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
public class ListaEstoque extends Activity {

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

    private String disponivel;
    private String reservado;
    private String inspecao;
    private String processo;
    private String pedido;
    private String bloqueio;
    private String emterc;

    public String par;
    public String motivo;

    protected void onCreate(Bundle icicle){

        super.onCreate(icicle);
        setContentView(R.layout.listagem_consulta_lotes);

        Intent it = getIntent();
        material = it.getStringExtra("material");
        par = it.getStringExtra("par");
        motivo = it.getStringExtra("motivo");
        setDisponivel(it.getStringExtra("disponivel"));
        setReservado(it.getStringExtra("reservada"));
        setInspecao(it.getStringExtra("inspecao"));
        setProcesso(it.getStringExtra("processo"));
        setPedido(it.getStringExtra("pedido"));
        setBloqueio(it.getStringExtra("bloqueado"));
        setEmterc(it.getStringExtra("emterc"));
        setCodigoMaterial(material);

        //Toast.makeText(ListaEstoque.this, "You Clicked at " + getCodigoMaterial(), Toast.LENGTH_SHORT).show();

        CarregaEstoque();

        Button btnSair = (Button) findViewById(R.id.btnSairEstoque);
        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListaEstoque.this, telaMenu.class);
                startActivity(i);
                finish();
            }
        });

    }

    public void CarregaEstoque(){

        AsyncTask<String, String, JSONObject> task = new AsyncTask<String, String, JSONObject>() {

            protected void onPreExecute(){
                dialog = new ProgressDialog(ListaEstoque.this);
                dialog.setMessage("Carregando ...");
                dialog.setIndeterminate(false);
                dialog.setCancelable(true);
                dialog.show();
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                AdapterListView jParser = new AdapterListView();
                // Getting JSON from URL
                String ord = "http://10.55.1.242/nova_intranet/views/ti/web_service_almox2.php?acao=carrega_json3&&mate="+getCodigoMaterial()+"&&disp="+getDisponivel()
                        +"&&res="+getReservado()+"&&pedido="+getPedido()+"&&proc="+getProcesso()+"&&insp="+getInspecao()+"&&bloq="+getBloqueio()+
                        "&&emterc="+getEmterc()+"&&par="+par+"&&motivo="+motivo;
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
                        BaseAdapter adapter = new SimpleAdapter(ListaEstoque.this, oslist, R.layout.estoque_custom,
                                new String[] {TAG_MAT, TAG_LOTE, TAG_LOCAL, TAG_DISP, TAG_RES, TAG_BLOQ, TAG_PRO},
                                new int[] {R.id.txtMaterialEstoque, R.id.txtLoteEstoque, R.id.txtLocalEstoque, R.id.txtDisponivelEstoque,
                                           R.id.txtReservadoEstoque, R.id.txtBloqueadoEstoque, R.id.txtProcessoEstoque});
                        lista.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                               // Toast.makeText(ListaEstoque.this, "You Clicked at " + oslist.get(+position).get("lote"), Toast.LENGTH_SHORT).show();
                               BuscaBloqueio(oslist.get(+position).get("lote"));
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

    public void BuscaBloqueio(final String lote){

        AsyncTask<String, Object, String> asyncTask = new AsyncTask<String, Object, String>() {

            @Override
            protected void onPreExecute(){
                dialog = new ProgressDialog(ListaEstoque.this);
                dialog.setMessage("Aguarde...");
                dialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://10.55.1.242/nova_intranet/views/ti/web_service_almox.php");

                try {

                    List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                    pairs.add(new BasicNameValuePair("acao", "carrega_lote_bloqueado"));
                    pairs.add(new BasicNameValuePair("lote", lote));
                    post.setEntity(new UrlEncodedFormEntity(pairs));

                    HttpResponse response = client.execute(post);
                    String responseString = EntityUtils.toString(response.getEntity());

                    return responseString.trim();

                } catch (Exception e) {
                    return null;
                }
            }

            protected void onPostExecute(String result) {

                dialog.dismiss();

                //Toast.makeText(ListaEstoque.this, "Verifique: "+result, Toast.LENGTH_LONG).show();

                if(result.equals("")){
                    Toast.makeText(ListaEstoque.this, "Esse material não possui estoque bloqueado!", Toast.LENGTH_LONG).show();
                }
                else {

                    String r[] = result.split(";");

                /*    AlertDialog.Builder bloqMat = new AlertDialog.Builder(ListaEstoque.this);

                    bloqMat.setTitle("Estoque Bloqueado");
                    bloqMat.setMessage("Código Lote: "+r[0]+"\n\n" +
                                       "Data: "+r[1]+"\n\n" +
                                       "Quantidade: "+r[2]+"\n\n"+
                                       "Motivo: "+r[3]+"\n\n"+
                                       "Usuário: "+r[4] );

                    bloqMat.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //String value = input.getText().toString();
                            //Toast.makeText(ListaMotivoBloqueio.this,"A quantidade digitada foi: "+value,Toast.LENGTH_LONG).show();
                            //ValidaQuantidade(getCodigoLote(), value);
                        }
                    });

                    bloqMat.show(); */

                    Intent intent = new Intent(ListaEstoque.this, ListaEstoqueBloqueadoCustom.class);
                    intent.putExtra("lote", r[0]);
                    startActivity(intent);

                }

            }

        };
        asyncTask.execute();

    }

    public String getCodigoMaterial() {
        return codigoMaterial;
    }

    public void setCodigoMaterial(String codigoMaterial) {
        this.codigoMaterial = codigoMaterial;
    }

    public String getDisponivel() {
        return disponivel;
    }

    public void setDisponivel(String disponivel) {
        this.disponivel = disponivel;
    }

    public String getReservado() {
        return reservado;
    }

    public void setReservado(String reservado) {
        this.reservado = reservado;
    }

    public String getInspecao() {
        return inspecao;
    }

    public void setInspecao(String inspecao) {
        this.inspecao = inspecao;
    }

    public String getProcesso() {
        return processo;
    }

    public void setProcesso(String processo) {
        this.processo = processo;
    }

    public String getPedido() {
        return pedido;
    }

    public void setPedido(String pedido) {
        this.pedido = pedido;
    }

    public String getBloqueio() {
        return bloqueio;
    }

    public void setBloqueio(String bloqueio) {
        this.bloqueio = bloqueio;
    }

    public String getEmterc() {
        return emterc;
    }

    public void setEmterc(String emterc) {
        this.emterc = emterc;
    }
}
