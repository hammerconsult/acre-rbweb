/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author cheles
 */
@GrupoDiagrama(nome = "RBTrans")
@Audited
@Entity

@Table(name = "PROCESSOCALCLANCOUTORGA")
@Etiqueta(value = "Processo de Cálculo de Lançamento de Outorga")
public class ProcessoCalculoLancamentoOutorga extends ProcessoCalculo implements Serializable {

    @ManyToOne
    private LancamentoOutorga lancamentoOutorga;
    @OneToMany(mappedBy = "procCalcLancamentoOutorga", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CalculoLancamentoOutorga> listaDeCalculoLancamentoOutorga;
    @Invisivel
    @Transient
    private Long criadoEm;

    public ProcessoCalculoLancamentoOutorga() {
        super();
        this.criadoEm = System.nanoTime();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<CalculoLancamentoOutorga> getListaDeCalculoLancamentoOutorga() {
        return listaDeCalculoLancamentoOutorga;
    }

    public void setListaDeCalculoLancamentoOutorga(List<CalculoLancamentoOutorga> listaDeCalculoLancamentoOutorga) {
        this.listaDeCalculoLancamentoOutorga = listaDeCalculoLancamentoOutorga;
    }

    public LancamentoOutorga getLancamentoOutorga() {
        return lancamentoOutorga;
    }

    public void setLancamentoOutorga(LancamentoOutorga lancamentoOutorga) {
        this.lancamentoOutorga = lancamentoOutorga;
    }


    @Override
    public List<? extends Calculo> getCalculos() {
        return listaDeCalculoLancamentoOutorga;
    }
}
