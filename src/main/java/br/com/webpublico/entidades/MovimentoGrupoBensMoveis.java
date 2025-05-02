/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Edi
 */
@Entity

@Etiqueta("Movimento Grupo Bens Móveis")
public class MovimentoGrupoBensMoveis extends SuperEntidade{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data do Movimento")
    private Date dataMovimento;

    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;

    @Etiqueta("Tipo Grupo")
    @Enumerated(EnumType.STRING)
    private TipoGrupo tipoGrupo;

    @Etiqueta("Natureza")
    @Enumerated(EnumType.STRING)
    private NaturezaTipoGrupoBem naturezaTipoGrupoBem;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Operação")
    private TipoOperacaoBensMoveis operacao;

    @ManyToOne
    @Etiqueta("Grupo Patrimonial")
    private GrupoBem grupoBem;

    @Etiqueta("Total de Débito")
    private BigDecimal credito;

    @Etiqueta("Total de Crédito")
    private BigDecimal debito;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Lançamento")
    private TipoLancamento tipoLancamento;

    @Transient
    private TipoOperacao tipoOperacao;
    @Transient
    private BigDecimal valor;
    @Transient
    private Boolean validarSaldo;


    public MovimentoGrupoBensMoveis() {
        this.credito = BigDecimal.ZERO;
        this.debito = BigDecimal.ZERO;
        validarSaldo = Boolean.TRUE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public TipoGrupo getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoGrupo tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    public NaturezaTipoGrupoBem getNaturezaTipoGrupoBem() {
        return naturezaTipoGrupoBem;
    }

    public void setNaturezaTipoGrupoBem(NaturezaTipoGrupoBem naturezaTipoGrupoBem) {
        this.naturezaTipoGrupoBem = naturezaTipoGrupoBem;
    }

    public TipoOperacaoBensMoveis getOperacao() {
        return operacao;
    }

    public void setOperacao(TipoOperacaoBensMoveis operacao) {
        this.operacao = operacao;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public BigDecimal getCredito() {
        return credito;
    }

    public void setCredito(BigDecimal credito) {
        this.credito = credito;
    }

    public BigDecimal getDebito() {
        return debito;
    }

    public void setDebito(BigDecimal debito) {
        this.debito = debito;
    }

    public TipoOperacao getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(TipoOperacao tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Boolean getValidarSaldo() {
        return validarSaldo;
    }

    public void setValidarSaldo(Boolean validarSaldo) {
        this.validarSaldo = validarSaldo;
    }

    public boolean isLancamentoNormal() {
        return this.tipoLancamento != null && TipoLancamento.NORMAL.equals(this.tipoLancamento);
    }

    public boolean isOperacaoDebito() {
        return this.tipoOperacao != null && TipoOperacao.DEBITO.equals(this.tipoOperacao);
    }
}
