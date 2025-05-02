package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.GuiaGRU;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GuiaGRURowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        GuiaGRU guiaGru = new GuiaGRU();
        guiaGru.setId(rs.getLong("id"));
        guiaGru.setCodigoRecolhimento(rs.getString("codigorecolhimento"));
        guiaGru.setNumeroReferencia(rs.getString("numeroreferencia"));
        guiaGru.setCompetencia(rs.getDate("competencia"));
        guiaGru.setVencimento(rs.getDate("vencimento"));
        guiaGru.setUgGestao(rs.getString("uggestao"));
        guiaGru.setValorPrincipal(rs.getBigDecimal("valorprincipal"));
        guiaGru.setCodigoBarra(rs.getString("codigobarra"));
        return guiaGru;
    }
}
