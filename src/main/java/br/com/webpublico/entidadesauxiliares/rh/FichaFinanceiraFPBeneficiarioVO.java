package br.com.webpublico.entidadesauxiliares.rh;

import java.util.Date;

public class FichaFinanceiraFPBeneficiarioVO extends FichaFinanceiraFPCamposVO {

    private Date inicioPensao;

    public FichaFinanceiraFPBeneficiarioVO() {
    }

    public Date getInicioPensao() {
        return inicioPensao;
    }

    public void setInicioPensao(Date inicioPensao) {
        this.inicioPensao = inicioPensao;
    }
}
