package br.com.webpublico.nfse.domain.dtos;

public class ArquivoParteNfseDTO implements br.com.webpublico.nfse.domain.dtos.NfseDTO {

    private Long id;
    private byte[] dados;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getDados() {
        return dados;
    }

    public void setDados(byte[] dados) {
        this.dados = dados;
    }
}
