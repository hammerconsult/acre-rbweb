package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.comum.PessoaFisicaRFB;
import br.com.webpublico.negocios.tributario.setter.PessoaFisicaRFBSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository(value = "pessoaFisicaRFBDAO")
public class JdbcPessoaFisicaRFBDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcPessoaFisicaRFBDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void inserir(List<PessoaFisicaRFB> pessoas) {
        getJdbcTemplate().batchUpdate("insert into pessoafisicarfb (id, cpf, nome, nomemae, datanascimento, " +
            "                             bairro, tipologradouro, logradouro, numero, " +
            "                             complemento, cep, municipio, ddd, telefone, email, situacao) " +
            "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ", new PessoaFisicaRFBSetter(pessoas, geradorDeIds));
    }

    public void deleteAll() {
        getJdbcTemplate().execute("delete from pessoafisicarfb");
    }

    public void update(List<PessoaFisicaRFB> pessoasFisicasRFB) {
        getJdbcTemplate().batchUpdate(" update pessoafisicarfb set situacao = ? where id = ? ",
            new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                    PessoaFisicaRFB pessoaFisicaRFB = pessoasFisicasRFB.get(i);
                    preparedStatement.setString(1, pessoaFisicaRFB.getSituacao().name());
                    preparedStatement.setLong(2, pessoaFisicaRFB.getId());
                }

                @Override
                public int getBatchSize() {
                    return pessoasFisicasRFB.size();
                }
            });
    }
}
