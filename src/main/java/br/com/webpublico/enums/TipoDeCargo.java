package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.rh.TipoDeCargoDTO;

/**
 * Created by Desenvolvimento on 07/04/2017.
 */
public enum TipoDeCargo {

    OUTROS("Outros", "2", "OUTROS PROFISSIONAIS", "16", "Profissionais que atuam na realização das atividades requeridos nos ambientes de secretaria, de manutenção em geral.", TipoDeCargoDTO.OUTROS),
    PROFESSOR("Professor", "1", "PROFISSIONAIS DO MAGISTÉRIO", "2", "Docente habilitado em curso de pedagogia", TipoDeCargoDTO.PROFESSOR);

    private String descricao;
    private String codigoCategoria;
    private String categoriaProfissionalSiope;
    private String codigoServicoSiope;
    private String tipoServicoSiope;
    private TipoDeCargoDTO toDto;

    TipoDeCargo(String descricao, String codigoCategoria, String categoriaProfissionalSiope, String codigoServicoSiope, String tipoServicoSiope, TipoDeCargoDTO toDto) {
        this.descricao = descricao;
        this.codigoCategoria = codigoCategoria;
        this.categoriaProfissionalSiope = categoriaProfissionalSiope;
        this.codigoServicoSiope = codigoServicoSiope;
        this.tipoServicoSiope= tipoServicoSiope;
        this.toDto = toDto;
    }

    public String getCodigoCategoria() {
        return codigoCategoria;
    }

    public void setCodigoCategoria(String codigoCategoria) {
        this.codigoCategoria = codigoCategoria;
    }

    public String getCategoriaProfissionalSiope() {
        return categoriaProfissionalSiope;
    }

    public void setCategoriaProfissionalSiope(String categoriaProfissionalSiope) {
        this.categoriaProfissionalSiope = categoriaProfissionalSiope;
    }

    public String getCodigoServicoSiope() {
        return codigoServicoSiope;
    }

    public void setCodigoServicoSiope(String codigoServicoSiope) {
        this.codigoServicoSiope = codigoServicoSiope;
    }

    public String getTipoServicoSiope() {
        return tipoServicoSiope;
    }

    public void setTipoServicoSiope(String tipoServicoSiope) {
        this.tipoServicoSiope = tipoServicoSiope;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoDeCargoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
