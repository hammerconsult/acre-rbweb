package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.EstornoLibCotaFinanceira;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by wellington on 23/08/2018.
 */
@Entity
@Etiqueta("Estorno Liberação Cota Financeira - Portal da Transparência")
public class EstornoLiberacaoCotaPortal extends EntidadePortalTransparencia {

    @ManyToOne
    @Etiqueta("Estorno Liberação")
    private EstornoLibCotaFinanceira estornoLibCotaFinanceira;

    public EstornoLiberacaoCotaPortal() {
        super.setTipo(TipoObjetoPortalTransparencia.LIBERACAO_COTA_FINANCEIRA_ESTORNO);
    }

    public EstornoLiberacaoCotaPortal(EstornoLibCotaFinanceira estornoLibCotaFinanceira) {
        this();
        this.estornoLibCotaFinanceira = estornoLibCotaFinanceira;
    }

    public EstornoLibCotaFinanceira getEstornoLibCotaFinanceira() {
        return estornoLibCotaFinanceira;
    }

    public void setEstornoLibCotaFinanceira(EstornoLibCotaFinanceira estornoLibCotaFinanceira) {
        this.estornoLibCotaFinanceira = estornoLibCotaFinanceira;
    }

    @Override
    public String toString() {
        return estornoLibCotaFinanceira.toString();
    }
}
