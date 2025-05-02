package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.TransferenciaContaFinanceira;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by wellington on 22/08/2018.
 */
@Entity
@Table(name = "TRANSCONTAFINANCEIRAPORTAL")
@Etiqueta("Transferência Conta Financeira - Portal da Transparência")
public class TransferenciaContaFinanceiraPortal extends EntidadePortalTransparencia {

    @ManyToOne
    @Etiqueta("Transferência")
    private TransferenciaContaFinanceira transContaFinanceira;

    public TransferenciaContaFinanceiraPortal() {
        super.setTipo(TipoObjetoPortalTransparencia.TRANSFERENCIA_CONTA_FINANCEIRA);
    }

    public TransferenciaContaFinanceiraPortal(TransferenciaContaFinanceira transContaFinanceira) {
        this();
        this.transContaFinanceira = transContaFinanceira;
    }

    public TransferenciaContaFinanceira getTransContaFinanceira() {
        return transContaFinanceira;
    }

    public void setTransContaFinanceira(TransferenciaContaFinanceira transContaFinanceira) {
        this.transContaFinanceira = transContaFinanceira;
    }

    @Override
    public String toString() {
        return transContaFinanceira.toString();
    }
}
