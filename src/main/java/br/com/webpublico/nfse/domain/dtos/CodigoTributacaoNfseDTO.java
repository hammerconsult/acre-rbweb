package br.com.webpublico.nfse.domain.dtos;

public class CodigoTributacaoNfseDTO {

    private Long id;
    private String codigo;
    private String descricao;
    private ServicoNfseDTO servico;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public ServicoNfseDTO getServico() {
        return servico;
    }

    public void setServico(ServicoNfseDTO servico) {
        this.servico = servico;
    }
}
