package br.com.webpublico.nfse.domain.dtos.enums;

import br.com.webpublico.tributario.interfaces.NfseEnumDTO;
public enum TipoPorteNfeDTO implements NfseEnumDTO {
    MICRO("Microempresa"),
    PEQUENA("Empresa de Pequeno Porte"),
    MEDIA("Empresa de Médio Porte"),
    GRANDE("Empresa de Grande Porte"),
    OUTRO("Outro");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoPorteNfeDTO(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
