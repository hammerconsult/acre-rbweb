package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.Contrato;
import br.com.webpublico.entidades.DispensaDeLicitacao;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by renat on 29/04/2019.
 */
@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class DispensaLicitacaoPortal extends EntidadePortalTransparencia {

    @ManyToOne
    private DispensaDeLicitacao dispensaDeLicitacao;

    public DispensaLicitacaoPortal() {
    }

    public DispensaLicitacaoPortal(DispensaDeLicitacao dispensaDeLicitacao) {
        this.dispensaDeLicitacao = dispensaDeLicitacao;
        super.setTipo(TipoObjetoPortalTransparencia.DISPENSA_LICITACAO);
    }

    public DispensaDeLicitacao getDispensaDeLicitacao() {
        return dispensaDeLicitacao;
    }

    public void setDispensaDeLicitacao(DispensaDeLicitacao dispensaDeLicitacao) {
        this.dispensaDeLicitacao = dispensaDeLicitacao;
    }

    @Override
    public String toString() {
        return dispensaDeLicitacao.toString();
    }
}
