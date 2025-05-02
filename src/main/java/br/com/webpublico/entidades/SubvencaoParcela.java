package br.com.webpublico.entidades;

import br.com.webpublico.enums.NaturezaDividaSubvencao;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.util.Util;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 27/12/13
 * Time: 16:04
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class SubvencaoParcela extends SuperEntidade implements Serializable, Comparable<SubvencaoParcela> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private ParcelaValorDivida parcelaValorDivida;
    @ManyToOne
    private SubvencaoEmpresas subvencaoEmpresas;
    private String cadastro;
    private String tipoCadastro;
    private String divida;
    private Integer exercicio;
    private String parcela;
    private Integer sd;
    private Date vencimento;
    private BigDecimal valorOriginal;

    private BigDecimal restoDoValorSubvencionado;
    private BigDecimal valorSubvencionado;
    private BigDecimal total;
    private BigDecimal valorResidual;
    private String situacao;
    private String tipoDeDebito;

    private BigDecimal imposto;
    private BigDecimal taxa;
    private BigDecimal desconto;
    private BigDecimal juros;
    private BigDecimal multa;
    private BigDecimal correcao;
    private BigDecimal valorHonorarios;
    private String referencia;
    private BigDecimal honorarioAtualCalculo;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataLancamento;
    @Temporal(TemporalType.TIMESTAMP)
    private Date emissao;
    @ManyToOne
    private DAM dam;
    @Enumerated(EnumType.STRING)
    private NaturezaDividaSubvencao naturezaDividaSubvencao;

    public SubvencaoParcela() {
        valorOriginal = BigDecimal.ZERO;
        valorHonorarios = BigDecimal.ZERO;
        restoDoValorSubvencionado = BigDecimal.ZERO;
        restoDoValorSubvencionado = BigDecimal.ZERO;
        valorResidual = BigDecimal.ZERO;
    }

    public BigDecimal getHonorarioAtualCalculo() {
        return honorarioAtualCalculo;
    }

    public void setHonorarioAtualCalculo(BigDecimal honorarioAtualCalculo) {
        this.honorarioAtualCalculo = honorarioAtualCalculo;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public String getTipoDeDebito() {
        return tipoDeDebito;
    }

    public void setTipoDeDebito(String tipoDeDebito) {
        this.tipoDeDebito = tipoDeDebito;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getRestoDoValorSubvencionado() {
        return restoDoValorSubvencionado;
    }

    public void setRestoDoValorSubvencionado(BigDecimal restoDoValorSubvencionado) {
        this.restoDoValorSubvencionado = restoDoValorSubvencionado;
    }

    public BigDecimal getValorResidual() {
        return valorResidual;
    }

    public void setValorResidual(BigDecimal valorResidual) {
        this.valorResidual = valorResidual;
    }

    public String getVencimentoToString() {
        return Util.dateToString(vencimento);
    }

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public String getEmissaoToString() {
        return emissao != null ? Util.dateToString(emissao) : "";
    }

    public BigDecimal getImposto() {
        return imposto;
    }

    public void setImposto(BigDecimal imposto) {
        this.imposto = imposto;
    }

    public BigDecimal getTaxa() {
        return taxa;
    }

    public void setTaxa(BigDecimal taxa) {
        this.taxa = taxa;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getJuros() {
        return juros;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getCorrecao() {
        return correcao;
    }

    public void setCorrecao(BigDecimal correcao) {
        this.correcao = correcao;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public NaturezaDividaSubvencao getNaturezaDividaSubvencao() {
        return naturezaDividaSubvencao;
    }

    public void setNaturezaDividaSubvencao(NaturezaDividaSubvencao naturezaDividaSubvencao) {
        this.naturezaDividaSubvencao = naturezaDividaSubvencao;
    }

    public String getSituacaoDescricaoEnum() {
        if (situacao != null) {
            SituacaoParcela sp = Enum.valueOf(SituacaoParcela.class, situacao);
            return sp.getDescricao();
        }
        return situacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public BigDecimal getValorSubvencionado() {
        return valorSubvencionado;
    }

    public void setValorSubvencionado(BigDecimal valorSubvencionado) {
        this.valorSubvencionado = valorSubvencionado;
    }

    public String getCadastro() {
        return cadastro;
    }

    public void setCadastro(String cadastro) {
        this.cadastro = cadastro;
    }

    public String getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(String tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public String getDivida() {
        return divida;
    }

    public void setDivida(String divida) {
        this.divida = divida;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public String getParcela() {
        return parcela;
    }

    public void setParcela(String parcela) {
        this.parcela = parcela;
    }

    public Integer getSd() {
        return sd;
    }

    public void setSd(Integer sd) {
        this.sd = sd;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    public BigDecimal getValorHonorarios() {
        return valorHonorarios;
    }

    public void setValorHonorarios(BigDecimal valorHonorarios) {
        this.valorHonorarios = valorHonorarios;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public ParcelaValorDivida getParcelaValorDivida() {
        return parcelaValorDivida;
    }

    public void setParcelaValorDivida(ParcelaValorDivida parcelaValorDivida) {
        this.parcelaValorDivida = parcelaValorDivida;
    }

    public SubvencaoEmpresas getSubvencaoEmpresas() {
        return subvencaoEmpresas;
    }

    public void setSubvencaoEmpresas(SubvencaoEmpresas subvencaoEmpresas) {
        this.subvencaoEmpresas = subvencaoEmpresas;
    }

    public DAM getDam() {
        return dam;
    }

    public void setDam(DAM dam) {
        this.dam = dam;
    }

    @Override
    public int compareTo(SubvencaoParcela o) {
        return this.getDataLancamento().compareTo(o.getDataLancamento());
    }
}


