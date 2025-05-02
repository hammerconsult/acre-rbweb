package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.PagamentoEstorno;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by renat on 16/05/2016.
 */
@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class PagamentoEstornoPortal extends EntidadePortalTransparencia {

    @ManyToOne
    @Etiqueta("Pagamento estorno")
    private PagamentoEstorno pagamentoEstorno;

    public PagamentoEstornoPortal() {
    }

    public PagamentoEstornoPortal(PagamentoEstorno pagamentoEstorno) {
        this.pagamentoEstorno= pagamentoEstorno;
        super.setTipo(TipoObjetoPortalTransparencia.PAGAMENTO_ESTORNO);
    }

    public PagamentoEstorno getPagamentoEstorno() {
        return pagamentoEstorno;
    }

    public void setPagamentoEstorno(PagamentoEstorno pagamentoEstorno) {
        this.pagamentoEstorno = pagamentoEstorno;
    }

    @Override
    public String toString() {
        return pagamentoEstorno.toString();
    }
}
