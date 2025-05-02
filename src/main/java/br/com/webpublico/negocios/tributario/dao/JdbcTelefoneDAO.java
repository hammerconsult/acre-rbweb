package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.DateUtils;
import br.com.webpublico.entidades.Telefone;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.repositorios.jdbc.util.JDBCUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository(value = "telefoneDAO")
public class JdbcTelefoneDAO extends JdbcDaoSupport implements Serializable {
    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcTelefoneDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void insert(Long rev, List<Telefone> telefones) {
        insertTelefone(telefones);
        insertTelefoneAud(rev, 0L, telefones);
    }

    private void insertTelefone(List<Telefone> telefones) {
        getJdbcTemplate().batchUpdate(" insert into telefone (id, dataregistro, telefone, tipofone, " +
            " pessoa_id, principal, pessoacontato) " +
            " values (?, ?, ?, ?, ?, ?, ?) ", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                Telefone telefone = telefones.get(i);
                telefone.setId(geradorDeIds.getProximoId());
                JDBCUtil.setLong(preparedStatement,1, telefone.getId());
                JDBCUtil.setDate(preparedStatement,2, DateUtils.toSQLDate(telefone.getDataRegistro()));
                JDBCUtil.setString(preparedStatement,3, telefone.getTelefone());
                JDBCUtil.setString(preparedStatement,4, telefone.getTipoFone().name());
                JDBCUtil.setLong(preparedStatement,5, telefone.getPessoa().getId());
                JDBCUtil.setBoolean(preparedStatement,6, telefone.getPrincipal());
                JDBCUtil.setString(preparedStatement,7, telefone.getPessoaContato());
            }

            @Override
            public int getBatchSize() {
                return telefones.size();
            }
        });
    }

    public void insertTelefoneAud(Long rev, Long revType, List<Telefone> telefones) {
        getJdbcTemplate().batchUpdate(" insert into telefone_aud (id, rev, revtype, dataregistro, telefone, " +
            " tipofone, pessoa_id, principal, pessoacontato) " +
            " values (?, ?, ?, ?, ?, ?, ?, ?, ?) ", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                Telefone telefone = telefones.get(i);
                JDBCUtil.setLong(preparedStatement,1, telefone.getId());
                JDBCUtil.setLong(preparedStatement,2, rev);
                JDBCUtil.setLong(preparedStatement,3, revType);
                JDBCUtil.setDate(preparedStatement,4, DateUtils.toSQLDate(telefone.getDataRegistro()));
                JDBCUtil.setString(preparedStatement,5, telefone.getTelefone());
                JDBCUtil.setString(preparedStatement,6, telefone.getTipoFone().name());
                JDBCUtil.setLong(preparedStatement,7, telefone.getPessoa().getId());
                JDBCUtil.setBoolean(preparedStatement,8, telefone.getPrincipal());
                JDBCUtil.setString(preparedStatement,9, telefone.getPessoaContato());
            }

            @Override
            public int getBatchSize() {
                return telefones.size();
            }
        });
    }
}
