package br.com.webpublico.entidadesauxiliares.softplan.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@JsonTypeName("dadosAlteracaoParcelamento")
public class DadosAlteracaoParcelamentoSoftPlanDTO extends DadosServicoSoftPlanDTO {

    private Long idParcelamento;
    private Long idCda;
    private String nuParcelamento;
    private BigInteger tpSituacao;
    private String dtEvento;
    private String deObservacao;
    protected BigInteger qtdeParcelasPagas;
    protected BigInteger qtdeParcelasAberto;
    protected BigDecimal vlPagoParcelamento;
    protected String dtUltimoPagamento;
    private List<CDA> saldoRemanescenteCDA;

    public DadosAlteracaoParcelamentoSoftPlanDTO() {
        saldoRemanescenteCDA = Lists.newArrayList();
    }

    public Long getIdParcelamento() {
        return idParcelamento;
    }

    public void setIdParcelamento(Long idParcelamento) {
        this.idParcelamento = idParcelamento;
    }

    public Long getIdCda() {
        return idCda;
    }

    public void setIdCda(Long idCda) {
        this.idCda = idCda;
    }

    public String getNuParcelamento() {
        return nuParcelamento;
    }

    public void setNuParcelamento(String nuParcelamento) {
        this.nuParcelamento = nuParcelamento;
    }

    public BigInteger getTpSituacao() {
        return tpSituacao;
    }

    public void setTpSituacao(BigInteger tpSituacao) {
        this.tpSituacao = tpSituacao;
    }

    public String getDtEvento() {
        return dtEvento;
    }

    public void setDtEvento(String dtEvento) {
        this.dtEvento = dtEvento;
    }

    public String getDeObservacao() {
        return deObservacao;
    }

    public void setDeObservacao(String deObservacao) {
        this.deObservacao = deObservacao;
    }

    public BigInteger getQtdeParcelasPagas() {
        return qtdeParcelasPagas;
    }

    public void setQtdeParcelasPagas(BigInteger qtdeParcelasPagas) {
        this.qtdeParcelasPagas = qtdeParcelasPagas;
    }

    public BigInteger getQtdeParcelasAberto() {
        return qtdeParcelasAberto;
    }

    public void setQtdeParcelasAberto(BigInteger qtdeParcelasAberto) {
        this.qtdeParcelasAberto = qtdeParcelasAberto;
    }

    public BigDecimal getVlPagoParcelamento() {
        return vlPagoParcelamento;
    }

    public void setVlPagoParcelamento(BigDecimal vlPagoParcelamento) {
        this.vlPagoParcelamento = vlPagoParcelamento;
    }

    public String getDtUltimoPagamento() {
        return dtUltimoPagamento;
    }

    public void setDtUltimoPagamento(String dtUltimoPagamento) {
        this.dtUltimoPagamento = dtUltimoPagamento;
    }

    public List<CDA> getSaldoRemanescenteCDA() {
        return saldoRemanescenteCDA;
    }

    public void setSaldoRemanescenteCDA(List<CDA> saldoRemanescenteCDA) {
        this.saldoRemanescenteCDA = saldoRemanescenteCDA;
    }

    public static class CDA {
        private String nuCDA;
        private BigDecimal vlAtualizadoPrincipal;
        private BigDecimal vlAtualizadoMulta;
        private BigDecimal vlAtualizadoJuros;
        private BigDecimal vlAtualizadoTotalCDA;
        private String seq;

        public CDA() {
        }

        public String getNuCDA() {
            return nuCDA;
        }

        public void setNuCDA(String nuCDA) {
            this.nuCDA = nuCDA;
        }

        public BigDecimal getVlAtualizadoPrincipal() {
            return vlAtualizadoPrincipal;
        }

        public void setVlAtualizadoPrincipal(BigDecimal vlAtualizadoPrincipal) {
            this.vlAtualizadoPrincipal = vlAtualizadoPrincipal;
        }

        public BigDecimal getVlAtualizadoMulta() {
            return vlAtualizadoMulta;
        }

        public void setVlAtualizadoMulta(BigDecimal vlAtualizadoMulta) {
            this.vlAtualizadoMulta = vlAtualizadoMulta;
        }

        public BigDecimal getVlAtualizadoJuros() {
            return vlAtualizadoJuros;
        }

        public void setVlAtualizadoJuros(BigDecimal vlAtualizadoJuros) {
            this.vlAtualizadoJuros = vlAtualizadoJuros;
        }

        public BigDecimal getVlAtualizadoTotalCDA() {
            return vlAtualizadoTotalCDA;
        }

        public void setVlAtualizadoTotalCDA(BigDecimal vlAtualizadoTotalCDA) {
            this.vlAtualizadoTotalCDA = vlAtualizadoTotalCDA;
        }

        public String getSeq() {
            return seq;
        }

        public void setSeq(String seq) {
            this.seq = seq;
        }
    }
}
