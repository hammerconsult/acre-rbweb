package br.com.webpublico.entidadesauxiliares;

import java.util.Date;

public class FichaCredorRG {

    private Date dataEmissao;
    private String rg;
    private String orgaoEmissaoRg;
    private String ufRg;

    public FichaCredorRG() {
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getOrgaoEmissaoRg() {
        return orgaoEmissaoRg;
    }

    public void setOrgaoEmissaoRg(String orgaoEmissaoRg) {
        this.orgaoEmissaoRg = orgaoEmissaoRg;
    }

    public String getUfRg() {
        return ufRg;
    }

    public void setUfRg(String ufRg) {
        this.ufRg = ufRg;
    }
}
