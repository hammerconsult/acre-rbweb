package br.com.webpublico.negocios.tributario.rowmapper;

import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.PessoaJuridica;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: java
 * Date: 20/09/13
 * Time: 13:56
 * To change this template use File | Settings | File Templates.
 */
public class PessoaRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        if (resultSet.getString("TIPOPESSOA").equals("F")) {
            PessoaFisica pf = new PessoaFisica();
            pf.setId(resultSet.getLong("ID"));
            pf.setNome(resultSet.getString("NOME"));
            pf.setCpf(resultSet.getString("CPF"));
            return pf;
        } else {
            PessoaJuridica pj = new PessoaJuridica();
            pj.setId(resultSet.getLong("ID"));
            pj.setRazaoSocial(resultSet.getString("RAZAOSOCIAL"));
            pj.setNomeFantasia(resultSet.getString("NOMEFANTASIA"));
            pj.setCnpj(resultSet.getString("CNPJ"));
            return pj;
        }
    }
}
