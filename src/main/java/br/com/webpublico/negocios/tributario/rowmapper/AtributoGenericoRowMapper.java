package br.com.webpublico.negocios.tributario.rowmapper;


import br.com.webpublico.negocios.tributario.auxiliares.AtributoGenerico;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AtributoGenericoRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        AtributoGenerico atr = new AtributoGenerico();
        atr.setIdentificacao(rs.getString("IDENTIFICACAO"));
        atr.setIdValoratributo(rs.getLong("ID"));
        atr.setAtivo(rs.getBoolean("ATIVO"));
        if (rs.getDate("VALORDATA") != null) {
            atr.setValor(rs.getDate("VALORDATA"));
        }
        if (rs.getBigDecimal("VALORDECIMAL") != null) {
            atr.setValor(rs.getBigDecimal("VALORDECIMAL"));
        }
        if (rs.getBigDecimal("VALORINTEIRO") != null) {
            atr.setValor(rs.getBigDecimal("VALORINTEIRO"));
        }
        if (rs.getString("VALORSTRING") != null) {
            atr.setValor(rs.getString("VALORSTRING"));
        }
        if (rs.getString("VALORPOSSIVEL") != null) {
            atr.setValor(rs.getString("VALORPOSSIVEL"));
            atr.setFator(rs.getDouble("FATOR"));
        }
        return atr;
    }
}
