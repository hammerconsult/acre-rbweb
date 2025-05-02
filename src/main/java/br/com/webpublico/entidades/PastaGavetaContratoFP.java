/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author andre
 */
@Entity
@Audited
@Etiqueta("Fichário do Servidor")
public class PastaGavetaContratoFP extends SuperEntidade implements Serializable, ValidadorVigencia {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO, ordemApresentacao = 7)
    @Etiqueta("Inicio de Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO, ordemApresentacao = 8)
    @Etiqueta("Final de Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date finalVigencia;
    @Tabelavel(ordemApresentacao = 1)
    @Etiqueta("Servidor")
    @ManyToOne
    private ContratoFP contratoFP;
    @Transient
    @Invisivel
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Etiqueta("Código do Fichário")
    private Integer ficharioCodigo;
    @Transient
    @Invisivel
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.ESQUERDA)
    @Etiqueta("Descrição do Fichário")
    private String ficharioDescricao;
    @Transient
    @Invisivel
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Etiqueta("Código da Gaveta")
    private Integer gavetaFicharioCodigo;
    @Transient
    @Invisivel
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.ESQUERDA)
    @Etiqueta("Descrição da Gaveta")
    private String gavetaFicharioDescricao;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Pasta")
    private PastaGaveta pastaGaveta;

    public PastaGavetaContratoFP() {
    }

    public PastaGavetaContratoFP(PastaGavetaContratoFP pastaGavetaContratoFP) {
        this.setId(pastaGavetaContratoFP.getId());
        this.setInicioVigencia(pastaGavetaContratoFP.getInicioVigencia());
        this.setFinalVigencia(pastaGavetaContratoFP.getFinalVigencia());
        this.setPastaGaveta(pastaGavetaContratoFP.getPastaGaveta());
        this.setContratoFP(pastaGavetaContratoFP.getContratoFP());
        if (this.getPastaGaveta() != null && this.getPastaGaveta().getGavetaFichario() != null) {
            gavetaFicharioCodigo = this.getPastaGaveta().getGavetaFichario().getCodigo();
            gavetaFicharioDescricao = this.getPastaGaveta().getGavetaFichario().getDescricao();
            if (this.getPastaGaveta().getGavetaFichario().getFichario() != null) {
                ficharioCodigo = this.getPastaGaveta().getGavetaFichario().getFichario().getCodigo();
                ficharioDescricao = this.getPastaGaveta().getGavetaFichario().getFichario().getDescricao();
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
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

    }

    public PastaGaveta getPastaGaveta() {
        return pastaGaveta;
    }

    public void setPastaGaveta(PastaGaveta pastaGaveta) {
        this.pastaGaveta = pastaGaveta;
    }

    @Override
    public String toString() {
        return pastaGaveta.toString();
    }
}
