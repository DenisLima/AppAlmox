package com.android.gktb.appalmox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

/**
 * Created by denis on 19/03/2015.
 */
public class validaMaterial extends Activity{

    private ProgressDialog progressDialog;
    private Context context;
    private String material;
    private String rtn;

    public validaMaterial(Context context){
        this.context = context;
    }

    public Context getContext(){
        return this.context;
    }

    public void Validacao() {

        AsyncTask<String, Object, String> task = new AsyncTask<String, Object, String>() {

            @Override
            protected String doInBackground(String... paramss) {

                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://10.55.1.242/nova_intranet/views/ti/web_service_almox.php");

                try {

                    List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                    pairs.add(new BasicNameValuePair("acao", "valida_material"));
                    pairs.add(new BasicNameValuePair("material", getMaterial()));
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
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Aguarde...");
                progressDialog.show();
            }

            protected void onPostExecute(String result) {
                //Cancela progressDialogo
                progressDialog.dismiss();

                String r[] = result.split(";");
                setRtn(r[0]);

                if (r[0].equals("material_nc")) {
                    Toast.makeText(getContext(), "Esse material não existe, efetue a leitura novamente!", Toast.LENGTH_LONG).show();
                } else {

                    new AlertDialog.Builder(getContext())
                            .setMessage("Agora efetue a leitura do NR")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                                    startActivityForResult(intent, 0);

                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                    //Toast.makeText(getContext(), "Material OK: "+r[1], Toast.LENGTH_LONG).show();
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

    public String getRtn() {
        return rtn;
    }

    public void setRtn(String rtn) {
        this.rtn = rtn;
    }

    //Recebe o resultado da primeira leitura
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                Toast.makeText(this, "Deu certo porra: ", Toast.LENGTH_LONG).show();

            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
                Toast.makeText(this, "Deu erro", Toast.LENGTH_LONG).show();
            }
        }
    }
}
