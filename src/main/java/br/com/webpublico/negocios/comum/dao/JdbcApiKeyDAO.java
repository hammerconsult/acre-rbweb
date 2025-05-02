package br.com.webpublico.negocios.comum.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;

@Repository
public class JdbcApiKeyDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcApiKeyDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }


    public boolean hasApiKeyRegistered(String apiKey) {
        return getJdbcTemplate().queryForRowSet(" select apikey from apikey where apikey = ? ",
            new Object[]{apiKey}).next();
    }

}
