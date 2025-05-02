package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.EnderecoCorreio;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.repositorios.jdbc.util.JDBCUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.util.List;
import java.sql.SQLException;

@Repository(value = "enderecoCorreioDAO")
public class JdbcEnderecoCorreioDAO extends JdbcDaoSupport implements Serializable {
    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcEnderecoCorreioDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void insert(Long rev, List<EnderecoCorreio> enderecosCorreio) {
        insertEnderecoCorreio(enderecosCorreio);
        insertEnderecoCorreioAud(rev, 0L, enderecosCorreio);
    }

    private void insertEnderecoCorreio(List<EnderecoCorreio> enderecosCorreio) {
        getJdbcTemplate().batchUpdate(" insert into enderecocorreio (id, bairro, cep, complemento, localidade, " +
            " logradouro, numero, tipoendereco, uf, principal, migracaochave, tipologradouro) " +
            " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                EnderecoCorreio enderecoCorreio = enderecosCorreio.get(i);
                enderecoCorreio.setId(geradorDeIds.getProximoId());
                JDBCUtil.setLong(preparedStatement, 1, enderecoCorreio.getId());
                JDBCUtil.setString(preparedStatement, 2, enderecoCorreio.getBairro());
                JDBCUtil.setString(preparedStatement, 3, enderecoCorreio.getCep());
                JDBCUtil.setString(preparedStatement, 4, enderecoCorreio.getComplemento());
                JDBCUtil.setString(preparedStatement, 5, enderecoCorreio.getLocalidade());
                JDBCUtil.setString(preparedStatement, 6, enderecoCorreio.getLogradouro());
                JDBCUtil.setString(preparedStatement, 7, enderecoCorreio.getNumero());
                JDBCUtil.setEnum(preparedStatement, 8, enderecoCorreio.getTipoEndereco());
                JDBCUtil.setString(preparedStatement, 9, enderecoCorreio.getUf());
                JDBCUtil.setBoolean(preparedStatement, 10, enderecoCorreio.getPrincipal());
                JDBCUtil.setString(preparedStatement, 11, enderecoCorreio.getMigracaoChave());
                JDBCUtil.setEnum(preparedStatement, 12, enderecoCorreio.getTipoLogradouro());
            }

            @Override
            public int getBatchSize() {
                return enderecosCorreio.size();
            }
        });
        getJdbcTemplate().batchUpdate(" insert into pessoa_enderecocorreio (pessoa_id, enderecoscorreio_id) " +
            " values (?, ?) ", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                EnderecoCorreio enderecoCorreio = enderecosCorreio.get(i);
                JDBCUtil.setLong(preparedStatement, 1, enderecoCorreio.getPessoaFisica().getId());
                JDBCUtil.setLong(preparedStatement, 2, enderecoCorreio.getId());
            }

            @Override
            public int getBatchSize() {
                return enderecosCorreio.size();
            }
        });
    }

    public void insertEnderecoCorreioAud(Long rev, Long revType, List<EnderecoCorreio> enderecosCorreio) {
        getJdbcTemplate().batchUpdate(" insert into enderecocorreio_aud (id, rev, revtype, bairro, cep, " +
            " complemento, localidade, logradouro, numero, tipoendereco, uf, principal, migracaochave, tipologradouro) " +
            " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                EnderecoCorreio enderecoCorreio = enderecosCorreio.get(i);
                JDBCUtil.setLong(preparedStatement, 1, enderecoCorreio.getId());
                JDBCUtil.setLong(preparedStatement, 2, rev);
                JDBCUtil.setLong(preparedStatement, 3, revType);
                JDBCUtil.setString(preparedStatement, 4, enderecoCorreio.getBairro());
                JDBCUtil.setString(preparedStatement, 5, enderecoCorreio.getCep());
                JDBCUtil.setString(preparedStatement, 6, enderecoCorreio.getComplemento());
                JDBCUtil.setString(preparedStatement, 7, enderecoCorreio.getLocalidade());
                JDBCUtil.setString(preparedStatement, 8, enderecoCorreio.getLogradouro());
                JDBCUtil.setString(preparedStatement, 9, enderecoCorreio.getNumero());
                JDBCUtil.setEnum(preparedStatement, 10, enderecoCorreio.getTipoEndereco());
                JDBCUtil.setString(preparedStatement, 11, enderecoCorreio.getUf());
                JDBCUtil.setBoolean(preparedStatement, 12, enderecoCorreio.getPrincipal());
                JDBCUtil.setString(preparedStatement, 13, enderecoCorreio.getMigracaoChave());
                JDBCUtil.setEnum(preparedStatement, 14, enderecoCorreio.getTipoLogradouro());

            }

            @Override
            public int getBatchSize() {
                return enderecosCorreio.size();
            }
        });
    }
}
