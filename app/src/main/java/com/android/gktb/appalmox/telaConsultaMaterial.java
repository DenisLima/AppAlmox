package com.android.gktb.appalmox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by denis on 25/05/15.
 */
public class telaConsultaMaterial extends Activity {

    private CheckBox disponivel,reservada, pedido, inspecao,processo, bloqueado, emterc;
    public String disponivels = "Nada";
    public String reservadas;
    public String pedidos;
    public String inspecaos;
    public String processos;
    public String bloqueados;
    public String emtercs;
    public String codMaterialConsulta;

    protected void onCreate(Bundle icicle){
        super.onCreate(icicle);
        setContentView(R.layout.tela_consulta_material);

        disponivel = (CheckBox) findViewById(R.id.chDisp);
        reservada  = (CheckBox) findViewById(R.id.chReservada);
        pedido     = (CheckBox) findViewById(R.id.chResPedido);
        inspecao   = (CheckBox) findViewById(R.id.chInspecao);
        processo   = (CheckBox) findViewById(R.id.chProcesso);
        bloqueado  = (CheckBox) findViewById(R.id.chBloqueado);
        emterc     = (CheckBox) findViewById(R.id.chEmTerc);

        Button btnSairCon = (Button) findViewById(R.id.btnSairConMat);
        btnSairCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final EditText codMaterial = (EditText) findViewById(R.id.codMaterialConsulta);

        Button btnEnviarConsulta = (Button) findViewById(R.id.btnEnviarConMat);
        btnEnviarConsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                disponivels = String.valueOf(disponivel.isChecked());
                reservadas = String.valueOf(reservada.isChecked());
                pedidos = String.valueOf(pedido.isChecked());
                inspecaos = String.valueOf(inspecao.isChecked());
                processos = String.valueOf(processo.isChecked());
                bloqueados = String.valueOf(bloqueado.isChecked());
                emtercs = String.valueOf(emterc.isChecked());

                codMaterialConsulta = codMaterial.getText().toString();

                //Toast.makeText(telaConsultaMaterial.this, codMaterialConsulta, Toast.LENGTH_LONG).show();

                if (bloqueados == "true"){
                    Intent it = new Intent(telaConsultaMaterial.this, telaConsultaBloqueado.class);
                    it.putExtra("mat",codMaterialConsulta);
                    it.putExtra("disponivel",disponivels);
                    it.putExtra("reservada",reservadas);
                    it.putExtra("pedido",pedidos);
                    it.putExtra("inspecao",inspecaos);
                    it.putExtra("processo",processos);
                    it.putExtra("bloqueado",bloqueados);
                    it.putExtra("emterc",emtercs);
                    startActivity(it);
                    finish();
                }
                else {
                    Intent it = new Intent(telaConsultaMaterial.this, ListaMateriais.class);
                    it.putExtra("par", "disp");
                    it.putExtra("motivo", "nada");
                    it.putExtra("mat", codMaterialConsulta);
                    it.putExtra("disponivel", disponivels);
                    it.putExtra("reservada", reservadas);
                    it.putExtra("pedido", pedidos);
                    it.putExtra("inspecao", inspecaos);
                    it.putExtra("processo", processos);
                    it.putExtra("bloqueado", bloqueados);
                    it.putExtra("emterc", emtercs);
                    startActivity(it);
                    finish();
                }



            }
        });

    }

}
