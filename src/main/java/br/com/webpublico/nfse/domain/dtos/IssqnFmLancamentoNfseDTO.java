package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.domain.dtos.enums.IssqnFmTipoLancamentoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.enums.MesNfseDTO;
import br.com.webpublico.nfse.domain.dtos.enums.TipoPessoaNfseDTO;

import java.math.BigDecimal;

public class IssqnFmLancamentoNfseDTO implements NfseDTO {

    private TipoPessoaNfseDTO tipoPessoa;
    private UFNfseDTO uf;
    private PessoaNfseDTO pessoa;
    private IssqnFmTipoLancamentoNfseDTO tipoLancamento;
    private Integer exercicio;
    private MesNfseDTO mes;
    private BigDecimal faturamento;
    private BigDecimal baseCalculo;
    private BigDecimal aliquota;
    private BigDecimal valor;
    private String observacao;

    public TipoPessoaNfseDTO getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(TipoPessoaNfseDTO tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public UFNfseDTO getUf() {
        return uf;
    }

    public void setUf(UFNfseDTO uf) {
        this.uf = uf;
    }

    public PessoaNfseDTO getPessoa() {
        return pessoa;
    }

    public void setPessoa(PessoaNfseDTO pessoa) {
        this.pessoa = pessoa;
    }

    public IssqnFmTipoLancamentoNfseDTO getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(IssqnFmTipoLancamentoNfseDTO tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public MesNfseDTO getMes() {
        return mes;
    }

    public void setMes(MesNfseDTO mes) {
        this.mes = mes;
    }

    public BigDecimal getFaturamento() {
        return faturamento;
    }

    public void setFaturamento(BigDecimal faturamento) {
        this.faturamento = faturamento;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public BigDecimal getAliquota() {
        return aliquota;
    }

    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
