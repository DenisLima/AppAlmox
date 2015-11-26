package com.android.gktb.appalmox;

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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by denis on 19/03/2015.
 */
public class testeAsync {

    private String retorno;
    private Context context;
    private ProgressDialog progressDialog;

    public testeAsync(Context c){
        this.context = c;
    }

    public Context getContext(){
        return context;
    }

    public String Processa(){


        AsyncTask<String, Object, String> assync = new AsyncTask<String, Object, String>() {

            @Override
            protected void onPreExecute() {
                //Cria novo um ProgressDialogo e exibe
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Aguarde...");
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://10.55.1.242/nova_intranet/views/ti/web_service_almox.php");

                try{

                    List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                    pairs.add(new BasicNameValuePair("acao","valida_material"));
                    pairs.add(new BasicNameValuePair("material","dois_rec"));
                    post.setEntity(new UrlEncodedFormEntity(pairs));

                    HttpResponse response = client.execute(post);
                    BufferedReader resposta = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                    if(resposta != null){


                        setRetorno(resposta.readLine().toString());
                        return resposta.readLine().toString();

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
                progressDialog.dismiss();
                Toast.makeText(getContext(), "O retorno foi: " + result, Toast.LENGTH_LONG).show();
                setRetorno(result);
            }

        }.execute();

        return getRetorno();

    }

    public String getRetorno() {
        return retorno;
    }

    public void setRetorno(String retorno) {
        this.retorno = retorno;
    }

}
