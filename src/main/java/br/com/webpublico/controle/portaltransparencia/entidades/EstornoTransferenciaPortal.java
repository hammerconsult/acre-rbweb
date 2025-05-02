package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.EstornoTransferencia;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by wellington on 23/08/2018.
 */
@Entity
@Etiqueta("Estorno Transferência Conta Financeira - Portal da Transparência")
public class EstornoTransferenciaPortal extends EntidadePortalTransparencia {

    @ManyToOne
    @Etiqueta("Estorno Transferência")
    private EstornoTransferencia estornoTransferencia;

    public EstornoTransferenciaPortal() {
        super.setTipo(TipoObjetoPortalTransparencia.TRANSFERENCIA_CONTA_FINANCEIRA_ESTORNO);
    }

    public EstornoTransferenciaPortal(EstornoTransferencia estornoTransferencia) {
        this();
        this.estornoTransferencia = estornoTransferencia;
    }

    public EstornoTransferencia getEstornoTransferencia() {
        return estornoTransferencia;
    }

    public void setEstornoTransferencia(EstornoTransferencia estornoTransferencia) {
        this.estornoTransferencia = estornoTransferencia;
    }

    @Override
    public String toString() {
        return estornoTransferencia.toString();
    }
}
