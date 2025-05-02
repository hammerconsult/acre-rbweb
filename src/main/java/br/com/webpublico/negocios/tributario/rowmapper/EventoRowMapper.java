package br.com.webpublico.negocios.tributario.rowmapper;


import br.com.webpublico.entidades.EventoCalculo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EventoRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        EventoCalculo evento = new EventoCalculo();
        evento.setId(rs.getLong("ID"));
        evento.setDescricao(rs.getString("DESCRICAO"));
        evento.setIdentificacao(rs.getString("IDENTIFICACAO"));
        evento.setRegra(rs.getString("REGRA"));
        evento.setTipoLancamento(EventoCalculo.TipoLancamento.valueOf(rs.getString("TIPOLANCAMENTO")));
        evento.setIdentificadorParaDesconto(rs.getString("DESCONTO"));
        return evento;
    }
}
