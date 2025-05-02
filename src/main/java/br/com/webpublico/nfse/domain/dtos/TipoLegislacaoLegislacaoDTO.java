package br.com.webpublico.nfse.domain.dtos;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wellington on 06/09/17.
 */
public class TipoLegislacaoLegislacaoDTO implements Serializable {
    private TipoLegislacaoDTO tipoLegislacaoDTO;
    private List<br.com.webpublico.nfse.domain.dtos.LegislacaoDTO> legislacoesDTO;

    public TipoLegislacaoLegislacaoDTO() {
    }

    public TipoLegislacaoDTO getTipoLegislacaoDTO() {
        return tipoLegislacaoDTO;
    }

    public void setTipoLegislacaoDTO(TipoLegislacaoDTO tipoLegislacaoDTO) {
        this.tipoLegislacaoDTO = tipoLegislacaoDTO;
    }

    public List<br.com.webpublico.nfse.domain.dtos.LegislacaoDTO> getLegislacoesDTO() {
        return legislacoesDTO;
    }

    public void setLegislacoesDTO(List<br.com.webpublico.nfse.domain.dtos.LegislacaoDTO> legislacoesDTO) {
        this.legislacoesDTO = legislacoesDTO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TipoLegislacaoLegislacaoDTO that = (TipoLegislacaoLegislacaoDTO) o;

        return tipoLegislacaoDTO != null ? tipoLegislacaoDTO.equals(that.tipoLegislacaoDTO) : that.tipoLegislacaoDTO == null;
    }

    @Override
    public int hashCode() {
        return tipoLegislacaoDTO != null ? tipoLegislacaoDTO.hashCode() : 0;
    }
}
