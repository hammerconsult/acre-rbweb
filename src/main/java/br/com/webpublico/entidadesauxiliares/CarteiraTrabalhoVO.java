package br.com.webpublico.entidadesauxiliares;

import java.util.Date;

/**
 * Created by carlos on 03/04/17.
 */
public class CarteiraTrabalhoVO {

    private String numero;
    private String serie;
    private Date dataEmissao;
    private String uf;
    private String pisPasep;
    private String orgaoExpedidor;
    private String bancoPisPasep;
    private Date dataEmissaoPisPasep;
    private Number anoPrimeiroEmprego;

    public CarteiraTrabalhoVO() {
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getPisPasep() {
        return pisPasep;
    }

    public void setPisPasep(String pisPasep) {
        this.pisPasep = pisPasep;
    }

    public String getOrgaoExpedidor() {
        return orgaoExpedidor;
    }

    public void setOrgaoExpedidor(String orgaoExpedidor) {
        this.orgaoExpedidor = orgaoExpedidor;
    }

    public String getBancoPisPasep() {
        return bancoPisPasep;
    }

    public void setBancoPisPasep(String bancoPisPasep) {
        this.bancoPisPasep = bancoPisPasep;
    }

    public Date getDataEmissaoPisPasep() {
        return dataEmissaoPisPasep;
    }

    public void setDataEmissaoPisPasep(Date dataEmissaoPisPasep) {
        this.dataEmissaoPisPasep = dataEmissaoPisPasep;
    }

    public Number getAnoPrimeiroEmprego() {
        return anoPrimeiroEmprego;
    }

    public void setAnoPrimeiroEmprego(Number anoPrimeiroEmprego) {
        this.anoPrimeiroEmprego = anoPrimeiroEmprego;
    }
}
