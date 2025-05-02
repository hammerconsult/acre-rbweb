package br.com.webpublico.pncp.dto;

public class ResultadoItemConsultaDTO {

    private Double quantidadeHomologada;
    private Double valorUnitarioHomologado;
    private Double valorTotalHomologado;
    private String nomeRazaoSocialFornecedor;
    private String dataResultado;
    private Integer numeroLote;
    private Integer numeroItem;
    private Integer numeroItemComLote;
    private String sequencialPncp;

    public Double getQuantidadeHomologada() {
        return quantidadeHomologada;
    }

    public void setQuantidadeHomologada(Double quantidadeHomologada) {
        this.quantidadeHomologada = quantidadeHomologada;
    }

    public Double getValorUnitarioHomologado() {
        return valorUnitarioHomologado;
    }

    public void setValorUnitarioHomologado(Double valorUnitarioHomologado) {
        this.valorUnitarioHomologado = valorUnitarioHomologado;
    }

    public Double getValorTotalHomologado() {
        return valorTotalHomologado;
    }

    public void setValorTotalHomologado(Double valorTotalHomologado) {
        this.valorTotalHomologado = valorTotalHomologado;
    }

    public String getNomeRazaoSocialFornecedor() {
        return nomeRazaoSocialFornecedor;
    }

    public void setNomeRazaoSocialFornecedor(String nomeRazaoSocialFornecedor) {
        this.nomeRazaoSocialFornecedor = nomeRazaoSocialFornecedor;
    }

    public String getDataResultado() {
        return dataResultado;
    }

    public void setDataResultado(String dataResultado) {
        this.dataResultado = dataResultado;
    }

    public Integer getNumeroLote() {
        return numeroLote;
    }

    public void setNumeroLote(Integer numeroLote) {
        this.numeroLote = numeroLote;
    }

    public Integer getNumeroItem() {
        return numeroItem;
    }

    public void setNumeroItem(Integer numeroItem) {
        this.numeroItem = numeroItem;
    }

    public Integer getNumeroItemComLote() {
        return numeroItemComLote;
    }

    public void setNumeroItemComLote(Integer numeroItemComLote) {
        this.numeroItemComLote = numeroItemComLote;
    }

    public String getSequencialPncp() {
        return sequencialPncp;
    }

    public void setSequencialPncp(String sequencialPncp) {
        this.sequencialPncp = sequencialPncp;
    }
}
