package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author AndreGustavo
 */
@Entity
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Cálculo Fiscalização RBTrans")
@Table(name = "CALCULOFISCRBTRANS")
public class CalculoFiscalizacaoRBTrans extends Calculo implements Serializable {

    @OneToOne
    @JoinColumn(name = "PROCCALCFISCRBTRANS_ID")
    private ProcessoCalculoFiscalizacaoRBTrans processoCalculoFiscalizacaoRBTrans;
    @OneToMany(mappedBy = "calculoFiscalizacaoRBTrans", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCalculoFiscalizacaoRBTrans> itensCalculoFiscalizacaoRBTrans;

    public CalculoFiscalizacaoRBTrans() {
        super.setTipoCalculo(TipoCalculo.FISCALIZACAO_RBTRANS);
    }

    public List<ItemCalculoFiscalizacaoRBTrans> getItensCalculoFiscalizacaoRBTrans() {
        return itensCalculoFiscalizacaoRBTrans;
    }

    public void setItensCalculoFiscalizacaoRBTrans(List<ItemCalculoFiscalizacaoRBTrans> itensCalculoFiscalizacaoRBTrans) {
        this.itensCalculoFiscalizacaoRBTrans = itensCalculoFiscalizacaoRBTrans;
    }

    public ProcessoCalculoFiscalizacaoRBTrans getProcessoCalculoFiscalizacaoRBTrans() {
        return processoCalculoFiscalizacaoRBTrans;
    }

    public void setProcessoCalculoFiscalizacaoRBTrans(ProcessoCalculoFiscalizacaoRBTrans processoCalculoFiscalizacaoRBTrans) {
        super.setProcessoCalculo(processoCalculoFiscalizacaoRBTrans);
        this.processoCalculoFiscalizacaoRBTrans = processoCalculoFiscalizacaoRBTrans;
    }

    @Override
    public ProcessoCalculo getProcessoCalculo() {
        return processoCalculoFiscalizacaoRBTrans;
    }

}
