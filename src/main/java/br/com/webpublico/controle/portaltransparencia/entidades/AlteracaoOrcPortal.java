package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.AlteracaoORC;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by renat on 13/05/2016.
 */
@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class AlteracaoOrcPortal extends EntidadePortalTransparencia {

    @ManyToOne
    private AlteracaoORC alteracaoORC;

    public AlteracaoOrcPortal() {
    }

    public AlteracaoOrcPortal(AlteracaoORC alteracaoORC) {
        this.alteracaoORC = alteracaoORC;
        super.setTipo(TipoObjetoPortalTransparencia.ALTERACAO_ORCAMENTARIA);
    }

    public AlteracaoORC getAlteracaoORC() {
        return alteracaoORC;
    }

    public void setAlteracaoORC(AlteracaoORC alteracaoORC) {
        this.alteracaoORC = alteracaoORC;
    }

    @Override
    public String toString() {
        return alteracaoORC.toString();
    }
}
