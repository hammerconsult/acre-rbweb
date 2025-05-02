package br.com.webpublico.enums;


import br.com.webpublico.nfse.domain.dtos.enums.TipoPorteNfeDTO;
import br.com.webpublico.tributario.interfaces.NfseEnumDTO;
import com.google.common.collect.Lists;

import java.util.List;

public enum TipoPorte implements NfseEnumDTO {
    MICRO("Microempresa", true, br.com.webpublico.pessoa.enumeration.TipoPorte.MICRO, TipoPorteNfeDTO.MICRO),
    PEQUENA("Empresa de Pequeno Porte", true, br.com.webpublico.pessoa.enumeration.TipoPorte.PEQUENA, TipoPorteNfeDTO.PEQUENA),
    MEDIA("Empresa de Médio Porte", false, br.com.webpublico.pessoa.enumeration.TipoPorte.MEDIA, TipoPorteNfeDTO.MEDIA),
    GRANDE("Empresa de Grande Porte", false, br.com.webpublico.pessoa.enumeration.TipoPorte.GRANDE, TipoPorteNfeDTO.GRANDE),
    OUTRO("Outro", true, br.com.webpublico.pessoa.enumeration.TipoPorte.OUTRO, null);

    private String descricao;
    private boolean visivel;
    private br.com.webpublico.pessoa.enumeration.TipoPorte porteDTO;
    private TipoPorteNfeDTO nfeDTO;

    public String getDescricao() {
        return descricao;
    }

    public boolean isVisivel() {
        return visivel;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public br.com.webpublico.pessoa.enumeration.TipoPorte getPorteDTO() {
        return porteDTO;
    }

    public TipoPorteNfeDTO getNfeDTO() {
        return nfeDTO;
    }

    private TipoPorte(String descricao, boolean visivel, br.com.webpublico.pessoa.enumeration.TipoPorte porteDTO, TipoPorteNfeDTO nfeDTO) {
        this.descricao = descricao;
        this.visivel = visivel;
        this.porteDTO = porteDTO;
        this.nfeDTO = nfeDTO;
    }

    public static List<TipoPorte> getTiposVisiveis() {
        List<TipoPorte> retorno = Lists.newArrayList();
        for (TipoPorte porte : TipoPorte.values()) {
            if (porte.isVisivel()) {
                retorno.add(porte);
            }
        }
        return retorno;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public static TipoPorte getTipoPortePorPorteDTO(br.com.webpublico.pessoa.enumeration.TipoPorte porteDTO) {
        for (TipoPorte tipoPorte : TipoPorte.values()) {
            if (tipoPorte.getPorteDTO().equals(porteDTO)) {
                return tipoPorte;
            }
        }
        return null;
    }
}

