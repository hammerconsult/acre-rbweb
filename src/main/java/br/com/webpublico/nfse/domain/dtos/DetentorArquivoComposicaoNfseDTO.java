package br.com.webpublico.nfse.domain.dtos;

import java.util.List;

public class DetentorArquivoComposicaoNfseDTO implements NfseDTO {

    private Long id;
    private List<ArquivoComposicaoNfseDTO> arquivos;
    private ArquivoComposicaoNfseDTO arquivo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ArquivoComposicaoNfseDTO> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<ArquivoComposicaoNfseDTO> arquivos) {
        this.arquivos = arquivos;
    }

    public ArquivoComposicaoNfseDTO getArquivo() {
        return arquivo;
    }

    public void setArquivo(ArquivoComposicaoNfseDTO arquivo) {
        this.arquivo = arquivo;
    }
}
