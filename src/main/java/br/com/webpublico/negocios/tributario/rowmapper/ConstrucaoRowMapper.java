package br.com.webpublico.negocios.tributario.rowmapper;


import br.com.webpublico.entidades.Construcao;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ConstrucaoRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        Construcao construcao = new Construcao();
        construcao.setAnoConstrucao(rs.getDate("ANOCONSTRUCAO"));
        construcao.setAreaConstruida(rs.getDouble("AREACONSTRUIDA"));
        construcao.setAreaTotalConstruida(rs.getDouble("AREATOTAL"));
        construcao.setCodigo(rs.getInt("CODIGO"));
        construcao.setDataAlvara(rs.getDate("DATAALVARA"));
        construcao.setDataHabitese(rs.getDate("DATAHABITESE"));
        construcao.setDataProtocolo(rs.getDate("DATAPROTOCOLO"));
        construcao.setDescricao(rs.getString("DESCRICAO"));
        construcao.setHabitese(rs.getString("HABITESE"));
        construcao.setId(rs.getLong("ID"));
        construcao.setNumeroAlvara(rs.getString("NUMEROALVARA"));
        construcao.setNumeroProtocolo(rs.getString("NUMEROPROTOCOLO"));
        construcao.setNumeroProtocolo(rs.getString("NUMEROPROTOCOLO"));
        construcao.setQuantidadePavimentos(rs.getInt("QUANTIDADEPAVIMENTOS"));
        return construcao;
    }
}
