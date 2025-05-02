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
public class CadastroEconomicoRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        CadastroEconomico cadastroEconomico = new CadastroEconomico();
        cadastroEconomico.setId(rs.getLong("id"));
        cadastroEconomico.setInscricaoCadastral(rs.getString("inscricaoCadastral"));
        if(rs.getString("tipoPessoa").equals("F")){
            PessoaFisica pessoaFisica = new PessoaFisica();
            pessoaFisica.setNome(rs.getString("nomePessoa"));
            pessoaFisica.setCpf(rs.getString("cpfCnpjPessoa"));
            cadastroEconomico.setPessoa(pessoaFisica);
        } else {
            PessoaJuridica pessoaJuridica = new PessoaJuridica();
            pessoaJuridica.setRazaoSocial(rs.getString("nomePessoa"));
            pessoaJuridica.setCnpj(rs.getString("cpfCnpjPessoa"));
            cadastroEconomico.setPessoa(pessoaJuridica);
        }
        return cadastroEconomico;
    }
}
