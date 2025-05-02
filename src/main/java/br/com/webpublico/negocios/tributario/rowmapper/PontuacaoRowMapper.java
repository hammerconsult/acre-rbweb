package br.com.webpublico.negocios.tributario.rowmapper;


import br.com.webpublico.entidades.Pontuacao;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PontuacaoRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        Pontuacao pontuacao = new Pontuacao();
        pontuacao.setId(rs.getLong("ID"));
        pontuacao.setIdentificacao(rs.getString("IDENTIFICACAO"));
        return pontuacao;
    }
}
