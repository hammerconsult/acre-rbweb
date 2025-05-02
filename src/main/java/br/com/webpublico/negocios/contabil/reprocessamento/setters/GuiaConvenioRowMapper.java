package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.GuiaConvenio;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GuiaConvenioRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        GuiaConvenio guiaConvenio = new GuiaConvenio();
        guiaConvenio.setId(rs.getLong("ID"));
        guiaConvenio.setCodigoBarra(rs.getString("CODIGOBARRA"));
        guiaConvenio.setValor(rs.getBigDecimal("VALOR"));
        return guiaConvenio;
    }
}
