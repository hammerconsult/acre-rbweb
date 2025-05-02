package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.EnderecoCorreio;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.EnderecoPessoaRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 29/10/14
 * Time: 09:34
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class JdbcEnderecoPessoa extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcEnderecoPessoa(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public List<EnderecoCorreio> buscarEnderecoPorPessoa(Long pessoa_id) {
        String sql = "select endereco_id, " +
            "       bairro, " +
            "       logradouro, " +
            "       cep, " +
            "       complemento, " +
            "       localidade, " +
            "       numero, " +
            "       tipoendereco, " +
            "       tipologradouro, " +
            "       uf " +
            "from vwenderecopessoa " +
            "where pessoa_id = ?";
        List<EnderecoCorreio> enderecoCorreios = getJdbcTemplate().query(sql, new Object[]{pessoa_id}, new EnderecoPessoaRowMapper());
        if (!enderecoCorreios.isEmpty()) {
            return enderecoCorreios;
        } else {
            return null;
        }
    }
}
