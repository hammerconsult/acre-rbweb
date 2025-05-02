package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.Licitacao;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by renat on 13/05/2016.
 */
@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class LicitacaoPortal extends EntidadePortalTransparencia {

    @ManyToOne
    private Licitacao licitacao;

    public LicitacaoPortal() {
    }

    public LicitacaoPortal(Licitacao licitacao) {
        this.licitacao = licitacao;
        super.setTipo(TipoObjetoPortalTransparencia.LICITACAO);
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    @Override
    public String toString() {
        return licitacao.toString();
    }
}
