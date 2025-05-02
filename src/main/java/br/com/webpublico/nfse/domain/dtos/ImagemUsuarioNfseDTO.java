package br.com.webpublico.nfse.domain.dtos;

public class ImagemUsuarioNfseDTO {

    private Long id;
    private String conteudo;
    private PessoaNfseDTO pessoa;

    public ImagemUsuarioNfseDTO(String conteudo, PessoaNfseDTO pessoa, Long id) {
        this.conteudo = conteudo;
        this.pessoa = pessoa;
        this.id = id;
    }

    public ImagemUsuarioNfseDTO() {

    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public PessoaNfseDTO getPessoa() {
        return pessoa;
    }

    public void setPessoa(PessoaNfseDTO pessoa) {
        this.pessoa = pessoa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
