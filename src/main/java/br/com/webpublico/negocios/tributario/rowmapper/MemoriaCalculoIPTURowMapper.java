/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios.tributario.rowmapper;

import br.com.webpublico.entidades.EventoCalculo;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import br.com.webpublico.entidades.MemoriaCaluloIPTU;

/**
 *
 * @author Wellington
 */
public class MemoriaCalculoIPTURowMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        MemoriaCaluloIPTU memoriaCalculoIPTU = new MemoriaCaluloIPTU();
        memoriaCalculoIPTU.setId(rs.getLong("ID"));
        memoriaCalculoIPTU.setValor(rs.getBigDecimal("VALOR"));
        memoriaCalculoIPTU.setEvento(new EventoCalculo());
        memoriaCalculoIPTU.getEvento().setId(rs.getLong("EVENTO_ID"));
        memoriaCalculoIPTU.getEvento().setDescricao(rs.getString("EVT_DESCRICAO"));
        return memoriaCalculoIPTU;
    }

}
