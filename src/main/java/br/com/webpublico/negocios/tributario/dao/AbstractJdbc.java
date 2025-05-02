package br.com.webpublico.negocios.tributario.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.Serializable;

public abstract class AbstractJdbc extends JdbcDaoSupport implements Serializable {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    private void init() {
        setDataSource(dataSource);
    }
}
