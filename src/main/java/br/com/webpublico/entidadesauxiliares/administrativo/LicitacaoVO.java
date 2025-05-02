package br.com.webpublico.entidadesauxiliares.administrativo;

public class LicitacaoVO {
    private Long id;
    private Integer numero;
    private Integer numeroLicitacao;
    private String processoDeCompra;
    private String comissao;
    private String unidadeAdministrativa;

    public LicitacaoVO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getNumeroLicitacao() {
        return numeroLicitacao;
    }

    public void setNumeroLicitacao(Integer numeroLicitacao) {
        this.numeroLicitacao = numeroLicitacao;
    }

    public String getProcessoDeCompra() {
        return processoDeCompra;
    }

    public void setProcessoDeCompra(String processoDeCompra) {
        this.processoDeCompra = processoDeCompra;
    }

    public String getComissao() {
        return comissao;
    }

    public void setComissao(String comissao) {
        this.comissao = comissao;
    }

    public String getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(String unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }
}
