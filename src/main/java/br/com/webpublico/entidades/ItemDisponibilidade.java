/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Disponibilidade")
@Audited
@Entity
public class ItemDisponibilidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data da Disponibilidade")
    private Date dataDisponibilidade;
    @ManyToOne
    private Disponibilidade disponibilidade;
    @OneToOne
    private SituacaoContratoFP situacaoContratoFP;
    @OneToOne
    private ProvimentoFP provimentoFP;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Contrato")
    @OneToOne
    private ContratoFP contratoFP;
    @Transient
    @Invisivel
    private Long criadoEm;
    @Transient
    @Invisivel
    private boolean selecionadoEmLista;

    public ItemDisponibilidade() {
        this.criadoEm = System.nanoTime();
    }

    public boolean isSelecionadoEmLista() {
        return selecionadoEmLista;
    }

    public void setSelecionadoEmLista(boolean selecionadoEmLista) {
        this.selecionadoEmLista = selecionadoEmLista;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Disponibilidade getDisponibilidade() {
        return disponibilidade;
    }

    public void setDisponibilidade(Disponibilidade disponibilidade) {
        this.disponibilidade = disponibilidade;
    }

    public Date getDataDisponibilidade() {
        return dataDisponibilidade;
    }

    public void setDataDisponibilidade(Date dataDisponibilidade) {
        this.dataDisponibilidade = dataDisponibilidade;
    }

    public SituacaoContratoFP getSituacaoContratoFP() {
        return situacaoContratoFP;
    }

    public void setSituacaoContratoFP(SituacaoContratoFP situacaoContratoFP) {
        this.situacaoContratoFP = situacaoContratoFP;
    }

    public ProvimentoFP getProvimentoFP() {
        return provimentoFP;
    }

    public void setProvimentoFP(ProvimentoFP provimentoFP) {
        this.provimentoFP = provimentoFP;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    @Override
    public String toString() {
        return new SimpleDateFormat("dd/MM/yyyy").format(dataDisponibilidade) + " - " + contratoFP;
    }
}
