package com.android.gktb.appalmox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
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

/**
 * Created by denis on 30/03/2015.
 */
public class ImprimeEtiqueta extends Activity {

    private String id;
    private Context context;
    ProgressDialog dialog;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void ImprimirEtiqueta(){

        AsyncTask<String, Integer, String> task = new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                dialog = new ProgressDialog(getContext());
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
                    pairs.add(new BasicNameValuePair("acao","impressao_etiqueta"));
                    pairs.add(new BasicNameValuePair("id",getId()));
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

                if (result.equals("impressao_ok")){
                    Toast.makeText(getContext(), "Etiqueta aguardando impressão!", Toast.LENGTH_LONG).show();
                 /*   Intent it = new Intent(ImprimeEtiqueta.this, telaMenu.class);
                    startActivity(it); */
                }
                else {
                    Toast.makeText(getContext(), "Problemas para gravar a etiqueta, favor verificar com o administrador!", Toast.LENGTH_LONG).show();
                 /*   Intent it = new Intent(ImprimeEtiqueta.this, telaMenu.class);
                    startActivity(it); */
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
}
