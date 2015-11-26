package com.android.gktb.appalmox;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.app.Activity;

/**
 * Created by denis on 19/03/2015.
 */
public class validaLote extends AsyncTask<String, Object, String> {

    private ProgressDialog progressDialog;
    private Context context;
    private String lote;

    public validaLote(Context context){
        this.context = context;
    }

    public Context getContext(){
        return this.context;
    }

    @Override
    protected String doInBackground(String... paramss) {

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://10.55.1.242/nova_intranet/views/ti/web_service_almox.php");

        try{

            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            pairs.add(new BasicNameValuePair("acao","valida_lote"));
            pairs.add(new BasicNameValuePair("lote",getLote()));
            post.setEntity(new UrlEncodedFormEntity(pairs));

            HttpResponse response = client.execute(post);
            String responseString = EntityUtils.toString(response.getEntity());

            return responseString.trim();

        }catch (Exception e){
            return null;
        }

    }

    @Override
    protected void onPreExecute() {
        //Cria novo um ProgressDialogo e exibe
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Aguarde...");
        progressDialog.show();
    }

    protected void onPostExecute(String result) {
        //Cancela progressDialogo
        progressDialog.dismiss();

        //Informa a mensagem
        if(result == "material_nc"){
            Toast.makeText(getContext(), "Esse material não existe, efetue a leitura novamente", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getContext(), "Material OK: "+result, Toast.LENGTH_LONG).show();
        }
    }


    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }
}
