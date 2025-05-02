package br.com.webpublico.entidadesauxiliares;

import java.util.Date;

/**
 * Created by mga on 02/06/2017.
 */
public class ServidorInfoFalta {

    private Integer item;
    private Date inicio;
    private Date fim;
    private String tipoFalta;
    private Date dataCadastro;

    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public String getTipoFalta() {
        return tipoFalta;
    }

    public void setTipoFalta(String tipoFalta) {
        this.tipoFalta = tipoFalta;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}
