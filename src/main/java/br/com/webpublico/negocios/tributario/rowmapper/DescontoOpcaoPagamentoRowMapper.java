package br.com.webpublico.negocios.tributario.rowmapper;

import br.com.webpublico.entidades.DescontoOpcaoPagamento;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DescontoOpcaoPagamentoRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        DescontoOpcaoPagamento descontoOpcaoPagamento = new DescontoOpcaoPagamento();
        descontoOpcaoPagamento.setId(rs.getLong("ID"));
        descontoOpcaoPagamento.setPercentualDescontoAdimplente(rs.getBigDecimal("PERCENTUALDESCONTOADIMPLENTE"));
        descontoOpcaoPagamento.setPercentualDescontoInadimplente(rs.getBigDecimal("PERCENTUALDESCONTOINADIMPLENTE"));

        return descontoOpcaoPagamento;
    }
}
