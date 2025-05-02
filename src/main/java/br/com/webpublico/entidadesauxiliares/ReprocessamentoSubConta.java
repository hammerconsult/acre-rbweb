package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.MovimentacaoFinanceira;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Mateus on 02/02/2015.
 */
public class ReprocessamentoSubConta {
    private Date dataMovimento;
    private BigDecimal valorCredito;
    private BigDecimal valorDebito;
    private SubConta subConta;
    private ContaDeDestinacao contaDeDestinacao;
    private UnidadeOrganizacional unidadeOrganizacional;
    private MovimentacaoFinanceira tipoMovimento;
    private EventoContabil eventoContabil;
    private String historico;
    private String uuid;

    public ReprocessamentoSubConta() {
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public BigDecimal getValorCredito() {
        return valorCredito;
    }

    public void setValorCredito(BigDecimal valorCredito) {
        this.valorCredito = valorCredito;
    }

    public BigDecimal getValorDebito() {
        return valorDebito;
    }

    public void setValorDebito(BigDecimal valorDebito) {
        this.valorDebito = valorDebito;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public MovimentacaoFinanceira getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(MovimentacaoFinanceira tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
