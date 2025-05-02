package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.Pagamento;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by renat on 16/05/2016.
 */
@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class PagamentoPortal extends EntidadePortalTransparencia {

    @ManyToOne
    @Etiqueta("Pagamento ")
    private Pagamento pagamento;

    public PagamentoPortal() {
    }

    public PagamentoPortal(Pagamento pagamento) {
        this.pagamento = pagamento;
        super.setTipo(TipoObjetoPortalTransparencia.PAGAMENTO);
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }

    @Override
    public String toString() {
        return pagamento.toString();
    }
}
