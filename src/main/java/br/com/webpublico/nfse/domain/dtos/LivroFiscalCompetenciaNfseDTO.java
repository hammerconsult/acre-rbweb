package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.domain.dtos.enums.TipoMovimentoMensalNfseDTO;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class LivroFiscalCompetenciaNfseDTO implements NfseDTO {

    private Long prestadorId;
    private Integer exercicio;
    private Integer mes;
    private TipoMovimentoMensalNfseDTO tipoMovimento;

    private Integer quantidade;
    private BigDecimal issqnProprio;
    private BigDecimal issqnRetido;
    private BigDecimal issqnPago;
    private BigDecimal saldoIssqn;
    private BigDecimal valorServico;
    private List<NotaFiscalSearchDTO> documentos;
    private List<TotalPorNaturezaSituacaoNfseDTO> totalPorNaturezaSituacao;

    private List<br.com.webpublico.nfse.domain.dtos.ItemPlanoContasInternoSearchNfseDTO> contasDesif;

    private Boolean detalhado;

    public LivroFiscalCompetenciaNfseDTO() {
        quantidade = 0;
        issqnProprio = BigDecimal.ZERO;
        issqnRetido = BigDecimal.ZERO;
        issqnPago = BigDecimal.ZERO;
        saldoIssqn = BigDecimal.ZERO;
        valorServico = BigDecimal.ZERO;
        documentos = Lists.newArrayList();
        totalPorNaturezaSituacao = Lists.newArrayList();

        contasDesif = Lists.newArrayList();

        detalhado = Boolean.TRUE;
    }

    public Long getPrestadorId() {
        return prestadorId;
    }

    public void setPrestadorId(Long prestadorId) {
        this.prestadorId = prestadorId;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public TipoMovimentoMensalNfseDTO getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(TipoMovimentoMensalNfseDTO tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getIssqnProprio() {
        return issqnProprio;
    }

    public void setIssqnProprio(BigDecimal issqnProprio) {
        this.issqnProprio = issqnProprio;
    }

    public BigDecimal getIssqnRetido() {
        return issqnRetido;
    }

    public void setIssqnRetido(BigDecimal issqnRetido) {
        this.issqnRetido = issqnRetido;
    }

    public BigDecimal getIssqnPago() {
        return issqnPago;
    }

    public void setIssqnPago(BigDecimal issqnPago) {
        this.issqnPago = issqnPago;
    }

    public BigDecimal getSaldoIssqn() {
        return saldoIssqn;
    }

    public void setSaldoIssqn(BigDecimal saldoIssqn) {
        this.saldoIssqn = saldoIssqn;
    }

    public BigDecimal getValorServico() {
        return valorServico;
    }

    public void setValorServico(BigDecimal valorServico) {
        this.valorServico = valorServico;
    }

    public List<NotaFiscalSearchDTO> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<NotaFiscalSearchDTO> documentos) {
        this.documentos = documentos;
    }

    public List<TotalPorNaturezaSituacaoNfseDTO> getTotalPorNaturezaSituacao() {
        return totalPorNaturezaSituacao;
    }

    public void setTotalPorNaturezaSituacao(List<TotalPorNaturezaSituacaoNfseDTO> totalPorNaturezaSituacao) {
        this.totalPorNaturezaSituacao = totalPorNaturezaSituacao;
    }

    public List<br.com.webpublico.nfse.domain.dtos.ItemPlanoContasInternoSearchNfseDTO> getContasDesif() {
        return contasDesif;
    }

    public void setContasDesif(List<br.com.webpublico.nfse.domain.dtos.ItemPlanoContasInternoSearchNfseDTO> contasDesif) {
        this.contasDesif = contasDesif;
    }

    public Boolean getDetalhado() {
        return detalhado;
    }

    public void setDetalhado(Boolean detalhado) {
        this.detalhado = detalhado;
    }
}
