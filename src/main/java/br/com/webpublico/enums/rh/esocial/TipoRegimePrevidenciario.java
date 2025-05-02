package br.com.webpublico.enums.rh.esocial;

import br.com.webpublico.webreportdto.dto.rh.TipoRegimePrevidenciarioDTO;
/**
 * Created by William on 11/09/2018.
 */
public enum TipoRegimePrevidenciario {
    RPPS("Rpps", 1, 2, TipoRegimePrevidenciarioDTO.RPPS),
    RGPS("Rgps", 2, 1, TipoRegimePrevidenciarioDTO.RGPS);

    private String descricao;
    private Integer codigo;
    private Integer codigoTipoRegimeEsocial;
    private TipoRegimePrevidenciarioDTO toDto;

    TipoRegimePrevidenciario(String descricao, Integer codigo, Integer codigoTipoRegimeEsocial, TipoRegimePrevidenciarioDTO toDto) {
        this.descricao = descricao;
        this.codigo = codigo;
        this.codigoTipoRegimeEsocial = codigoTipoRegimeEsocial;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Integer getCodigoTipoRegimeEsocial() {
        return codigoTipoRegimeEsocial;
    }

    public void setCodigoTipoRegimeEsocial(Integer codigoTipoRegimeEsocial) {
        this.codigoTipoRegimeEsocial = codigoTipoRegimeEsocial;
    }

    public TipoRegimePrevidenciarioDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
