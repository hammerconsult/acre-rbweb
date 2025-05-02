package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.negocios.tributario.rowmapper.CadastroEconomicoRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;

@Repository(value = "voRelatorioGenericoDAO")
public class JdbcVORelatorioGenericoDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcVORelatorioGenericoDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

}
