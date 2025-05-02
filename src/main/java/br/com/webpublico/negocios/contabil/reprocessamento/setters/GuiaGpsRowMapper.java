package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.GuiaGPS;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GuiaGpsRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        GuiaGPS guiaGPS = new GuiaGPS();
        guiaGPS.setId(rs.getLong("ID"));
        guiaGPS.setCodigoReceitaTributo(rs.getString("CODIGORECEITATRIBUTO"));
        guiaGPS.setCodigoIdentificacaoTributo(rs.getString("CODIGOIDENTIFICACAOTRIBUTO"));
        guiaGPS.setPeriodoCompetencia(rs.getDate("PERIODOCOMPETENCIA"));
        guiaGPS.setValorPrevistoINSS(rs.getBigDecimal("VALORPREVISTOINSS"));
        guiaGPS.setValorOutrasEntidades(rs.getBigDecimal("VALOROUTRASENTIDADES"));
        guiaGPS.setAtualizacaoMonetaria(rs.getBigDecimal("ATUALIZACAOMONETARIA"));
        return guiaGPS;
    }
}
