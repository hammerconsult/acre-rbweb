/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
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
@GrupoDiagrama(nome = "RecursosHumanos")
public class RegraModalidadeTipoAfast implements Serializable, ValidadorEntidade, ValidadorVigencia {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Início da Vigência")
    @Tabelavel
    private Date inicioVigencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Final da Vigência")
    @Tabelavel
    private Date finalVigencia;
    @Obrigatorio
    private Boolean prorrogaPeriodoAquisitivo;
    @Obrigatorio
    private Boolean perdePeriodoAquisitivo;
    @Obrigatorio
    private Boolean reduzDiasTrabalhados;
    @ManyToOne
    private ModalidadeContratoFP modalidadeContratoFP;
    @Etiqueta("Tipo do Afastamento")
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    private TipoAfastamento tipoAfastamento;
    @Transient
    private Date dataRegistro;
    @Invisivel
    @Transient
    private Long criadoEm;
    @Invisivel
    @Transient
    public boolean isEditando;

    public RegraModalidadeTipoAfast() {
        criadoEm = System.nanoTime();
        isEditando = false;
        dataRegistro = new Date();
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
        return this.getFinalVigencia();
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public void setFimVigencia(Date data) {
        setFinalVigencia(data);
    }

    public ModalidadeContratoFP getModalidadeContratoFP() {
        return modalidadeContratoFP;
    }

    public void setModalidadeContratoFP(ModalidadeContratoFP modalidadeContratoFP) {
        this.modalidadeContratoFP = modalidadeContratoFP;
    }

    public Boolean getPerdePeriodoAquisitivo() {
        return perdePeriodoAquisitivo;
    }

    public void setPerdePeriodoAquisitivo(Boolean perdePeriodoAquisitivo) {
        this.perdePeriodoAquisitivo = perdePeriodoAquisitivo;
    }

    public Boolean getProrrogaPeriodoAquisitivo() {
        return prorrogaPeriodoAquisitivo;
    }

    public void setProrrogaPeriodoAquisitivo(Boolean prorrogaPeriodoAquisitivo) {
        this.prorrogaPeriodoAquisitivo = prorrogaPeriodoAquisitivo;
    }

    public Boolean getReduzDiasTrabalhados() {
        return reduzDiasTrabalhados;
    }

    public void setReduzDiasTrabalhados(Boolean reduzDiasTrabalhados) {
        this.reduzDiasTrabalhados = reduzDiasTrabalhados;
    }

    public TipoAfastamento getTipoAfastamento() {
        return tipoAfastamento;
    }

    public void setTipoAfastamento(TipoAfastamento tipoAfastamento) {
        this.tipoAfastamento = tipoAfastamento;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public boolean isEditando() {
        return isEditando;
    }

    public void setEditando(boolean editando) {
        isEditando = editando;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.RegraModalidadeTipoAfast[ id=" + id + " ]";
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }

    public boolean temDataFinalVigenciaInformada() {
        return this.getFinalVigencia() != null;
    }
}
