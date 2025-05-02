package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ContaDeDestinacao;
import br.com.webpublico.entidades.DividaPublica;
import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.Intervalo;
import br.com.webpublico.enums.OperacaoDiarioDividaPublica;
import br.com.webpublico.enums.OperacaoMovimentoDividaPublica;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Mateus on 02/02/2015.
 */
public class SaldoDividaPublicaReprocessamento {

    private Date data;
    private BigDecimal valor;
    private UnidadeOrganizacional unidadeOrganizacional;
    private DividaPublica dividaPublica;
    private OperacaoMovimentoDividaPublica operacaoMovimentoDividaPublica;
    private Boolean estorno;
    private OperacaoDiarioDividaPublica operacaoDiarioDividaPublica;
    private Intervalo intervalo;
    private ContaDeDestinacao contaDeDestinacao;

    public SaldoDividaPublicaReprocessamento() {

    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public DividaPublica getDividaPublica() {
        return dividaPublica;
    }

    public void setDividaPublica(DividaPublica dividaPublica) {
        this.dividaPublica = dividaPublica;
    }

    public OperacaoMovimentoDividaPublica getOperacaoMovimentoDividaPublica() {
        return operacaoMovimentoDividaPublica;
    }

    public void setOperacaoMovimentoDividaPublica(OperacaoMovimentoDividaPublica operacaoMovimentoDividaPublica) {
        this.operacaoMovimentoDividaPublica = operacaoMovimentoDividaPublica;
    }

    public Boolean getEstorno() {
        return estorno;
    }

    public void setEstorno(Boolean estorno) {
        this.estorno = estorno;
    }

    public OperacaoDiarioDividaPublica getOperacaoDiarioDividaPublica() {
        return operacaoDiarioDividaPublica;
    }

    public void setOperacaoDiarioDividaPublica(OperacaoDiarioDividaPublica operacaoDiarioDividaPublica) {
        this.operacaoDiarioDividaPublica = operacaoDiarioDividaPublica;
    }

    public Intervalo getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(Intervalo intervalo) {
        this.intervalo = intervalo;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }
}
