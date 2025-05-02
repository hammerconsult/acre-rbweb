package br.com.webpublico.negocios.tributario.rowmapper;

import br.com.webpublico.entidades.CadastroRural;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: java
 * Date: 20/09/13
 * Time: 10:57
 * To change this template use File | Settings | File Templates.
 */
public class CadastroRuralRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        CadastroRural cadastroRural = new CadastroRural();
        cadastroRural.setId(rs.getLong("id"));
        cadastroRural.setNomePropriedade(rs.getString("nomePropriedade"));
        cadastroRural.setNumeroIncra(rs.getString("numeroIncra"));
        cadastroRural.setCodigo(rs.getLong("codigo"));
        return cadastroRural;
    }
}
