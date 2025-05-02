/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoCadastralContabil;
import br.com.webpublico.enums.TipoOperacaoConcilicaoBancaria;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Renato
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Conciliacao")
@Etiqueta("Conciliação Bancária ")
@Table(name = "LANCCONCILIACAOBANCARIA")
public class LancamentoConciliacaoBancaria implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Etiqueta("Número")
    private Long numero;
    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date data;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Etiqueta("Data de Conciliação")
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataConciliacao;
    @Pesquisavel
    @Obrigatorio
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.ESQUERDA)
    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    @Etiqueta("Operação de Conciliação")
    private OperacaoConciliacao operacaoConciliacao;
    @Pesquisavel
    @Obrigatorio
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.ESQUERDA)
    @ManyToOne
    @Etiqueta("Conta Financeira")
    private SubConta subConta;
    @Obrigatorio
    @Etiqueta("Histórico")
    private String historico;
    @Transient
    private Long criadoEm;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Etiqueta")
    private SituacaoCadastralContabil situacao;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Obrigatorio
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.ESQUERDA)
    @Etiqueta("Operação de Conciliação")
    private TipoOperacaoConcilicaoBancaria tipoOperacaoConciliacao;
    @Pesquisavel
    @Obrigatorio
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.ESQUERDA)
    @ManyToOne
    @Etiqueta("Tipo de Conciliação")
    private TipoConciliacao tipoConciliacao;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @Etiqueta("Valor (R$)")
    private BigDecimal Valor;
    @ManyToOne
    private Identificador identificador;

    public LancamentoConciliacaoBancaria() {
        this.Valor = new BigDecimal(BigInteger.ZERO);
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public OperacaoConciliacao getOperacaoConciliacao() {
        return operacaoConciliacao;
    }

    public void setOperacaoConciliacao(OperacaoConciliacao operacaoConciliacao) {
        this.operacaoConciliacao = operacaoConciliacao;
    }

    public TipoConciliacao getTipoConciliacao() {
        return tipoConciliacao;
    }

    public void setTipoConciliacao(TipoConciliacao tipoConciliacao) {
        this.tipoConciliacao = tipoConciliacao;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public BigDecimal getValor() {
        return Valor;
    }

    public void setValor(BigDecimal Valor) {
        this.Valor = Valor;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public SituacaoCadastralContabil getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoCadastralContabil situacao) {
        this.situacao = situacao;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public TipoOperacaoConcilicaoBancaria getTipoOperacaoConciliacao() {
        return tipoOperacaoConciliacao;
    }

    public void setTipoOperacaoConciliacao(TipoOperacaoConcilicaoBancaria tipoOperacaoConciliacao) {
        this.tipoOperacaoConciliacao = tipoOperacaoConciliacao;
    }

    public Date getDataConciliacao() {
        return dataConciliacao;
    }

    public void setDataConciliacao(Date dataConciliacao) {
        this.dataConciliacao = dataConciliacao;
    }

    public Identificador getIdentificador() {
        return identificador;
    }

    public void setIdentificador(Identificador identificador) {
        this.identificador = identificador;
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
        return "Número = " + numero + ", data=" + new SimpleDateFormat("dd/MM/yyyy").format(data) + ", Unidade Organizacional = " + unidadeOrganizacional + ", Operação = " + operacaoConciliacao + ", Tipo =" + tipoConciliacao + ", Valor=" + Valor + ", situacao=" + situacao;
    }
}
