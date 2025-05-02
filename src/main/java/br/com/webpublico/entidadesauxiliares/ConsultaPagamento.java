/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author claudio
 */
public class ConsultaPagamento {

    private Pessoa pessoa;
    private String contribuinte;
    private String cpfCnpjContribuinte;
    private String numeroCadastro;
    private String tipoCadastro;
    private String exercicio;
    private String divida;
    private Date vencimento;
    private String situacao;
    private Date dataEmissao;
    private Date dataPagamento;
    private String numeroParcela;
    private String lote;
    private String banco;
    private BigDecimal saldo;
    private BigDecimal valorParcela;
    private BigDecimal multa;
    private BigDecimal juros;
    private BigDecimal correcao;
    private BigDecimal total;
    private BigDecimal valorPago;
    private ParcelaValorDivida parcela;
    private List<ItemParcelaValorDivida> itens;

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getContribuinte() {
        return contribuinte;
    }

    public void setContribuinte(String contribuinte) {
        this.contribuinte = contribuinte;
    }

    public BigDecimal getCorrecao() {
        return correcao;
    }

    public void setCorrecao(BigDecimal correcao) {
        this.correcao = correcao;
    }

    public String getCpfCnpjContribuinte() {
        return cpfCnpjContribuinte;
    }

    public void setCpfCnpjContribuinte(String cpfCnpjContribuinte) {
        this.cpfCnpjContribuinte = cpfCnpjContribuinte;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getDivida() {
        return divida;
    }

    public void setDivida(String divida) {
        this.divida = divida;
    }

    public String getExercicio() {
        return exercicio;
    }

    public void setExercicio(String exercicio) {
        this.exercicio = exercicio;
    }

    public BigDecimal getJuros() {
        return juros;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public String getNumeroCadastro() {
        return numeroCadastro;
    }

    public void setNumeroCadastro(String numeroCadastro) {
        this.numeroCadastro = numeroCadastro;
    }

    public String getNumeroParcela() {
        return numeroParcela;
    }

    public void setNumeroParcela(String numeroParcela) {
        this.numeroParcela = numeroParcela;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(String tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getValorParcela() {
        return valorParcela;
    }

    public void setValorParcela(BigDecimal valorParcela) {
        this.valorParcela = valorParcela;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    private ConsultaPagamento() {
    }

    public ParcelaValorDivida getParcela() {
        return parcela;
    }

    public void setParcela(ParcelaValorDivida parcela) {
        this.parcela = parcela;
    }

    public List<ItemParcelaValorDivida> getItens() {
        return itens;
    }

    public void setItens(List<ItemParcelaValorDivida> itens) {
        this.itens = itens;
    }

    public ConsultaPagamento(Pessoa pessoa, Cadastro cadastro, Integer ano, Divida divida, SituacaoParcela situacao, Date emissao, BigDecimal saldo, ParcelaValorDivida parcela, ItemLoteBaixa itemLoteBaixa) {
        inicializaPadrao(pessoa, cadastro, ano, divida, parcela, situacao, emissao, saldo, itemLoteBaixa);
    }

    private void inicializaPadrao(Pessoa pessoa, Cadastro cadastro, Integer ano, Divida divida, ParcelaValorDivida parcela, SituacaoParcela situacao, Date emissao, BigDecimal saldo, ItemLoteBaixa itemLoteBaixa) {
        total = BigDecimal.ZERO;
        this.pessoa = pessoa;
        this.contribuinte = pessoa.getNome();
        this.cpfCnpjContribuinte = pessoa.getCpf_Cnpj();
        if (cadastro != null) {
            this.numeroCadastro = cadastro.getNumeroCadastro();
            this.tipoCadastro = cadastro.getTipoDeCadastro().getDescricao();
        } else {
            this.numeroCadastro = pessoa.getCpf_Cnpj();
            this.tipoCadastro = TipoCadastroTributario.PESSOA.getDescricao();
        }
        this.exercicio = String.valueOf(ano);
        this.divida = divida.getDescricao();
        this.vencimento = parcela.getVencimento();
        this.situacao = situacao.getDescricao();
        this.dataEmissao = emissao;
        this.dataPagamento = itemLoteBaixa.getLoteBaixa().getDataPagamento();
        this.numeroParcela = parcela.getSequenciaParcela();
        this.valorParcela = parcela.getValor();
        this.valorPago = itemLoteBaixa.getValorPago();
        this.multa = itemLoteBaixa.getDam().getMulta();
        this.juros = itemLoteBaixa.getDam().getJuros();
        this.correcao = itemLoteBaixa.getDam().getCorrecaoMonetaria();
        this.total = itemLoteBaixa.getDam().getValorOriginal();
        this.saldo = saldo;
        this.parcela = parcela;
        this.lote = itemLoteBaixa.getLoteBaixa().getCodigoLote();
        this.banco = itemLoteBaixa.getLoteBaixa().getBanco().getDescricao();
    }
}
