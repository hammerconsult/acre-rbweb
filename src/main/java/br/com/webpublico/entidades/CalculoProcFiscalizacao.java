/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Leonardo
 */
@Entity
@Audited

@GrupoDiagrama(nome = "fiscalizacaogeral")
@Etiqueta("Calculo do Processo de Fiscalizacao Geral")
public class CalculoProcFiscalizacao extends Calculo implements Serializable {

    @OneToOne
    private ProcessoFiscalizacao processoFiscalizacao;
    @ManyToOne
    private ProcessoCalculoProcFiscal processo;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "calculo")
    private List<ItemCalcProcFiscalizacao> itemCalcProcFiscalizacoes;

    public CalculoProcFiscalizacao() {
        super.setTipoCalculo(TipoCalculo.PROC_FISCALIZACAO);
    }

    public ProcessoCalculoProcFiscal getProcesso() {
        return processo;
    }

    public void setProcesso(ProcessoCalculoProcFiscal processo) {
        super.setProcessoCalculo(processo);
        this.processo = processo;
    }

    public ProcessoFiscalizacao getProcessoFiscalizacao() {
        return processoFiscalizacao;
    }

    public void setProcessoFiscalizacao(ProcessoFiscalizacao processoFiscalizacao) {
        this.processoFiscalizacao = processoFiscalizacao;
    }

    public List<ItemCalcProcFiscalizacao> getItemCalcProcFiscalizacoes() {
        return itemCalcProcFiscalizacoes;
    }

    public void setItemCalcProcFiscalizacoes(List<ItemCalcProcFiscalizacao> itemCalcProcFiscalizacoes) {
        this.itemCalcProcFiscalizacoes = itemCalcProcFiscalizacoes;
    }

    @Override
    public ProcessoCalculo getProcessoCalculo() {
       return processo;
    }

}
