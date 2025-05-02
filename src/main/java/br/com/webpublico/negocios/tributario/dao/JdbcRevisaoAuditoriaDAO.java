package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;

@Repository(value = "revisaoAuditoriaDAO")
public class JdbcRevisaoAuditoriaDAO extends JdbcDaoSupport implements Serializable {
    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcRevisaoAuditoriaDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public Long newRevisaoAuditoria() {
        Long id = geradorDeIds.getProximoId();
        getJdbcTemplate().update(" insert into revisaoauditoria (id, datahora, ip, usuario) " +
            " values (?, current_date, ?, ?) ", preparedStatement -> {
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, SistemaFacade.obtemIp());
            preparedStatement.setString(3, SistemaFacade.obtemLogin());
        });
        return id;
    }
}
