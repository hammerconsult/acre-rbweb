package br.com.webpublico.entidadesauxiliares;

import java.util.Date;

/**
 * Created by carlos on 03/04/17.
 */
public class RgVO {
    private Date dataemissao;
    private String numero;
    private String orgaoEmissao;
    private String uf;

    public RgVO() {
    }

    public Date getDataemissao() {
        return dataemissao;
    }

    public void setDataemissao(Date dataemissao) {
        this.dataemissao = dataemissao;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getOrgaoEmissao() {
        return orgaoEmissao;
    }

    public void setOrgaoEmissao(String orgaoEmissao) {
        this.orgaoEmissao = orgaoEmissao;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }
}
