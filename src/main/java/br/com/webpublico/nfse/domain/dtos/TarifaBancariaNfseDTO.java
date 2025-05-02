package br.com.webpublico.nfse.domain.dtos;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TarifaBancariaNfseDTO implements RowMapper<TarifaBancariaNfseDTO> {

    private Long id;
    private Integer codigo;
    private String descricao;
    private GrupoTarifaBancariaNfseDTO grupo;
    private PeriodicidadeTarifaBancariaNfseDTO periodicidade;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public GrupoTarifaBancariaNfseDTO getGrupo() {
        return grupo;
    }

    public void setGrupo(GrupoTarifaBancariaNfseDTO grupo) {
        this.grupo = grupo;
    }

    public PeriodicidadeTarifaBancariaNfseDTO getPeriodicidade() {
        return periodicidade;
    }

    public void setPeriodicidade(PeriodicidadeTarifaBancariaNfseDTO periodicidade) {
        this.periodicidade = periodicidade;
    }

    @Override
    public TarifaBancariaNfseDTO mapRow(ResultSet resultSet, int i) throws SQLException {
        TarifaBancariaNfseDTO dto = new TarifaBancariaNfseDTO();
        dto.setId(resultSet.getLong("ID"));
        dto.setCodigo(resultSet.getInt("CODIGO"));
        dto.setDescricao(resultSet.getString("DESCRICAO"));
        dto.setGrupo(GrupoTarifaBancariaNfseDTO.valueOf(resultSet.getString("GRUPO")));
        dto.setPeriodicidade(PeriodicidadeTarifaBancariaNfseDTO.valueOf(resultSet.getString("PERIODICIDADE")));
        return dto;
    }
}
