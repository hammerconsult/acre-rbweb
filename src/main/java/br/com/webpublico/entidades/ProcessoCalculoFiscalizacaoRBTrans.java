package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Processo de Cálculo Fiscalização RBTrans")
@Table(name = "PROCESSOCALCFISCRBTRANS")
public class ProcessoCalculoFiscalizacaoRBTrans extends ProcessoCalculo implements Serializable {

    @OneToMany(mappedBy = "processoCalculoFiscalizacaoRBTrans", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoFiscalizacaoRBTrans> listaDeCalculos;
    @ManyToOne
    private FiscalizacaoRBTrans fiscalizacaoRBTrans;

    public ProcessoCalculoFiscalizacaoRBTrans() {
        listaDeCalculos = new ArrayList<CalculoFiscalizacaoRBTrans>();
    }

    public FiscalizacaoRBTrans getFiscalizacaoRBTrans() {
        return fiscalizacaoRBTrans;
    }

    public void setFiscalizacaoRBTrans(FiscalizacaoRBTrans fiscalizacaoRBTrans) {
        this.fiscalizacaoRBTrans = fiscalizacaoRBTrans;
    }

    public List<CalculoFiscalizacaoRBTrans> getListaDeCalculos() {
        return listaDeCalculos;
    }

    public void setListaDeCalculos(List<CalculoFiscalizacaoRBTrans> listaDeCalculos) {
        this.listaDeCalculos = listaDeCalculos;
    }

    @Override
    public List<? extends Calculo> getCalculos() {
        return listaDeCalculos;
    }
}
