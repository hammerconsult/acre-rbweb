package br.com.webpublico.nfse.domain.dtos;

public class ArquivoBase64DTO {

    private Long id;
    private String conteudo;

    public ArquivoBase64DTO() {
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public ArquivoBase64DTO(String conteudo) {
        this.conteudo = conteudo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
