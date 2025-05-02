package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

/**
 * Created by William on 10/04/2017.
 */
public class TraillerDAF607 implements Serializable {

    private String codigoRegistro;
    private String sequencialRegistro;
    private String totalRegistrosGravador;
    private String valorTotalRecebido;

    public TraillerDAF607() {
    }

    public String getCodigoRegistro() {
        return codigoRegistro;
    }

    public void setCodigoRegistro(String codigoRegistro) {
        this.codigoRegistro = codigoRegistro;
    }

    public String getSequencialRegistro() {
        return sequencialRegistro;
    }

    public void setSequencialRegistro(String sequencialRegistro) {
        this.sequencialRegistro = sequencialRegistro;
    }

    public String getTotalRegistrosGravador() {
        return totalRegistrosGravador;
    }

    public void setTotalRegistrosGravador(String totalRegistrosGravador) {
        this.totalRegistrosGravador = totalRegistrosGravador;
    }

    public String getValorTotalRecebido() {
        return valorTotalRecebido;
    }

    public void setValorTotalRecebido(String valorTotalRecebido) {
        this.valorTotalRecebido = valorTotalRecebido;
    }

    @Override
    public String toString() {
        return "TraillerDAF607{" +
            "codigoRegistro='" + codigoRegistro + '\'' +
            ", sequencialRegistro='" + sequencialRegistro + '\'' +
            ", totalRegistrosGravador='" + totalRegistrosGravador + '\'' +
            ", valorTotalRecebido='" + valorTotalRecebido + '\'' +
            '}';
    }
}
