package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.domain.dtos.enums.TipoAnexoTipoManualDTO;

import java.io.Serializable;

/**
 * Created by william on 05/09/17.
 */
public class TipoManualDTO implements Serializable {
    private Long id;
    private String descricao;
    private Integer ordem;
    private Boolean habilitarExibicao;
    private TipoAnexoTipoManualDTO tipoAnexo;

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

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Boolean getHabilitarExibicao() {
        return habilitarExibicao;
    }

    public void setHabilitarExibicao(Boolean habilitarExibicao) {
        this.habilitarExibicao = habilitarExibicao;
    }

    public TipoAnexoTipoManualDTO getTipoAnexo() {
        return tipoAnexo;
    }

    public void setTipoAnexo(TipoAnexoTipoManualDTO tipoAnexo) {
        this.tipoAnexo = tipoAnexo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TipoManualDTO that = (TipoManualDTO) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
