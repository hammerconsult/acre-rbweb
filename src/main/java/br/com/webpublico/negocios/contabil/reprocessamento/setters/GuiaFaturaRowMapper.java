package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.GuiaFatura;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GuiaFaturaRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        GuiaFatura guiaFatura = new GuiaFatura();
        guiaFatura.setId(rs.getLong("ID"));
        guiaFatura.setCodigoBarra(rs.getString("CODIGOBARRA"));
        guiaFatura.setDataVencimento(rs.getDate("DATAVENCIMENTO"));
        guiaFatura.setValorNominal(rs.getBigDecimal("VALORNOMINAL"));
        guiaFatura.setValorDescontos(rs.getBigDecimal("VALORDESCONTOS"));
        guiaFatura.setValorJuros(rs.getBigDecimal("VALORJUROS"));
        return guiaFatura;
    }
}
