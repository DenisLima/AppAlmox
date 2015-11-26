package com.android.gktb.appalmox;

/**
 * Created by denis on 26/03/2015.
 */
public class itensLista {

    private String codMaterial;
    private int codLote;
    private String codLocal;
    private String motivo;
    private double qtd;

    public String getCodMaterial() {
        return codMaterial;
    }

    public void setCodMaterial(String codMaterial) {
        this.codMaterial = codMaterial;
    }

    public int getCodLote() {
        return codLote;
    }

    public void setCodLote(int codLote) {
        this.codLote = codLote;
    }

    public String getCodLocal() {
        return codLocal;
    }

    public void setCodLocal(String codLocal) {
        this.codLocal = codLocal;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public double getQtd() {
        return qtd;
    }

    public void setQtd(double qtd) {
        this.qtd = qtd;
    }
}
