package br.com.webpublico.negocios.tributario.rowmapper;


import br.com.webpublico.entidades.Lote;
import br.com.webpublico.entidades.ZonaFiscal;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoteRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        Lote lote = new Lote();
        lote.setId(rs.getLong("ID"));
        lote.setAreaLote(rs.getDouble("AREALOTE"));
        lote.setCodigoLote(rs.getString("CODIGOLOTE"));
        lote.setCodigoQuadra(rs.getString("QUADRA"));
        lote.setCodigoSetor(rs.getString("SETOR"));
        lote.setZonaFiscal(new ZonaFiscal(rs.getLong("IDZONAFISCAL"),
            rs.getBigDecimal("INDICE"),
            rs.getBigDecimal("VALORUNITARIOTERRENO")));
        return lote;
    }
}
