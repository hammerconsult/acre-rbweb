package br.com.webpublico.negocios.tributario.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;

@Repository(value = "arrecadacaoDAO")
public class JdbcArrecadacaoDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcArrecadacaoDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void updateIntegracaoArrecadacaoLoteBaixa(Long idLoteBaixa) {
        String sql = " update LoteBaixa set integracaoArrecadacao = 1 where id = ? ";
        getJdbcTemplate().update(sql, idLoteBaixa);
    }

    public void updateIntegracaoEstornoLoteBaixa(Long idLoteBaixa) {
        String sql = " update LoteBaixa set integracaoEstorno = 1 where id = ? ";
        getJdbcTemplate().update(sql, idLoteBaixa);
    }
}
