package br.com.webpublico.negocios.tributario.rowmapper;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NotaFiscalRowMapper implements RowMapper {

    private Long id;
    private Long numero;

    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        NotaFiscalRowMapper mapper = new NotaFiscalRowMapper();
        mapper.setId(resultSet.getLong("ID"));
        mapper.setNumero(resultSet.getLong("NUMERO"));
        return mapper;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }
}
