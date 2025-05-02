package br.com.webpublico.negocios.tributario.rowmapper;


import br.com.webpublico.entidades.ServicoUrbano;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ServicoRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        ServicoUrbano servico = new ServicoUrbano();
        servico.setId(rs.getLong("ID"));
        servico.setDataRegistro(rs.getDate("DATAREGISTRO"));
        servico.setNome(rs.getString("NOME"));
        servico.setIdentificacao(rs.getString("IDENTIFICACAO"));
        servico.setCodigo(rs.getLong("CODIGO"));
        return servico;
    }
}
