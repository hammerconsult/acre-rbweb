/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoPenalidadeContrato;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contratos")
@Etiqueta("Penalidade de Contrato")
@Inheritance(strategy = InheritanceType.JOINED)
public class PenalidadeContrato implements Serializable, ValidadorEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;
    @ManyToOne
    private Contrato contrato;
    @Obrigatorio
    @Etiqueta("Tipo de Penalidade")
    @Enumerated(EnumType.STRING)
    private TipoPenalidadeContrato tipo;
    @Obrigatorio
    @Etiqueta("Data da Ocorrência")
    @Temporal(TemporalType.DATE)
    private Date dataOcorrencia;
    @Obrigatorio
    @Etiqueta("Descrição Ocorrência")
    private String descricaoOcorrencia;
    @Etiqueta("Responsável pela ciência")
    @Obrigatorio
    @ManyToOne
    private PessoaFisica pessoaFisica;
    @Etiqueta("Data da Ciência")
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    private Date dataAssinatura;
    @Obrigatorio
    @Etiqueta("Observação")
    private String observacoes;
    @Transient
    @Invisivel
    private Long criadoEm;
    @Transient
    @Invisivel
    private Operacoes operacao;

    public PenalidadeContrato() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public TipoPenalidadeContrato getTipo() {
        return tipo;
    }

    public boolean isDeclaracaoInidoneidade() {
        return TipoPenalidadeContrato.DECLARACAO_INIDONEIDADE.equals(getTipo());
    }

    public boolean isSuspensaoTemporaria() {
        return TipoPenalidadeContrato.SUSPENSAO_TEMPORARIA.equals(getTipo());
    }

    public boolean isMulta() {
        return TipoPenalidadeContrato.MULTA.equals(getTipo());
    }

    public void setTipo(TipoPenalidadeContrato tipo) {
        this.tipo = tipo;
    }

    public String getDescricaoOcorrencia() {
        return descricaoOcorrencia;
    }

    public void setDescricaoOcorrencia(String descricaoOcorrencia) {
        this.descricaoOcorrencia = descricaoOcorrencia;
    }

    public Date getDataOcorrencia() {
        return dataOcorrencia;
    }

    public void setDataOcorrencia(Date dataOcorrencia) {
        this.dataOcorrencia = dataOcorrencia;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public Date getDataAssinatura() {
        return dataAssinatura;
    }

    public void setDataAssinatura(Date dataAssinatura) {
        this.dataAssinatura = dataAssinatura;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return id == null ? "Dados ainda não gravados" : "Penalidade de contrato código: " + id;
    }

    public Operacoes getOperacao() {
        return operacao;
    }

    public void setOperacao(Operacoes operacao) {
        this.operacao = operacao;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }
}
