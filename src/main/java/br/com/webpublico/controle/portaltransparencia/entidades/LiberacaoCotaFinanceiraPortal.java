package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.LiberacaoCotaFinanceira;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by wellington on 23/08/2018.
 */
@Entity
@Etiqueta("Liberação Cota Financeira - Portal da Transparência")
public class LiberacaoCotaFinanceiraPortal extends EntidadePortalTransparencia {

    @ManyToOne
    @Etiqueta("Liberação")
    private LiberacaoCotaFinanceira liberacaoCotaFinanceira;

    public LiberacaoCotaFinanceiraPortal() {
        super.setTipo(TipoObjetoPortalTransparencia.LIBERACAO_COTA_FINANCEIRA);
    }

    public LiberacaoCotaFinanceiraPortal(LiberacaoCotaFinanceira liberacaoCotaFinanceira) {
        this();
        this.liberacaoCotaFinanceira = liberacaoCotaFinanceira;
    }

    public LiberacaoCotaFinanceira getLiberacaoCotaFinanceira() {
        return liberacaoCotaFinanceira;
    }

    public void setLiberacaoCotaFinanceira(LiberacaoCotaFinanceira liberacaoCotaFinanceira) {
        this.liberacaoCotaFinanceira = liberacaoCotaFinanceira;
    }

    @Override
    public String toString() {
        return liberacaoCotaFinanceira.toString();
    }
}
