package br.com.webpublico.nfse.domain.dtos;

public class ProdutoServicoBancarioNfseDTO {

    private Long id;
    private GrupoProdutoServicoBancarioNfseDTO grupo;
    private Integer codigo;
    private String descricao;
    private Boolean obrigaDescricaoComplementar;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GrupoProdutoServicoBancarioNfseDTO getGrupo() {
        return grupo;
    }

    public void setGrupo(GrupoProdutoServicoBancarioNfseDTO grupo) {
        this.grupo = grupo;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getObrigaDescricaoComplementar() {
        return obrigaDescricaoComplementar;
    }

    public void setObrigaDescricaoComplementar(Boolean obrigaDescricaoComplementar) {
        this.obrigaDescricaoComplementar = obrigaDescricaoComplementar;
    }
}
