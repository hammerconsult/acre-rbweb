/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
public class FaixaReferenciaFP extends SuperEntidade implements Serializable, ValidadorVigencia, Comparable<FaixaReferenciaFP> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Início da Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Tabelavel
    @Etiqueta("Final da Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date finalVigencia;
    @Obrigatorio
    @Etiqueta("Referência FP")
    @ManyToOne
    private ReferenciaFP referenciaFP;
    @OneToMany(mappedBy = "faixaReferenciaFP", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemFaixaReferenciaFP> itensFaixaReferenciaFP = Lists.newArrayList();
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataRegistro;

    @Transient
    private Boolean mostrarItens = Boolean.FALSE;

    public FaixaReferenciaFP() {
        this.dataRegistro = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    @Override
    public Date getFimVigencia() {
        return getFinalVigencia();
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public void setFimVigencia(Date data) {
        setFinalVigencia(data);
    }

    public ReferenciaFP getReferenciaFP() {
        return referenciaFP;
    }

    public void setReferenciaFP(ReferenciaFP referenciaFP) {
        this.referenciaFP = referenciaFP;
    }

    public List<ItemFaixaReferenciaFP> getItensFaixaReferenciaFP() {
        return itensFaixaReferenciaFP;
    }

    public void setItensFaixaReferenciaFP(List<ItemFaixaReferenciaFP> itensFaixaReferenciaFP) {
        this.itensFaixaReferenciaFP = itensFaixaReferenciaFP;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Boolean getMostrarItens() {
        return mostrarItens;
    }

    public void setMostrarItens(Boolean mostrarItens) {
        this.mostrarItens = mostrarItens;
    }

    @Override
    public String toString() {
        return DataUtil.getDataFormatada(inicioVigencia) + " - " + DataUtil.getDataFormatada(finalVigencia);
    }

    @Override
    public int compareTo(FaixaReferenciaFP outro) {
        return inicioVigencia.compareTo(outro.getInicioVigencia());
    }

    public void adicionarItem(ItemFaixaReferenciaFP itemFaixaReferenciaSelecionado) {
        if (!itensFaixaReferenciaFP.contains(itemFaixaReferenciaSelecionado)) {
            itensFaixaReferenciaFP.add(itemFaixaReferenciaSelecionado);
            Collections.sort(itensFaixaReferenciaFP);
        }
    }

    public void removerItem(ItemFaixaReferenciaFP itemFaixaReferenciaSelecionado) {
        if (itensFaixaReferenciaFP.contains(itemFaixaReferenciaSelecionado)) {
            itensFaixaReferenciaFP.remove(itemFaixaReferenciaSelecionado);
        }
    }
}
