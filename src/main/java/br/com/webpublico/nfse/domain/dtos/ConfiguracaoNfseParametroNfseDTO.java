package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.domain.dtos.enums.TipoParametroNfseDTO;

public class ConfiguracaoNfseParametroNfseDTO implements NfseDTO {

    private Long id;
    private TipoParametroNfseDTO tipoParametroNfseDTO;
    private String valor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoParametroNfseDTO getTipoParametroNfseDTO() {
        return tipoParametroNfseDTO;
    }

    public void setTipoParametroNfseDTO(TipoParametroNfseDTO tipoParametroNfseDTO) {
        this.tipoParametroNfseDTO = tipoParametroNfseDTO;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
