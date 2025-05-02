package br.com.webpublico.negocios.tributario.rowmapper;

import br.com.webpublico.entidades.OpcaoPagamento;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OpcaoPagamentoRowMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        OpcaoPagamento opcaoPagamento = new OpcaoPagamento();
        opcaoPagamento.setId(rs.getLong("ID"));
        opcaoPagamento.setNumeroParcelas(rs.getInt("NUMEROPARCELAS"));

        return opcaoPagamento;
    }
}
