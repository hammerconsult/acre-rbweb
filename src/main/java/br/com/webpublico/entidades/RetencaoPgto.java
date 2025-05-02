/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoConsignacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author major
 */
@Entity
@GrupoDiagrama(nome = "Contabil")
@Audited
@Etiqueta("Retenções de Pagamento")

public class RetencaoPgto implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Número")
    @Tabelavel
    private String numero;
    @Obrigatorio
    @Etiqueta("Valor")
    @Tabelavel
    private BigDecimal valor;
    @Etiqueta("Data")
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataRetencao;
    @Tabelavel
    private BigDecimal saldo;
    @Etiqueta("Complemento Histórico")
    private String complementoHistorico;
    @Obrigatorio
    @Etiqueta("Conta Extraorçamentária")
    @ManyToOne
    private Conta contaExtraorcamentaria;
    @Etiqueta("Fonte de Recursos")
    @ManyToOne
    @Obrigatorio
    private FonteDeRecursos fonteDeRecursos;
    @Tabelavel
    @Etiqueta("Conta Financeira")
    @ManyToOne
    private SubConta subConta;
    @Obrigatorio
    @Etiqueta("Usuário do Sistema")
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @ManyToOne
    private Pagamento pagamento;
    @Etiqueta("Estorno de Pagamento")
    @ManyToOne
    private PagamentoEstorno pagamentoEstorno;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;
    private Boolean efetivado;
    private Boolean estornado;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Pessoa")
    private Pessoa pessoa;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Classe")
    private ClasseCredor classeCredor;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private TipoConsignacao tipoConsignacao;
    @Transient
    private Long criadoEm;
    @Transient
    private BigDecimal porcentagem;

    public RetencaoPgto() {
        valor = new BigDecimal(BigInteger.ZERO);
        saldo = new BigDecimal(BigInteger.ZERO);
        efetivado = false;
        estornado = false;
        this.criadoEm = System.nanoTime();
        porcentagem = BigDecimal.ZERO;
    }

    public RetencaoPgto(String numero, BigDecimal valor, Date dataRetencao, BigDecimal saldo, String complementoHistorico, SubConta subConta, FonteDeRecursos fonteDeRecursos, Conta contaExtraorcamentaria, UsuarioSistema usuarioSistema, Pagamento pagamento, UnidadeOrganizacional unidadeOrganizacional, Boolean efetivado, Boolean estornado, TipoConsignacao tipoConsignacao) {
        this.numero = numero;
        this.valor = valor;
        this.dataRetencao = dataRetencao;
        this.saldo = saldo;
        this.complementoHistorico = complementoHistorico;
        this.subConta = subConta;
        this.fonteDeRecursos = fonteDeRecursos;
        this.contaExtraorcamentaria = contaExtraorcamentaria;
        this.usuarioSistema = usuarioSistema;
        this.pagamento = pagamento;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.efetivado = efetivado;
        this.estornado = estornado;
        this.tipoConsignacao = tipoConsignacao;
        this.criadoEm = System.nanoTime();
    }

    public Boolean getEstornado() {
        return estornado;
    }

    public void setEstornado(Boolean estornado) {
        this.estornado = estornado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComplementoHistorico() {
        return complementoHistorico;
    }

    public void setComplementoHistorico(String complementoHistorico) {
        this.complementoHistorico = complementoHistorico;
    }

    public Conta getContaExtraorcamentaria() {
        return contaExtraorcamentaria;
    }

    public void setContaExtraorcamentaria(Conta contaExtraorcamentaria) {
        this.contaExtraorcamentaria = contaExtraorcamentaria;
    }

    public Boolean getEfetivado() {
        return efetivado;
    }

    public void setEfetivado(Boolean efetivado) {
        this.efetivado = efetivado;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getDataRetencao() {
        return dataRetencao;
    }

    public void setDataRetencao(Date dataRetencao) {
        this.dataRetencao = dataRetencao;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public TipoConsignacao getTipoConsignacao() {
        return tipoConsignacao;
    }

    public void setTipoConsignacao(TipoConsignacao tipoConsignacao) {
        this.tipoConsignacao = tipoConsignacao;
    }

    public PagamentoEstorno getPagamentoEstorno() {
        return pagamentoEstorno;
    }

    public void setPagamentoEstorno(PagamentoEstorno pagamentoEstorno) {
        this.pagamentoEstorno = pagamentoEstorno;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public BigDecimal getPorcentagem() {
        return porcentagem;
    }

    public void setPorcentagem(BigDecimal porcentagem) {
        this.porcentagem = porcentagem;
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
        return "Nº " + numero + " (" + DataUtil.getDataFormatada(dataRetencao) + ") " + Util.formataValor(valor);
    }
}
