package br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil;

import br.com.webpublico.enums.Intervalo;
import br.com.webpublico.enums.NaturezaDividaAtivaCreditoReceber;
import br.com.webpublico.enums.OperacaoDividaAtiva;
import br.com.webpublico.enums.TipoLancamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SaldoDividaAtivaContabilDTO {
    private Long id;
    private LocalDate dataSaldo;
    private Long idUnidadeOrganizacional;
    private Long idContaReceita;
    private NaturezaDividaAtivaCreditoReceber natureza;
    private Intervalo intervalo;
    private Long idContaDeDestinacao;
    private OperacaoDividaAtiva operacaoDividaAtiva;
    private TipoLancamento tipoLancamento;
    private BigDecimal valor;

    public SaldoDividaAtivaContabilDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataSaldo() {
        return dataSaldo;
    }

    public void setDataSaldo(LocalDate dataSaldo) {
        this.dataSaldo = dataSaldo;
    }

    public Long getIdUnidadeOrganizacional() {
        return idUnidadeOrganizacional;
    }

    public void setIdUnidadeOrganizacional(Long idUnidadeOrganizacional) {
        this.idUnidadeOrganizacional = idUnidadeOrganizacional;
    }

    public Long getIdContaReceita() {
        return idContaReceita;
    }

    public void setIdContaReceita(Long idContaReceita) {
        this.idContaReceita = idContaReceita;
    }

    public NaturezaDividaAtivaCreditoReceber getNatureza() {
        return natureza;
    }

    public void setNatureza(NaturezaDividaAtivaCreditoReceber natureza) {
        this.natureza = natureza;
    }

    public Intervalo getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(Intervalo intervalo) {
        this.intervalo = intervalo;
    }

    public Long getIdContaDeDestinacao() {
        return idContaDeDestinacao;
    }

    public void setIdContaDeDestinacao(Long idContaDeDestinacao) {
        this.idContaDeDestinacao = idContaDeDestinacao;
    }

    public OperacaoDividaAtiva getOperacaoDividaAtiva() {
        return operacaoDividaAtiva;
    }

    public void setOperacaoDividaAtiva(OperacaoDividaAtiva operacaoDividaAtiva) {
        this.operacaoDividaAtiva = operacaoDividaAtiva;
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
}

