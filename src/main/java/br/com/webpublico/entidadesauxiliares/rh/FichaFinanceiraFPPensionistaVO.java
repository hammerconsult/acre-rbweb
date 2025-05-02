package br.com.webpublico.entidadesauxiliares.rh;

import java.math.BigDecimal;
import java.util.Date;

public class FichaFinanceiraFPPensionistaVO extends FichaFinanceiraFPCamposVO {

    private BigDecimal remuneracaoInstituidor;
    private BigDecimal numeroCotas;
    private Date dataConcessao;
    private String tipoPensao;
    private String pensionista;

    public FichaFinanceiraFPPensionistaVO() {
    }

    public BigDecimal getRemuneracaoInstituidor() {
        return remuneracaoInstituidor;
    }

    public void setRemuneracaoInstituidor(BigDecimal remuneracaoInstituidor) {
        this.remuneracaoInstituidor = remuneracaoInstituidor;
    }

    public BigDecimal getNumeroCotas() {
        return numeroCotas;
    }

    public void setNumeroCotas(BigDecimal numeroCotas) {
        this.numeroCotas = numeroCotas;
    }

    public Date getDataConcessao() {
        return dataConcessao;
    }

    public void setDataConcessao(Date dataConcessao) {
        this.dataConcessao = dataConcessao;
    }

    public String getTipoPensao() {
        return tipoPensao;
    }

    public void setTipoPensao(String tipoPensao) {
        this.tipoPensao = tipoPensao;
    }

    public String getPensionista() {
        return pensionista;
    }

    public void setPensionista(String pensionista) {
        this.pensionista = pensionista;
    }
}
