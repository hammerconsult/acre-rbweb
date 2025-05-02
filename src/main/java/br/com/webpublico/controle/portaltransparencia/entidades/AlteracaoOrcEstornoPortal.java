package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.EstornoAlteracaoOrc;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by renat on 13/05/2016.
 */
@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class AlteracaoOrcEstornoPortal extends EntidadePortalTransparencia {

    @ManyToOne
    private EstornoAlteracaoOrc estornoAlteracaoOrc;

    public AlteracaoOrcEstornoPortal() {
    }

    public AlteracaoOrcEstornoPortal(EstornoAlteracaoOrc estornoAlteracaoOrc) {
        this.estornoAlteracaoOrc = estornoAlteracaoOrc;
        super.setTipo(TipoObjetoPortalTransparencia.ALTERACAO_ORCAMENTARIA_ESTORNO);
    }

    public EstornoAlteracaoOrc getEstornoAlteracaoOrc() {
        return estornoAlteracaoOrc;
    }

    public void setEstornoAlteracaoOrc(EstornoAlteracaoOrc estornoAlteracaoOrc) {
        this.estornoAlteracaoOrc = estornoAlteracaoOrc;
    }

    @Override
    public String toString() {
        return estornoAlteracaoOrc.toString();
    }
}
