package br.com.webpublico.pncp.dto;

import br.com.webpublico.util.DataUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class ResultadoItemDTO {

    @JsonIgnore
    private Long itemProcessoDeCompraId;
    @JsonIgnore
    private String sequencialPncp;
    @JsonIgnore
    private Integer numeroItem;

    private BigDecimal quantidadeHomologada;
    private BigDecimal valorUnitarioHomologado;
    private BigDecimal valorTotalHomologado;
    private BigDecimal percentualDesconto;
    private String tipoPessoaId;
    private String niFornecedor;
    private String nomeRazaoSocialFornecedor;
    private Integer porteFornecedorId;
    private Integer naturezaJuridicaId;
    private String codigoPais;
    private Boolean indicadorSubcontratacao;
    private Integer ordemClassificacaoSrp;
    private String dataResultado;

    public ResultadoItemDTO(){}

    public ResultadoItemDTO(BigDecimal quantidadeHomologada, BigDecimal valorUnitarioHomologado, BigDecimal valorTotalHomologado, BigDecimal percentualDesconto, String tipoPessoaId, String niFornecedor, String nomeRazaoSocialFornecedor, Integer porteFornecedorId, Integer naturezaJuridicaId, String codigoPais, Boolean indicadorSubcontratacao, Integer ordemClassificacaoSrp, String dataResultado, Long itemProcessoDeCompraId, String sequencialPncp, Integer numeroItem) {
        this.quantidadeHomologada = quantidadeHomologada;
        this.valorUnitarioHomologado = valorUnitarioHomologado;
        this.valorTotalHomologado = valorTotalHomologado;
        this.percentualDesconto = percentualDesconto;
        this.tipoPessoaId = tipoPessoaId;
        this.niFornecedor = niFornecedor;
        this.nomeRazaoSocialFornecedor = nomeRazaoSocialFornecedor;
        this.porteFornecedorId = porteFornecedorId;
        this.naturezaJuridicaId = naturezaJuridicaId;
        this.codigoPais = codigoPais;
        this.indicadorSubcontratacao = indicadorSubcontratacao;
        this.ordemClassificacaoSrp = ordemClassificacaoSrp;
        this.dataResultado = dataResultado;
        this.itemProcessoDeCompraId = itemProcessoDeCompraId;
        this.sequencialPncp = sequencialPncp;
        this.numeroItem = numeroItem;
    }

    public BigDecimal getQuantidadeHomologada() {
        return quantidadeHomologada;
    }

    public void setQuantidadeHomologada(BigDecimal quantidadeHomologada) {
        this.quantidadeHomologada = quantidadeHomologada;
    }

    public BigDecimal getValorUnitarioHomologado() {
        return valorUnitarioHomologado;
    }

    public void setValorUnitarioHomologado(BigDecimal valorUnitarioHomologado) {
        this.valorUnitarioHomologado = valorUnitarioHomologado;
    }

    public BigDecimal getValorTotalHomologado() {
        return valorTotalHomologado;
    }

    public void setValorTotalHomologado(BigDecimal valorTotalHomologado) {
        this.valorTotalHomologado = valorTotalHomologado;
    }

    public BigDecimal getPercentualDesconto() {
        return percentualDesconto;
    }

    public void setPercentualDesconto(BigDecimal percentualDesconto) {
        this.percentualDesconto = percentualDesconto;
    }

    public String getTipoPessoaId() {
        return tipoPessoaId;
    }

    public void setTipoPessoaId(String tipoPessoaId) {
        this.tipoPessoaId = tipoPessoaId;
    }

    public String getNiFornecedor() {
        return niFornecedor;
    }

    public void setNiFornecedor(String niFornecedor) {
        this.niFornecedor = niFornecedor;
    }

    public String getNomeRazaoSocialFornecedor() {
        return nomeRazaoSocialFornecedor;
    }

    public void setNomeRazaoSocialFornecedor(String nomeRazaoSocialFornecedor) {
        this.nomeRazaoSocialFornecedor = nomeRazaoSocialFornecedor;
    }

    public Integer getPorteFornecedorId() {
        return porteFornecedorId;
    }

    public void setPorteFornecedorId(Integer porteFornecedorId) {
        this.porteFornecedorId = porteFornecedorId;
    }

    public Integer getNaturezaJuridicaId() {
        return naturezaJuridicaId;
    }

    public void setNaturezaJuridicaId(Integer naturezaJuridicaId) {
        this.naturezaJuridicaId = naturezaJuridicaId;
    }

    public String getCodigoPais() {
        return codigoPais;
    }

    public void setCodigoPais(String codigoPais) {
        this.codigoPais = codigoPais;
    }

    public Boolean isIndicadorSubcontratacao() {
        return indicadorSubcontratacao;
    }

    public void setIndicadorSubcontratacao(Boolean indicadorSubcontratacao) {
        this.indicadorSubcontratacao = indicadorSubcontratacao;
    }

    public Integer getOrdemClassificacaoSrp() {
        return ordemClassificacaoSrp;
    }

    public void setOrdemClassificacaoSrp(Integer ordemClassificacaoSrp) {
        this.ordemClassificacaoSrp = ordemClassificacaoSrp;
    }

    public String getDataResultado() {
        return dataResultado;
    }

    public void setDataResultado(String dataResultado) {
        this.dataResultado = dataResultado;
    }

    public Long getItemProcessoDeCompraId() {
        return itemProcessoDeCompraId;
    }

    public void setItemProcessoDeCompraId(Long itemProcessoDeCompraId) {
        this.itemProcessoDeCompraId = itemProcessoDeCompraId;
    }

    public String getSequencialPncp() {
        return sequencialPncp;
    }

    public void setSequencialPncp(String sequencialPncp) {
        this.sequencialPncp = sequencialPncp;
    }

    public Integer getNumeroItem() {
        return numeroItem;
    }

    public void setNumeroItem(Integer numeroItem) {
        this.numeroItem = numeroItem;
    }

    public static List<ResultadoItemDTO> mapearObjetosParaDtos(List<Object> resultList) {
        List<ResultadoItemDTO> retorno = Lists.newArrayList();
        if (resultList == null) {
            return retorno;
        }

        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;
            BigDecimal quantidadeHomologada = objeto[0] != null ? (BigDecimal) objeto[0] : null;
            BigDecimal valorUnitarioHomologado = objeto[1] != null ? (BigDecimal) objeto[1] : null;
            BigDecimal valorTotalHomologado = quantidadeHomologada != null && valorUnitarioHomologado != null ? quantidadeHomologada.multiply(valorUnitarioHomologado).setScale(2, RoundingMode.HALF_EVEN) : null;
            BigDecimal percentualDesconto = objeto[3] != null ? (BigDecimal) objeto[3] : null;
            String tipoPessoaId = (String) objeto[4];
            String niFornecedor = objeto[5] != null ? ((String) objeto[5]).replaceAll("\\D", "") : null;
            String nomeRazaoSocialFornecedor = (String) objeto[6];
            Integer porteFornecedorId = objeto[7] != null ? Integer.parseInt(String.valueOf(objeto[7])) : null;
            Integer naturezaJuridicaId = objeto[8] != null ? ((BigDecimal) objeto[8]).intValue() : null;
            String codigoPais = (String) objeto[9];
            Boolean indicadorSubcontratacao = objeto[10] != null ? (((BigDecimal) objeto[10]).intValue() == 1 ? Boolean.TRUE : Boolean.FALSE) : null;
            Integer ordemClassificacaoSrp = objeto[11] != null ? ((BigDecimal) objeto[11]).intValue() : null;
            String dataResultado = objeto[12] != null ? DataUtil.getDataFormatada(new Date(((Timestamp) objeto[12]).getTime()), "yyyy-MM-dd") : null;
            Long itemProcessoDeCompraId = objeto[13] != null ? ((BigDecimal) objeto[13]).longValue() : null;
            String sequencialPncp = (String) objeto[14];
            Integer numeroItem = objeto[15] != null ? ((BigDecimal) objeto[15]).intValue() : null;
            String numeroLote = objeto[16] != null ? String.valueOf(objeto[16]) : null;
            Integer numeroItemPncp = Integer.valueOf(numeroLote + String.format("%04d", numeroItem));
            ResultadoItemDTO resultadoItemDTO = new ResultadoItemDTO(
                quantidadeHomologada, valorUnitarioHomologado, valorTotalHomologado, percentualDesconto,
                tipoPessoaId, niFornecedor, nomeRazaoSocialFornecedor, porteFornecedorId, naturezaJuridicaId,
                codigoPais, indicadorSubcontratacao, ordemClassificacaoSrp, dataResultado, itemProcessoDeCompraId, sequencialPncp,
                numeroItemPncp);
            retorno.add(resultadoItemDTO);
        }

        return retorno;
    }

}
