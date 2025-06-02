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

@Etiqueta("Movimento Grupo Material")
public class MovimentoGrupoMaterial extends SuperEntidade {

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

    @Etiqueta("Tipo Estoque")
    @Enumerated(EnumType.STRING)
    private TipoEstoque tipoEstoque;

    @Etiqueta("Natureza")
    @Enumerated(EnumType.STRING)
    private NaturezaTipoGrupoMaterial naturezaTipoGrupoMaterial;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Operação")
    private TipoOperacaoBensEstoque operacao;

    @ManyToOne
    @Etiqueta("Grupo Material")
    private GrupoMaterial grupoMaterial;

    @Etiqueta("Total de Débito")
    private BigDecimal credito;

    @Etiqueta("Total de Crédito")
    private BigDecimal debito;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Lançamento")
    private TipoLancamento tipoLancamento;

    @Etiqueta("Id Origem")
    private Long origem;

    @Transient
    private TipoOperacao tipoOperacao;
    @Transient
    private BigDecimal valor;
    @Transient
    private Boolean validarSaldo;

    public MovimentoGrupoMaterial() {
        this.credito = BigDecimal.ZERO;
        this.debito = BigDecimal.ZERO;
        this.validarSaldo = Boolean.TRUE;
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

    public TipoEstoque getTipoEstoque() {
        return tipoEstoque;
    }

    public void setTipoEstoque(TipoEstoque tipoEstoque) {
        this.tipoEstoque = tipoEstoque;
    }

    public NaturezaTipoGrupoMaterial getNaturezaTipoGrupoMaterial() {
        return naturezaTipoGrupoMaterial;
    }

    public void setNaturezaTipoGrupoMaterial(NaturezaTipoGrupoMaterial naturezaTipoGrupoMaterial) {
        this.naturezaTipoGrupoMaterial = naturezaTipoGrupoMaterial;
    }

    public TipoOperacaoBensEstoque getOperacao() {
        return operacao;
    }

    public void setOperacao(TipoOperacaoBensEstoque operacao) {
        this.operacao = operacao;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
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

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public TipoOperacao getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(TipoOperacao tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
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

    public Long getOrigem() {
        return origem;
    }

    public void setOrigem(Long origem) {
        this.origem = origem;
    }

    public boolean isNaturezaAjustePerda(){
        return this.naturezaTipoGrupoMaterial != null && NaturezaTipoGrupoMaterial.AJUSTE_PERDA.equals(this.naturezaTipoGrupoMaterial);
    }

    public boolean isNaturezaOriginal(){
        return this.naturezaTipoGrupoMaterial != null && NaturezaTipoGrupoMaterial.ORIGINAL.equals(this.naturezaTipoGrupoMaterial);
    }

    public boolean isNaturezaVPD(){
        return this.naturezaTipoGrupoMaterial != null && NaturezaTipoGrupoMaterial.VPD.equals(this.naturezaTipoGrupoMaterial);
    }

    public boolean isNaturezaVPA(){
        return this.naturezaTipoGrupoMaterial != null && NaturezaTipoGrupoMaterial.VPA.equals(this.naturezaTipoGrupoMaterial);
    }

    public boolean isNaturezaReducao(){
        return this.naturezaTipoGrupoMaterial != null && NaturezaTipoGrupoMaterial.REDUCAO.equals(this.naturezaTipoGrupoMaterial);
    }
}
