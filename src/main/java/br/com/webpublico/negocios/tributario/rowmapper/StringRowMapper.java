package br.com.webpublico.negocios.tributario.rowmapper;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.PessoaJuridica;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: java
 * Date: 20/09/13
 * Time: 08:29
 * To change this template use File | Settings | File Templates.
 */
public class StringRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        return rs.getString("value");
    }
}
