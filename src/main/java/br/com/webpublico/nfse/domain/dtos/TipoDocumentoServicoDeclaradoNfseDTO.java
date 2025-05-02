package br.com.webpublico.nfse.domain.dtos;

public class TipoDocumentoServicoDeclaradoNfseDTO implements NfseDTO {


    private Long id;

    private String descricao;

    private String serie;

    private String subSerie;

    private Boolean ativo;

    public TipoDocumentoServicoDeclaradoNfseDTO() {
        super();
        ativo = Boolean.TRUE;
    }

    public TipoDocumentoServicoDeclaradoNfseDTO(Long id, String descricao, String serie, String subSerie, Boolean ativo) {
        this.id = id;
        this.descricao = descricao;
        this.serie = serie;
        this.subSerie = subSerie;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getSubSerie() {
        return subSerie;
    }

    public void setSubSerie(String subSerie) {
        this.subSerie = subSerie;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
