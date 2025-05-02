package br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil;

import br.com.webpublico.enums.MovimentacaoFinanceira;
import br.com.webpublico.enums.TipoOperacao;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SaldoSubContaDTO {
    private Long id;
    private Long idUnidade;
    private Long idSubConta;
    private Long idContaDeDestinacao;
    private BigDecimal valor;
    private TipoOperacao operacao;
    private LocalDate data;
    private Long idEventoContabil;
    private String historicoRazao;
    private MovimentacaoFinanceira tipoDeMovimento;
    private String uuid;
    private Boolean realizarValidacoes;

    public SaldoSubContaDTO() {
    }

    public Long getIdUnidade() {
        return idUnidade;
    }

    public void setIdUnidade(Long idUnidade) {
        this.idUnidade = idUnidade;
    }

    public Long getIdSubConta() {
        return idSubConta;
    }

    public void setIdSubConta(Long idSubConta) {
        this.idSubConta = idSubConta;
    }

    public Long getIdContaDeDestinacao() {
        return idContaDeDestinacao;
    }

    public void setIdContaDeDestinacao(Long idContaDeDestinacao) {
        this.idContaDeDestinacao = idContaDeDestinacao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public TipoOperacao getOperacao() {
        return operacao;
    }

    public void setOperacao(TipoOperacao operacao) {
        this.operacao = operacao;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Long getIdEventoContabil() {
        return idEventoContabil;
    }

    public void setIdEventoContabil(Long idEventoContabil) {
        this.idEventoContabil = idEventoContabil;
    }

    public String getHistoricoRazao() {
        return historicoRazao;
    }

    public void setHistoricoRazao(String historicoRazao) {
        this.historicoRazao = historicoRazao;
    }

    public MovimentacaoFinanceira getTipoDeMovimento() {
        return tipoDeMovimento;
    }

    public void setTipoDeMovimento(MovimentacaoFinanceira tipoDeMovimento) {
        this.tipoDeMovimento = tipoDeMovimento;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean getRealizarValidacoes() {
        return realizarValidacoes;
    }

    public void setRealizarValidacoes(Boolean realizarValidacoes) {
        this.realizarValidacoes = realizarValidacoes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
